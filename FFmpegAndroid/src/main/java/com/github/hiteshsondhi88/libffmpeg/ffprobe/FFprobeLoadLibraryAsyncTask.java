package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import android.content.Context;
import android.os.AsyncTask;

import com.github.hiteshsondhi88.libffmpeg.cpuhelper.CpuArch;
import com.github.hiteshsondhi88.libffmpeg.FileUtils;
import com.github.hiteshsondhi88.libffmpeg.Log;

import java.io.File;

public class FFprobeLoadLibraryAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String cpuArchNameFromAssets;
    private final FFprobeLoadBinaryResponseHandler ffprobeLoadBinaryResponseHandler;
    private final Context context;

    FFprobeLoadLibraryAsyncTask(Context context, String cpuArchNameFromAssets, FFprobeLoadBinaryResponseHandler ffprobeLoadBinaryResponseHandler) {
        this.context = context;
        this.cpuArchNameFromAssets = cpuArchNameFromAssets;
        this.ffprobeLoadBinaryResponseHandler = ffprobeLoadBinaryResponseHandler;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File ffprobeFile = new File(FileUtils.getFFprobe(context));
        if (ffprobeFile.exists() && isDeviceFFprobeVersionOld() && !ffprobeFile.delete()) {
            return false;
        }
        if (!ffprobeFile.exists()) {
            boolean isFileCopied = FileUtils.copyBinaryFromAssetsToData(context,
                    cpuArchNameFromAssets + File.separator + FileUtils.ffprobeFileName,
                    FileUtils.ffprobeFileName);

            // make file executable
            if (isFileCopied) {
                if (!ffprobeFile.canExecute()) {
                    Log.d("FFprobe is not executable, trying to make it executable ...");
                    if (ffprobeFile.setExecutable(true)) {
                        return true;
                    }
                } else {
                    Log.d("FFprobe is executable");
                    return true;
                }
            }
        }
        return ffprobeFile.exists() && ffprobeFile.canExecute();
    }

    @Override
    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (ffprobeLoadBinaryResponseHandler != null) {
            if (isSuccess) {
                ffprobeLoadBinaryResponseHandler.onSuccess();
            } else {
                ffprobeLoadBinaryResponseHandler.onFailure();
            }
            ffprobeLoadBinaryResponseHandler.onFinish();
        }
    }

    private boolean isDeviceFFprobeVersionOld() {
        return CpuArch.fromString(FileUtils.SHA1(FileUtils.getFFprobe(context))).equals(CpuArch.NONE);
    }
}
