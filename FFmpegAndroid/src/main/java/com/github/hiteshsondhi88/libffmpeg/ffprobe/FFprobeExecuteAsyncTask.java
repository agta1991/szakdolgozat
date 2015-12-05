package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import android.os.AsyncTask;

import com.github.hiteshsondhi88.libffmpeg.CommandResult;
import com.github.hiteshsondhi88.libffmpeg.ExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.Log;
import com.github.hiteshsondhi88.libffmpeg.ShellCommand;
import com.github.hiteshsondhi88.libffmpeg.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class FFprobeExecuteAsyncTask extends AsyncTask<Void, String, CommandResult> {

    private final String[] cmd;
    private final ExecuteResponseHandler ffprobeExecuteResponseHandler;
    private final ShellCommand shellCommand;
    private final long timeout;
    private long startTime;
    private Process process;
    private String output = "";

    FFprobeExecuteAsyncTask(String[] cmd, long timeout, ExecuteResponseHandler ffprobeExecuteResponseHandler) {
        this.cmd = cmd;
        this.timeout = timeout;
        this.ffprobeExecuteResponseHandler = ffprobeExecuteResponseHandler;
        this.shellCommand = new ShellCommand();
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
        if (ffprobeExecuteResponseHandler != null) {
            ffprobeExecuteResponseHandler.onStart();
        }
    }

    @Override
    protected CommandResult doInBackground(Void... params) {
        try {
            process = shellCommand.run(cmd);
            if (process == null) {
                return CommandResult.getDummyFailureResponse();
            }
            Log.d("Running publishing updates method");
            checkAndUpdateProcess();
            return CommandResult.getOutputFromProcess(process);
        } catch (TimeoutException e) {
            Log.e("FFprobe timed out", e);
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            Log.e("Error running FFprobe", e);
        } finally {
            Util.destroyProcess(process);
        }
        return CommandResult.getDummyFailureResponse();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values != null && values[0] != null && ffprobeExecuteResponseHandler != null) {
            ffprobeExecuteResponseHandler.onProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(CommandResult commandResult) {
        if (ffprobeExecuteResponseHandler != null) {
            output += commandResult.getOutput();
            if (commandResult.isSuccess()) {
                ffprobeExecuteResponseHandler.onSuccess(output);
            } else {
                ffprobeExecuteResponseHandler.onFailure(output);
            }
            ffprobeExecuteResponseHandler.onFinish();
        }
    }

    private void checkAndUpdateProcess() throws TimeoutException, InterruptedException {
        while (!Util.isProcessCompleted(process)) {
            // checking if process is completed
            if (Util.isProcessCompleted(process)) {
                return;
            }

            // Handling timeout
            if (timeout != Long.MAX_VALUE && System.currentTimeMillis() > startTime + timeout) {
                throw new TimeoutException("FFprobe timed out");
            }

            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    if (isCancelled()) {
                        return;
                    }

                    output += line + "\n";
                    publishProgress(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isProcessCompleted() {
        return Util.isProcessCompleted(process);
    }

}
