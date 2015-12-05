package com.github.hiteshsondhi88.libffmpeg;

public class CommandResult {
    final String output;
    final boolean success;

    public CommandResult(boolean success, String output) {
        this.success = success;
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public boolean isSuccess() {
        return success;
    }

    public static CommandResult getDummyFailureResponse() {
        return new CommandResult(false, "");
    }

    public static CommandResult getOutputFromProcess(Process process) {
        String output;
        if (success(process.exitValue())) {
            output = Util.convertInputStreamToString(process.getInputStream());
        } else {
            output = Util.convertInputStreamToString(process.getErrorStream());
        }
        return new CommandResult(success(process.exitValue()), output);
    }

    public static boolean success(Integer exitValue) {
        return exitValue != null && exitValue == 0;
    }

}