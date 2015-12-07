package hu.bme.agocs.videoeditor.videoeditor.data;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegExecutor;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobe;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeExecutor;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeLoadBinaryResponseHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.FFmpegInfo;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.FFmpegTask;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.ProcessUpdate;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.MediaType;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.ProcessUpdateType;
import hu.bme.agocs.videoeditor.videoeditor.data.event.ProgressEvent;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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

    public Observable<ProcessUpdate> singleTask(FFmpegTask task) {
        return Observable.create(subscriber -> {
            Timber.i("Single task started with command: \n" + task.getParameters());
            try {
                ffmpeg.execute(task.getParameters(), new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.FAILURE, message));
                        Timber.i("onSuccess: " + message);
                    }

                    @Override
                    public void onProgress(String message) {
                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.FAILURE, message));
                        Timber.i("onProgress: " + message);
                    }

                    @Override
                    public void onFailure(String message) {
                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.FAILURE, message));
                        Timber.e("onFailure: " + message);
                    }

                    @Override
                    public void onStart() {
                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.START, null));
                        Timber.i("onStart");
                    }

                    @Override
                    public void onFinish() {
                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.FINISH, null));
                        Timber.i("onFinish");
                        subscriber.onCompleted();
                    }
                });
            } catch (CommandAlreadyRunningException e) {
                subscriber.onError(new Exception("FFmpeg command already running."));
                Timber.e(e, "FFmpeg command already running.");
                subscriber.onCompleted();
            }

        });
    }

    public Observable<FFmpegInfo> analyzeMedia(String path) {
        return Observable.create(
                new Observable.OnSubscribe<ProcessUpdate>() {
                    @Override
                    public void call(Subscriber<? super ProcessUpdate> subscriber) {

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
                                        subscriber.onNext(new ProcessUpdate(ProcessUpdateType.SUCCESS, message));
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
                })
                .delay(10, TimeUnit.SECONDS)
                .retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {
                        return throwable instanceof CommandAlreadyRunningException && integer < 3;
                    }
                })
                .filter(processUpdate -> ProcessUpdateType.SUCCESS.equals(processUpdate.getType()))
                .flatMap(processUpdate -> {
                    final Gson gson = new Gson();
                    String jsonStr = processUpdate.getOutput();
                    jsonStr = jsonStr.substring(jsonStr.indexOf("{"));
                    FFmpegInfo info = gson.fromJson(jsonStr, FFmpegInfo.class);
                    return Observable.just(info);
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

    public Observable<MediaObject> concatMediaObjects(ArrayList<MediaObject> mediaObjects) {
        ffmpeg.killRunningProcesses();
        ffprobe.killRunningProcesses();
        return Observable.from(mediaObjects)
                .flatMap(mediaObject ->
                                convertToIntermediateFormat(mediaObject.getFilePath())
                                        .flatMap(tempPath -> analyzeMedia(tempPath)
                                                        .flatMap(ffmpegInfo -> {
                                                            MediaObject tempObject = new MediaObject(MediaType.VIDEO, tempPath);
                                                            tempObject.setMediaInfo(ffmpegInfo);
                                                            return Observable.just(tempObject);
                                                        })
                                        )
                )
                .buffer(mediaObjects.size())
                .flatMap(this::concatAndDelete);
    }

    public Observable<MediaObject> concatAndDelete(List<MediaObject> temporaryMediaObjects) {
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
                for (MediaObject tempObject : temporaryMediaObjects) {
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
        }).flatMap(outputPath -> {
            MediaObject mediaObject = new MediaObject(MediaType.VIDEO, outputPath);
            return analyzeMedia(outputPath).flatMap(ffmpegInfo -> {
                mediaObject.setMediaInfo(ffmpegInfo);
                return Observable.just(mediaObject);
            });
        });
    }
}
