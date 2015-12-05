package hu.bme.agocs.videoeditor.videoeditor.data;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobe;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeLoadBinaryResponseHandler;

import hu.bme.agocs.videoeditor.videoeditor.data.entity.FFmpegTask;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.MediaObject;
import hu.bme.agocs.videoeditor.videoeditor.data.entity.ProcessUpdate;
import hu.bme.agocs.videoeditor.videoeditor.data.enums.ProcessUpdateType;
import hu.bme.agocs.videoeditor.videoeditor.presentation.VideoEditor;
import rx.Observable;
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
}
