package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import com.github.hiteshsondhi88.libffmpeg.ResponseHandler;

public interface FFprobeLoadBinaryResponseHandler extends ResponseHandler {

    /**
     * on Fail
     */
    void onFailure();

    /**
     * on Success
     */
    void onSuccess();

}
