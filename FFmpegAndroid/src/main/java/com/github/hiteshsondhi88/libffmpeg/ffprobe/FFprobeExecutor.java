package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import android.graphics.Path;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.Log;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpeg;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Agócs Tamás on 2015. 12. 06..
 */
public class FFprobeExecutor {

    private static final String DURATION_REGEXP = "Duration: ([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9])";
    private static final String PROGRESS_REGEXP = "time=([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9])";

    private static final String PRINT_FORMAT = "-print_format";
    private static final String JSON = "json";

    private static final String SHOW_FORMAT = "-show_format";
    private static final String SHOW_STREAMS = "-show_streams";

    private final FFprobe ffprobe;
    private final String inputPath;
    private final boolean showFormat;
    private final boolean showStreams;
    private final boolean quietModeEnabled;
    private final FFprobeExecutorListener listener;

    public FFprobeExecutor(Builder builder) {
        this.ffprobe = builder.ffprobe;
        this.listener = builder.listener;
        this.inputPath = builder.inputPath;
        this.showFormat = builder.showFormat;
        this.showStreams = builder.showStreams;
        this.quietModeEnabled = builder.enableQuietMode;
    }


    private String[] prepareCommandArgs() {
        ArrayList<String> argList = new ArrayList<>();

        if (quietModeEnabled) {
            argList.add("-v");
            argList.add("quiet");
        }

        argList.add(PRINT_FORMAT);
        argList.add(JSON);

        if (showFormat) {
            argList.add(SHOW_FORMAT);
        }

        if (showStreams) {
            argList.add(SHOW_STREAMS);
        }

        argList.add(inputPath);

        Log.d("Prepared FFprobe arguments: " + argList.toString());
        return argList.toArray(new String[argList.size()]);
    }

    public void execute() throws CommandAlreadyRunningException {
        String[] commandArgs = prepareCommandArgs();
        ffprobe.execute(commandArgs, new ExecuteBinaryResponseHandler() {

            public void onFailure(String message) {
                listener.onFailure(message);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            public void onProgress(String message) {
                listener.onInfo(message);
            }

            public void onSuccess(String message) {
                listener.onSuccess(message);
            }
        });
    }

    public static class Builder {
        protected FFprobe ffprobe;
        protected String inputPath;
        protected boolean showFormat = false;
        protected boolean showStreams = false;
        protected boolean enableQuietMode = false;
        protected FFprobeExecutorListener listener;

        public Builder(FFprobe ffprobe) {
            this.ffprobe = ffprobe;
        }

        public Builder setInput(String inputPath) {
            this.inputPath = inputPath;
            return this;
        }

        public Builder setShowFormat(boolean showFormat) {
            this.showFormat = showFormat;
            return this;
        }

        public Builder setShowStreams(boolean showStreams) {
            this.showStreams = showStreams;
            return this;
        }

        public Builder setEnableQuietMode(boolean enableQuietMode) {
            this.enableQuietMode = enableQuietMode;
            return this;
        }

        public Builder setHandler(FFprobeExecutorListener listener) {
            this.listener = listener;
            return this;
        }

        public FFprobeExecutor build() {
            return new FFprobeExecutor(this);
        }
    }

    public interface FFprobeExecutorListener {
        void onFailure(String paramString);

        void onInfo(String paramString);

        void onProgress(String paramString, float paramFloat, long paramLong);

        void onSuccess(String paramString);
    }
}
