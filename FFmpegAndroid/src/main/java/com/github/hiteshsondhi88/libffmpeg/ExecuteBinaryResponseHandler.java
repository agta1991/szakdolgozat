package com.github.hiteshsondhi88.libffmpeg;

public abstract class ExecuteBinaryResponseHandler implements ExecuteResponseHandler {

    @Override
    public abstract void onSuccess(String message);

    @Override
    public abstract void onProgress(String message);

    @Override
    public abstract void onFailure(String message);

    @Override
    public abstract void onStart();

    @Override
    public abstract void onFinish();
}
