package hu.bme.agocs.videoeditor.videoeditor.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaActionSound;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Filterable;

import com.github.hiteshsondhi88.libffmpeg.FileUtils;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegExecutor;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobe;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeExecutor;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeLoadBinaryResponseHandler;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.FFmpegInfo;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.MediaType;
import hu.bme.agocs.videoeditor.videoeditor.data.event.ProgressEvent;
import hu.bme.agocs.videoeditor.videoeditor.data.utils.RetryWhenExceptionWithDelay;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import timber.log.Timber;

/**
 * Created by Agócs Tamás on 2015. 11. 27..
 */
public class VideoManager {

    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private static VideoManager instance;

    private boolean isInitialized = false;
    private boolean isFFmpegLoaded = false;
    private boolean isFFprobeLoaded = false;

    public static VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }

    private VideoManager() {
        ffmpeg = FFmpeg.getInstance(VideoEditor.getContext());
        ffprobe = FFprobe.getInstance(VideoEditor.getContext());
    }

    public void init() {
        try {
            ffmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Timber.d("FFmpeg load failed.");
                    isFFmpegLoaded = false;
                }

                @Override
                public void onSuccess() {
                    Timber.d("FFmpeg loaded successfully.");
                    isFFmpegLoaded = true;
                }

                @Override
                public void onStart() {
                    Timber.d("FFmpeg load started.");
                }

                @Override
                public void onFinish() {
                    Timber.d("FFmpeg load finished.");
                }
            });
            ffprobe.loadBinary(new FFprobeLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Timber.d("FFprobe load failed.");
                    isFFprobeLoaded = false;
                }

                @Override
                public void onSuccess() {
                    Timber.d("FFprobe loaded successfully.");
                    isFFprobeLoaded = true;
                }

                @Override
                public void onStart() {
                    Timber.d("FFprobe load started.");
                }

                @Override
                public void onFinish() {
                    Timber.d("FFprobe load finished.");
                }
            });
            isInitialized = true;
        } catch (NotSupportedException e) {
            isInitialized = false;
            e.printStackTrace();
        }
    }

    public Observable<MediaObject> analyzeMedia(MediaType type, String path) {
        return analyzeMediaJson(path)
                .retryWhen(new RetryWhenExceptionWithDelay(3, 5, TimeUnit.SECONDS, CommandAlreadyRunningException.class))
                .flatMap(outputJson -> {
                    final Gson gson = new Gson();
                    outputJson = outputJson.substring(outputJson.indexOf("{"));
                    FFmpegInfo info = gson.fromJson(outputJson, FFmpegInfo.class);
                    MediaObject mediaObject = new MediaObject(type, path);
                    mediaObject.setMediaInfo(info);
                    return Observable.just(mediaObject);
                });
    }

    public Observable<MediaObject> replaceAudioOnMedia(MediaObject sourceMedia, MediaObject audio) {
        long maxLength;
        switch (sourceMedia.getType()) {
            case VIDEO:
                maxLength = Math.min((long) (Double.parseDouble(sourceMedia.getMediaInfo().getFormat().getDuration()) * 1000),
                        (long) (Double.parseDouble(audio.getMediaInfo().getFormat().getDuration()) * 1000));
                return replaceAudioOnVideo(sourceMedia, audio, maxLength)
                        .doOnNext(outputPath -> FileUtils.changePermission(outputPath, "777"))
                        .flatMap(outputFile -> analyzeMedia(MediaType.VIDEO, outputFile));
            case PICTURE:
                maxLength = (long) (Double.parseDouble(audio.getMediaInfo().getFormat().getDuration()) * 1000);
                return scaleImageIfNeccessary(sourceMedia.getFilePath())
                        .flatMap(scaledImagePath -> replaceAudioOnPicture(scaledImagePath, audio.getFilePath(), maxLength))
                        .doOnNext(outputPath -> FileUtils.changePermission(outputPath, "777"))
                        .flatMap(outputFile -> analyzeMedia(MediaType.VIDEO, outputFile));
            default:
                return Observable.error(new Throwable("The source media can't be audio."));
        }
    }

    public Observable<MediaObject> concatMediaObjects(ArrayList<MediaObject> mediaObjects) {
        ffmpeg.killRunningProcesses();
        ffprobe.killRunningProcesses();
        return Observable.from(mediaObjects)
                .flatMap(mediaObject -> convertToIntermediateFormat(mediaObject.getFilePath()))
                .flatMap(tempIntermediatePath -> analyzeMedia(MediaType.VIDEO, tempIntermediatePath))
                .buffer(mediaObjects.size())
                .flatMap(this::concatAndDelete);
    }

    public Observable<MediaObject> concatAndDelete(List<MediaObject> temporaryMediaObjects) {
        return concatIntermediates(temporaryMediaObjects)
                .doOnNext(outputPath -> FileUtils.changePermission(outputPath, "777"))
                .flatMap(outputPath -> analyzeMedia(MediaType.VIDEO, outputPath))
                .doOnCompleted(() -> {
                    for (MediaObject tempIntermediate : temporaryMediaObjects) {
                        File tempFile = new File(tempIntermediate.getFilePath());
                        if (tempFile.exists()) {
                            tempFile.delete();
                            Timber.d("concatAndDelete - Deleted: " + tempIntermediate.getFilePath());
                        }
                    }
                });
    }

    public Observable<String> scaleImageIfNeccessary(String imagePath) {
        return Observable.create(subscriber -> {

            Bitmap image = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            image = BitmapFactory.decodeFile(imagePath, options);

            if (image != null) {

                Log.d("AsyncImageScale", "image is not null");
                float scale;
                if (image.getHeight() < image.getWidth()) {
                    scale = (float) Constants.MAX_PICTURE_SIZE / (float) image.getWidth();
                } else {
                    scale = (float) Constants.MAX_PICTURE_SIZE / (float) image.getHeight();
                }
                if (scale < 1f) {

                    Log.d("AsyncImageScale", "image have to be scaled");
                    Matrix scaleMatrix = new Matrix();
                    scaleMatrix.setScale(scale, scale);
                    File outputFile = new File(VideoEditor.getContext().getCacheDir().getAbsolutePath()
                            + File.pathSeparator + new Date().getTime() + ".png");
                    try {
                        FileOutputStream fos = new FileOutputStream(outputFile);
                        Bitmap resizedImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), scaleMatrix, false);
                        if (resizedImage.getWidth() % 2 == 1) {
                            resizedImage.setWidth(resizedImage.getWidth() - 1);
                        }
                        if (resizedImage.getHeight() % 2 == 1) {
                            resizedImage.setHeight(resizedImage.getHeight() - 1);
                        }
                        resizedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();

                        Log.d("AsyncImageScale", "image is scaled, outputfile: " + outputFile);
                        subscriber.onNext(outputFile.getAbsolutePath());
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                        Log.d("AsyncImageScale", "image scaling failed");
                        subscriber.onNext(imagePath);
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onNext(imagePath);
                    subscriber.onCompleted();
                }
            } else {
                Log.d("AsyncImageScale", "image is null");
                subscriber.onNext(imagePath);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<String> analyzeMediaJson(String path) {
        return Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {

                        Timber.i("Single task started with input: " + path);
                        FFprobeExecutor executor = new FFprobeExecutor.Builder(ffprobe)
                                .setInput(path)
                                .setShowFormat(true)
                                .setShowStreams(true)
                                .setEnableQuietMode(true)
                                .setHandler(new FFprobeExecutor.FFprobeExecutorListener() {
                                    @Override
                                    public void onFailure(String message) {
                                        subscriber.onError(new Throwable(message));
                                        Timber.e("onFailure: " + message);
                                    }

                                    @Override
                                    public void onInfo(String message) {
                                        Timber.i("FFprobe info: " + message);
                                    }

                                    @Override
                                    public void onProgress(String message, float paramFloat, long paramLong) {
                                        Timber.i("onProgress: " + message);
                                    }

                                    @Override
                                    public void onSuccess(String message) {
                                        subscriber.onNext(message);
                                        subscriber.onCompleted();
                                        Timber.i("onSuccess: " + message);
                                    }
                                }).build();

                        try {
                            executor.execute();
                        } catch (CommandAlreadyRunningException e) {
                            subscriber.onError(new Exception("FFprobe command already running."));
                            Timber.e(e, "FFprobe command already running.");
                            subscriber.onCompleted();
                        }
                    }
                });
    }

    public Observable<String> concatIntermediates(List<MediaObject> mediaObjects) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String outputPath = VideoEditor.getBaseDirPath() + String.valueOf(System.nanoTime()) + ".mp4";
                FFmpegExecutor.Builder executorBuilder = new FFmpegExecutor.Builder(ffmpeg)
                        .concat()
                        .outputVideoCodec("copy")
                        .outputVideoCodec("copy")
                        .audioBitsreamFilter("aac_adtstoasc")
                        .strict()
                        .additionalParam("-2")
                        .enableOutputOverride(true)
                        .output(outputPath);
                for (MediaObject tempObject : mediaObjects) {
                    executorBuilder.input(tempObject.getFilePath());
                }

                executorBuilder.setListener(new FFmpegExecutor.FFmpegExecutorListener() {
                    @Override
                    public void onFailure(String message) {
                        Timber.e("concatAndDelete - onFailure: " + message);
                        subscriber.onError(new Throwable(message));
                    }

                    @Override
                    public void onInfo(String message) {
                        Timber.i("concatAndDelete - onInfo: " + message);
                    }

                    @Override
                    public void onProgress(String message, float progress, long remaining) {
                        Timber.i("concatAndDelete - onProgress: " + message);
                        EventBus.getDefault().post(new ProgressEvent(false, progress, remaining, message));
                    }

                    @Override
                    public void onSuccess(String message) {
                        Timber.i("concatAndDelete - onSuccess: " + message);
                        EventBus.getDefault().post(new ProgressEvent(true, 0, 0, message));
                        subscriber.onNext(outputPath);
                        subscriber.onCompleted();
                    }
                });

                FFmpegExecutor executor = executorBuilder.build();

                try {
                    executor.execute();
                } catch (CommandAlreadyRunningException e) {
                    Timber.e(e, "concatAndDelete");
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<String> convertToIntermediateFormat(String videoPath) {
        return Observable.create(subscriber -> {
            String outputPath = VideoEditor.getContext().getCacheDir() + "/" + String.valueOf(System.nanoTime()) + ".ts";
            FFmpegExecutor.Builder executorBuilder = new FFmpegExecutor.Builder(ffmpeg)
                    .input(videoPath)
                    .additionalParam("-filter:v", "scale=iw*min(720/iw\\,480/ih):ih*min(720/iw\\,480/ih), pad=720:480:(720-iw*min(720/iw\\,480/ih))/2:(480-ih*min(720/iw\\,480/ih))/2")
                    .outputAudioCodec("aac")
                    .outputVideoCodec("libx264")
                    .videoBitsreamFilter("h264_mp4toannexb")
                    .audioBitsreamFilter("aac_adtstoasc")
                    .strict()
                    .additionalParam("-2")
                    .output(outputPath);

            executorBuilder.setListener(new FFmpegExecutor.FFmpegExecutorListener() {
                @Override
                public void onFailure(String message) {
                    Timber.e("convertToIntermediateFormat - onFailure: " + message);
                    subscriber.onError(new Throwable(message));
                }

                @Override
                public void onInfo(String message) {
                    Timber.i("convertToIntermediateFormat - onInfo: " + message);
                }

                @Override
                public void onProgress(String message, float progress, long remaining) {
                    Timber.i("convertToIntermediateFormat - onProgress: " + message);
                    EventBus.getDefault().post(new ProgressEvent(false, progress, remaining, message));
                }

                @Override
                public void onSuccess(String message) {
                    Timber.i("convertToIntermediateFormat - onSuccess: " + message);
                    EventBus.getDefault().post(new ProgressEvent(true, 0, 0, message));
                    subscriber.onNext(outputPath);
                    subscriber.onCompleted();
                }
            });

            FFmpegExecutor executor = executorBuilder.build();

            try {
                executor.execute();
            } catch (CommandAlreadyRunningException e) {
                Timber.e(e, "convertToIntermediateFormat");
                subscriber.onError(e);
            }
        });
    }


    public Observable<String> replaceAudioOnVideo(MediaObject source, MediaObject audio, long maxLength) {
        return Observable.create(subscriber -> {
            String outputPath = VideoEditor.getBaseDirPath() + String.valueOf(System.nanoTime()) + ".mp4";

            FFmpegExecutor.Builder executorBuilder = new FFmpegExecutor.Builder(ffmpeg)
                    .enableOutputOverride(true)
                    .input(source.getFilePath())
                    .input(audio.getFilePath())
                    .outputAudioCodec("aac")
                    .outputVideoCodec("libx264")
                    .strict()
                    .additionalParam("-2")
                    .additionalParam("-pix_fmt", "yuv420p")
                    .additionalParam("-map", "0:v:0")
                    .map(1, 0)
                    .buildIndex()
                    .additionalParam("-r")
                    .additionalParam("15")
                    .additionalParam("-g")
                    .additionalParam("1")
                    .additionalParam("-t", FFmpeg.getFFmpegTimeFormat(maxLength))
                    .enableShortest(true)
                    .output(outputPath);

            executorBuilder.setListener(new FFmpegExecutor.FFmpegExecutorListener() {
                @Override
                public void onFailure(String message) {
                    Timber.e("replaceAudioOnVideo - onFailure: " + message);
                    subscriber.onError(new Throwable(message));
                }

                @Override
                public void onInfo(String message) {
                    Timber.i("replaceAudioOnVideo - onInfo: " + message);
                }

                @Override
                public void onProgress(String message, float progress, long remaining) {
                    Timber.i("replaceAudioOnVideo - onProgress: " + message);
                    EventBus.getDefault().post(new ProgressEvent(false, progress, remaining, message));
                }

                @Override
                public void onSuccess(String message) {
                    Timber.i("replaceAudioOnVideo - onSuccess: " + message);
                    EventBus.getDefault().post(new ProgressEvent(true, 0, 0, message));
                    subscriber.onNext(outputPath);
                    subscriber.onCompleted();
                }
            });

            FFmpegExecutor executor = executorBuilder.build();

            try {
                executor.execute();
            } catch (CommandAlreadyRunningException e) {
                Timber.e(e, "replaceAudioOnVideo");
                subscriber.onError(e);
            }
        });
    }

    private Observable<String> replaceAudioOnPicture(String imagePath, String audioFilePath, long maxLength) {
        return Observable.create(subscriber -> {
            String outputPath = VideoEditor.getBaseDirPath() + String.valueOf(System.nanoTime()) + ".mp4";

            FFmpegExecutor.Builder executorBuilder = new FFmpegExecutor.Builder(ffmpeg)
                    .enableOutputOverride(true)
                    .additionalParam("-f", "image2")
                    .loopInput(true)
                    .input(imagePath)
                    .input(audioFilePath)
                    .enableShortest(true)
                    .outputAudioCodec("aac")
                    .outputVideoCodec("libx264")
                    .additionalParam("-tune")
                    .additionalParam("stillimage")
                    .strict()
                    .additionalParam("-2")
                    .additionalParam("-pix_fmt", "yuv420p")
                    .buildIndex()
                    .additionalParam("-r")
                    .additionalParam("15")
                    .additionalParam("-g")
                    .additionalParam("1")
                    .additionalParam("-t", FFmpeg.getFFmpegTimeFormat(maxLength))
                    .output(outputPath);

            executorBuilder.setListener(new FFmpegExecutor.FFmpegExecutorListener() {
                @Override
                public void onFailure(String message) {
                    Timber.e("replaceAudioOnPicture - onFailure: " + message);
                    subscriber.onError(new Throwable(message));
                }

                @Override
                public void onInfo(String message) {
                    Timber.i("replaceAudioOnPicture - onInfo: " + message);
                }

                @Override
                public void onProgress(String message, float progress, long remaining) {
                    Timber.i("replaceAudioOnPicture - onProgress: " + message);
                    EventBus.getDefault().post(new ProgressEvent(false, progress, remaining, message));
                }

                @Override
                public void onSuccess(String message) {
                    Timber.i("replaceAudioOnPicture - onSuccess: " + message);
                    EventBus.getDefault().post(new ProgressEvent(true, 0, 0, message));
                    subscriber.onNext(outputPath);
                    subscriber.onCompleted();
                }
            });

            FFmpegExecutor executor = executorBuilder.build();

            try {
                executor.execute();
            } catch (CommandAlreadyRunningException e) {
                Timber.e(e, "replaceAudioOnPicture");
                subscriber.onError(e);
            }
        });
    }
}
