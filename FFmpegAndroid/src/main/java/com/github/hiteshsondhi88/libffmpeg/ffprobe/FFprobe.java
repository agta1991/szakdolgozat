package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import android.content.Context;
import android.text.TextUtils;

import com.github.hiteshsondhi88.libffmpeg.CommandResult;
import com.github.hiteshsondhi88.libffmpeg.ExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FileUtils;
import com.github.hiteshsondhi88.libffmpeg.Log;
import com.github.hiteshsondhi88.libffmpeg.R;
import com.github.hiteshsondhi88.libffmpeg.ShellCommand;
import com.github.hiteshsondhi88.libffmpeg.Util;
import com.github.hiteshsondhi88.libffmpeg.cpuhelper.CpuArchHelper;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;

import java.lang.reflect.Array;
import java.util.Map;

/**
 * Created by Agócs Tamás on 2015. 11. 30..
 */
public class FFprobe implements FFprobeInterface {

    private final Context context;
    private FFprobeExecuteAsyncTask ffprobeExecuteAsyncTask;
    private FFprobeLoadLibraryAsyncTask ffprobeLoadLibraryAsyncTask;

    private static final long MINIMUM_TIMEOUT = 10 * 1000;
    private long timeout = Long.MAX_VALUE;

    private static FFprobe instance = null;

    private FFprobe(Context context) {
        this.context = context.getApplicationContext();
        Log.setDEBUG(Util.isDebug(this.context));
    }

    public static FFprobe getInstance(Context context) {
        if (instance == null) {
            instance = new FFprobe(context);
        }
        return instance;
    }

    @Override
    public void loadBinary(FFprobeLoadBinaryResponseHandler ffprobeLoadBinaryResponseHandler) throws NotSupportedException {
        String cpuArchNameFromAssets = null;
        switch (CpuArchHelper.getCpuArch()) {
            case x86:
                Log.i("Loading FFprobe for x86 CPU");
                cpuArchNameFromAssets = "x86";
                break;
            case ARMv7:
                Log.i("Loading FFprobe for armv7 CPU");
                cpuArchNameFromAssets = "armeabi-v7a";
                break;
            case ARMv7_NEON:
                Log.i("Loading FFprobe for armv7-neon CPU");
                cpuArchNameFromAssets = "armeabi-v7a-neon";
                break;
            case NONE:
                throw new NotSupportedException("Device not supported");
        }

        if (!TextUtils.isEmpty(cpuArchNameFromAssets)) {
            ffprobeLoadLibraryAsyncTask = new FFprobeLoadLibraryAsyncTask(context, cpuArchNameFromAssets, ffprobeLoadBinaryResponseHandler);
            ffprobeLoadLibraryAsyncTask.execute();
        } else {
            throw new NotSupportedException("Device not supported");
        }
    }

    @Override
    public void execute(Map<String, String> environvenmentVars, String[] cmd, ExecuteResponseHandler ffprobeExecuteResponseHandler) throws CommandAlreadyRunningException {
        if (ffprobeExecuteAsyncTask != null && !ffprobeExecuteAsyncTask.isProcessCompleted()) {
            throw new CommandAlreadyRunningException("FFprobe command is already running, you are only allowed to run single command at a time");
        }
        if (cmd.length != 0) {
            String[] ffprobe = new String[]{FileUtils.getFFprobe(context, environvenmentVars)};
            String[] command = concatenate(ffprobe, cmd);
            ffprobeExecuteAsyncTask = new FFprobeExecuteAsyncTask(command, timeout, ffprobeExecuteResponseHandler);
            ffprobeExecuteAsyncTask.execute();
        } else {
            throw new IllegalArgumentException("shell command cannot be empty");
        }
    }

    public <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    @Override
    public void execute(String[] cmd, ExecuteResponseHandler ffprobeExecuteResponseHandler) throws CommandAlreadyRunningException {
        execute(null, cmd, ffprobeExecuteResponseHandler);
    }

    @Override
    public String getDeviceFFprobeVersion() throws CommandAlreadyRunningException {
        ShellCommand shellCommand = new ShellCommand();
        CommandResult commandResult = shellCommand.runWaitFor(new String[]{FileUtils.getFFprobe(context), "-version"});
        if (commandResult.isSuccess()) {
            return commandResult.getOutput().split(" ")[2];
        }
        // if unable to find version then return "" to avoid NPE
        return "";
    }

    @Override
    public String getLibraryFFprobeVersion() {
        return context.getString(R.string.shipped_ffmpeg_version);
    }

    @Override
    public boolean isFFprobeCommandRunning() {
        return ffprobeExecuteAsyncTask != null && !ffprobeExecuteAsyncTask.isProcessCompleted();
    }

    @Override
    public boolean killRunningProcesses() {
        return Util.killAsync(ffprobeLoadLibraryAsyncTask) || Util.killAsync(ffprobeExecuteAsyncTask);
    }

    @Override
    public void setTimeout(long timeout) {
        if (timeout >= MINIMUM_TIMEOUT) {
            this.timeout = timeout;
        }
    }

}
