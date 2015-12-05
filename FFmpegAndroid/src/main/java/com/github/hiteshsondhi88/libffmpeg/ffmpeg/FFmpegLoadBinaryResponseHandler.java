package com.github.hiteshsondhi88.libffmpeg.ffmpeg;

import com.github.hiteshsondhi88.libffmpeg.ResponseHandler;

public interface FFmpegLoadBinaryResponseHandler extends ResponseHandler {

    /**
     * on Fail
     */
    void onFailure();

    /**
     * on Success
     */
    void onSuccess();

}
