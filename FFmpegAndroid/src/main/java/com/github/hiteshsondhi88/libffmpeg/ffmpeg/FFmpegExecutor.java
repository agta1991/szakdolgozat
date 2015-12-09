package com.github.hiteshsondhi88.libffmpeg.ffmpeg;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.Log;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.ffprobe.FFprobeExecutor;


import java.util.ArrayList;

import java.util.Date;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Agócs Tamás on 2015. 12. 06..
 */
public class FFmpegExecutor {

    private static final String DURATION_REGEXP = "Duration: ([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9])";
    private static final String PROGRESS_REGEXP = "time=([0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\.[0-9][0-9])";
    private ArrayList<FFmpegExecutor.Builder.Arg> builderArgs;
    private List<Long> durations;
    private final FFmpeg ffmpeg;
    private final List<String> inputPaths;
    private final FFmpegExecutorListener listener;
    private final List<FFmpegExecutor.Builder.MapEntry> mapping;
    private final String outputPath;
    private boolean shortest = false;
    private long totalDuration = -1L;

    private FFmpegExecutor(Builder builder) {
        this.ffmpeg = builder.ffmpeg;
        this.inputPaths = builder.inputPaths;
        this.outputPath = builder.outputPath;
        this.listener = builder.listener;
        this.mapping = builder.mapping;
        this.durations = new ArrayList<>();
        this.builderArgs = builder.args;
    }

    private void calculateTotalDuration() {
        totalDuration = 1;
        boolean isLooping = false;
        for (Builder.Arg arg : builderArgs) {
            isLooping |= Builder.LOOP.equals(arg.getArgument());
        }
        if (!isLooping) {
            if (shortest) {
                if (!durations.isEmpty()) {
                    if (durations.size() == 1) {
                        totalDuration = durations.get(durations.size() - 1);
                    } else {
                        totalDuration = Long.MAX_VALUE;
                        for (Long lenght : durations) {
                            if (lenght < totalDuration) {
                                totalDuration = lenght;
                            }
                        }
                    }
                }
            } else {
                totalDuration = 0;
                for (Long lenght : durations) {
                    totalDuration += lenght;
                }
            }
        } else {
            if (!durations.isEmpty()) {
                if (durations.size() == 1) {
                    totalDuration = durations.get(durations.size() - 1);
                } else {
                    totalDuration = Long.MIN_VALUE;
                    for (Long lenght : durations) {
                        if (lenght > totalDuration) {
                            totalDuration = lenght;
                        }
                    }
                }
            }
        }
    }

    private String[] prepareCommandArgs() {
        /*ArrayList<String> argList = new ArrayList<>();
        boolean isConcat = false;
        int mappingId = 0;

        for (FFmpegExecutor.Builder.Arg arg : builderArgs) {
            if (Builder.CONCAT.equals(arg.getArgument())) {
                isConcat = true;
            }
        }

        for (Builder.Arg arg : builderArgs) {
            switch (arg.getArgument()) {
                case Builder.INPUT:
                    if (!isConcat) {
                        argList.add(arg.getArgument());
                        argList.add(arg.getParameter());
                    }
                    break;
                case Builder.CONCAT:
                    argList.add(Builder.INPUT);
                    String concatPrameter = "";
                    concatPrameter += Builder.CONCAT;
                    for (String path : inputPaths) {
                        if (!concatPrameter.endsWith(":")) {
                            concatPrameter += "|";
                        }
                        concatPrameter += path;
                    }
                    argList.add(concatPrameter);
                    break;
                case Builder.MAP:
                    argList.add(Builder.MAP);
                    argList.add(mapping.get(mappingId).fileId + ":" + mapping.get(mappingId).streamId);
                case Builder.SHORTEST:
                    argList.add(Builder.SHORTEST);
                    break;
                default:
                    argList.add(arg.getArgument());
                    if (arg.getParameter() != null) {
                        argList.add(arg.getParameter());
                    }
                    break;
            }
        }
        argList.add(outputPath);
        Log.d("Prepared FFmpeg arguments: " + argList.toString());
        return argList.toArray(new String[argList.size()]);*/
        ArrayList<String> argumentList = new ArrayList<>();
        int mapPos = 0;
        String concatParam = "";
        boolean isConcatenating = false;
        for (Builder.Arg builderArg : builderArgs) {
            if (Builder.CONCAT.equals(builderArg.getArgument())) {
                isConcatenating = true;
            }
        }
        if (isConcatenating) {
            concatParam = "concat:";
            for (String path : inputPaths) {
                if (!concatParam.endsWith(":")) {
                    concatParam = concatParam + "|";
                }
                concatParam = concatParam + path;
            }
        }
        for (Builder.Arg builderArg : builderArgs) {
            switch (builderArg.getArgument()) {
                case Builder.INPUT:
                    if (isConcatenating) {
                        break;
                    } else {
                        argumentList.add(builderArg.getArgument());
                        if (builderArg.getParameter() != null) {
                            argumentList.add(builderArg.getParameter());
                        }
                        break;
                    }
                case Builder.CONCAT:
                    argumentList.add(Builder.INPUT);
                    argumentList.add(concatParam);
                    break;
                case Builder.MAP:
                    if (builderArg.getParameter() == null) {
                        if (mapPos < mapping.size()) {
                            argumentList.add(Builder.MAP);
                            argumentList.add(mapping.get(mapPos).fileId + ":" + mapping.get(mapPos).streamId);
                            mapPos++;
                        }
                    } else {
                        argumentList.add(Builder.MAP);
                        argumentList.add(builderArg.getParameter());
                    }
                    break;
                case Builder.SHORTEST:
                    shortest = true;
                    //Must not break!!!
                default:
                    argumentList.add(builderArg.getArgument());
                    if (builderArg.getParameter() != null) {
                        argumentList.add(builderArg.getParameter());
                    }
            }
        }

        argumentList.add(outputPath);

        android.util.Log.d("Prepared FFmpeg arguments: ", argumentList.toString());
        return argumentList.toArray(new String[argumentList.size()]);
    }

