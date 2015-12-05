package com.github.hiteshsondhi88.libffmpeg.ffmpeg;

import java.util.Map;

import com.github.hiteshsondhi88.libffmpeg.ExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;

@SuppressWarnings("unused")
public interface FFmpegInterface {

    /**
     * Load binary to the device according to archituecture. This also updates FFmpeg binary if the binary on device have old version.
     *
     * @param ffmpegLoadBinaryResponseHandler {@link FFmpegLoadBinaryResponseHandler}
     * @throws NotSupportedException
     */
    void loadBinary(FFmpegLoadBinaryResponseHandler ffmpegLoadBinaryResponseHandler) throws NotSupportedException;

    /**
     * Executes a command
     *
     * @param environvenmentVars           Environment variables
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link ExecuteResponseHandler}
     * @throws CommandAlreadyRunningException
     */
    void execute(Map<String, String> environvenmentVars, String[] cmd, ExecuteResponseHandler ffmpegExecuteResponseHandler) throws CommandAlreadyRunningException;

    /**
     * Executes a command
     *
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link ExecuteResponseHandler}
     * @throws CommandAlreadyRunningException
     */
    void execute(String[] cmd, ExecuteResponseHandler ffmpegExecuteResponseHandler) throws CommandAlreadyRunningException;

    /**
     * Tells FFmpeg version currently on device
     *
     * @return FFmpeg version currently on device
     * @throws CommandAlreadyRunningException
     */
    String getDeviceFFmpegVersion() throws CommandAlreadyRunningException;

    /**
     * Tells FFmpeg version shipped with current library
     *
     * @return FFmpeg version shipped with Library
     */
    String getLibraryFFmpegVersion();

    /**
     * Checks if FFmpeg command is Currently running
     *
     * @return true if FFmpeg command is running
     */
    boolean isFFmpegCommandRunning();

    /**
     * Kill Running FFmpeg process
     *
     * @return true if process is killed successfully
     */
    boolean killRunningProcesses();

    /**
     * Timeout for FFmpeg process, should be minimum of 10 seconds
     *
     * @param timeout in milliseconds
     */
    void setTimeout(long timeout);

}