    public void execute() throws CommandAlreadyRunningException {
        String[] commandArgs = prepareCommandArgs();
        ffmpeg.execute(commandArgs, new ExecuteBinaryResponseHandler() {
            int avgCount = 0;
            float avgProgress = -1.0F;
            float lastProgress = 0.0F;
            long lastTimeStamp = -1L;

            public void onFailure(String message) {
                FFmpegExecutor.this.listener.onFailure(message);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            public void onProgress(String message) {
                long currentTime;
                if (message.contains("time=")) {
                    final Matcher progressPattern = Pattern.compile(PROGRESS_REGEXP).matcher(message);
                    if (totalDuration == -1L) {
                        calculateTotalDuration();
                    }
                    if (progressPattern.find()) {
                        String[] foundPatterns = progressPattern.group(1).split(":");
                        int progressTimePosition = (int) (Float.parseFloat(foundPatterns[2]) * 1000.0F) + Integer.parseInt(foundPatterns[1]) * 60000 + Integer.parseInt(foundPatterns[0]) * 3600000;
                        currentTime = new Date().getTime();
                        if (this.lastTimeStamp != -1L) {
                            float currentProgress = progressTimePosition / (float) totalDuration;
                            this.avgProgress = ((avgProgress * avgCount + (currentProgress - lastProgress) / (float) (currentTime - lastTimeStamp)) / (avgCount + 1));
                            int remaining = (int) ((1.0F - progressTimePosition / (float) totalDuration) / this.avgProgress) / 1000;
                            this.avgCount += 1;
                            listener.onProgress(message, progressTimePosition / (float) totalDuration, remaining);
                        }
                        this.lastTimeStamp = currentTime;
                        this.lastProgress = progressTimePosition / (float) totalDuration;
                    }
                    return;
                }
                Matcher durrationPattern = Pattern.compile(DURATION_REGEXP).matcher(message);
                if (durrationPattern.find()) {
                    String[] durationPatternResult = durrationPattern.group(1).split(":");
                    int secs = (int) (Float.parseFloat(durationPatternResult[2]) * 1000.0F);
                    int mins = Integer.parseInt(durationPatternResult[1]) * 60000;
                    long hours = Integer.parseInt(durationPatternResult[0]) * 3600000;
                    durations.add(hours + mins + secs);
                }
                listener.onInfo(message);
            }

            public void onSuccess(String message) {
                listener.onSuccess(message);
            }
        });
    }

    public static class Builder {
        protected static final String AUDIO_BITSTREAM_FILTER = "-bsf:a";
        protected static final String AUDIO_CODEC = "-c:a";
        protected static final String CONCAT = "concat:";
        protected static final String CRF = "-crf";
        protected static final String FASTSTART = "faststart";
        protected static final String FILTER_COMPLEX = "-filter_complex";
        protected static final String FLAGS = "-flags";
        protected static final String GENPTS = "+genpts";
        protected static final String GLOBAL_HEADER = "-global_header";
        protected static final String INPUT = "-i";
        protected static final String LOOP = "-loop";
        protected static final String LOOP_ARG = "1";
        protected static final String MAP = "-map";
        protected static final String MOVFLAGS = "-movflags";
        protected static final String OUTPUT_OVERRIDE = "-y";
        protected static final String SHORTEST = "-shortest";
        protected static final String STRICT = "-strict";
        protected static final String THREADS = "-threads";
        protected static final String VIDEO_BITSTREAM_FILTER = "-bsf:v";
        protected static final String VIDEO_CODEC = "-c:v";
        protected ArrayList<Arg> args = new ArrayList<>();
        protected FFmpeg ffmpeg;
        protected List<String> inputPaths = new ArrayList<>();
        protected FFmpegExecutor.FFmpegExecutorListener listener;
        protected List<MapEntry> mapping = new ArrayList<>();
        protected String outputPath;

        public Builder(FFmpeg ffmpeg) {
            this.ffmpeg = ffmpeg;
        }

        public FFmpegExecutor build() {
            return new FFmpegExecutor(this);
        }

        public Builder input(String inputPath) {
            inputPaths.add(inputPath);
            args.add(new Arg(INPUT, inputPath));
            return this;
        }

        public Builder loopInput(boolean enable) {
            if (enable) {
                args.add(new Arg(LOOP, LOOP_ARG));
            } else {
                args.remove(new Arg(LOOP));
            }
            return this;
        }

        public Builder enableShortest(boolean enable) {
            if (enable) {
                args.add(new Arg(SHORTEST));
            } else {
                args.remove(new Arg(SHORTEST));
            }
            return this;
        }

        public Builder enableOutputOverride(boolean enable) {
            if (enable) {
                args.add(new Arg(OUTPUT_OVERRIDE));
            } else {
                args.remove(new Arg(OUTPUT_OVERRIDE));
            }
            return this;
        }

        public Builder output(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder outputAudioCodec(String audioCodec) {
            args.add(new Arg(AUDIO_CODEC, audioCodec));
            return this;
        }

        public Builder outputVideoCodec(String videoCodec) {
            args.add(new Arg(VIDEO_CODEC, videoCodec));
            return this;
        }

        public Builder setListener(FFmpegExecutorListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder concat() {
            args.add(new Arg(CONCAT));
            return this;
        }

        public Builder audioBitsreamFilter(String filter) {
            args.add(new Arg(AUDIO_BITSTREAM_FILTER, filter));
            return this;
        }

        public Builder videoBitsreamFilter(String filter) {
            args.add(new Arg(VIDEO_BITSTREAM_FILTER, filter));
            return this;
        }

        public Builder filterComplex(String filter_complex) {
            args.add(new Arg(FILTER_COMPLEX, filter_complex));
            return this;
        }

        public Builder strict() {
            args.add(new Arg(STRICT));
            return this;
        }

        public Builder additionalParam(String argument) {
            args.add(new Arg(argument));
            return this;
        }

        public Builder additionalParam(String argument, String parameter) {
            args.add(new Arg(argument, parameter));
            return this;
        }

        public Builder map(int fileId, int streamId) {
            mapping.add(new MapEntry(fileId, streamId));
            args.add(new Arg(MAP));
            return this;
        }

        public Builder fflags() {
            args.add(new Arg(FLAGS));
            args.add(new Arg(GLOBAL_HEADER));
            return this;
        }

        public Builder genpts() {
            args.add(new Arg(GENPTS));
            return this;
        }

        public Builder threads(int threadCount) {
            args.add(new Arg(THREADS, String.valueOf(threadCount)));
            return this;
        }

        public Builder buildIndex() {
            args.add(new Arg(CRF, "26"));  //Medium quality
            args.add(new Arg(MOVFLAGS, FASTSTART));
            return this;
        }

        protected class MapEntry {
            public int fileId;
            public int streamId;

            public MapEntry(int fileId, int streamId) {
                this.fileId = fileId;
                this.streamId = streamId;
            }
        }

        public class Arg {
            public String argument;
            public String parameter;

            public Arg(String argument) {
                this.argument = argument;
            }

            public Arg(String argument, String parameter) {
                this.argument = argument;
                this.parameter = parameter;
            }

            @Override
            public boolean equals(Object other) {
                if (!(other instanceof Arg)) {
                    return false;
                }
                return argument.equals(((Arg) other).getArgument());
            }

            @Override
            public int hashCode() {
                return argument.hashCode();
            }

            public String getArgument() {
                return argument;
            }

            public String getParameter() {
                return parameter;
            }
        }
    }

    public interface FFmpegExecutorListener {
        void onFailure(String paramString);

        void onInfo(String paramString);

        void onProgress(String paramString, float paramFloat, long paramLong);

        void onSuccess(String paramString);
    }
}
