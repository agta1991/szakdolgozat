package com.github.hiteshsondhi88.libffmpeg.ffprobe;

import com.github.hiteshsondhi88.libffmpeg.ExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.CommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.NotSupportedException;
import com.github.hiteshsondhi88.libffmpeg.ffmpeg.FFmpegLoadBinaryResponseHandler;

import java.util.Map;

/**
 * Created by Agócs Tamás on 2015. 11. 30..
 */
public interface FFprobeInterface {

    /**
     * Load binary to the device according to archituecture. This also updates FFprobe binary if the binary on device have old version.
     *
     * @param ffprobeLoadBinaryResponseHandler {@link FFmpegLoadBinaryResponseHandler}
     * @throws NotSupportedException
     */
    void loadBinary(FFprobeLoadBinaryResponseHandler ffprobeLoadBinaryResponseHandler) throws NotSupportedException;

    /**
     * Executes a command
     *
     * @param environvenmentVars            Environment variables
     * @param cmd                           command to execute
     * @param ffprobeExecuteResponseHandler {@link ExecuteResponseHandler}
     * @throws CommandAlreadyRunningException
     */
    void execute(Map<String, String> environvenmentVars, String[] cmd, ExecuteResponseHandler ffprobeExecuteResponseHandler) throws CommandAlreadyRunningException;

    /**
     * Executes a command
     *
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link ExecuteResponseHandler}
     * @throws CommandAlreadyRunningException
     */
    void execute(String[] cmd, ExecuteResponseHandler ffmpegExecuteResponseHandler) throws CommandAlreadyRunningException;

    /**
     * Tells FFprobe version currently on device
     *
     * @return FFprobe version currently on device
     * @throws CommandAlreadyRunningException
     */
    String getDeviceFFprobeVersion() throws CommandAlreadyRunningException;

    /**
     * Tells FFprobe version shipped with current library
     *
     * @return FFprobe version shipped with Library
     */
    String getLibraryFFprobeVersion();

    /**
     * Checks if FFprobe command is Currently running
     *
     * @return true if FFprobe command is running
     */
    boolean isFFprobeCommandRunning();

    /**
     * Kill Running FFprobe process
     *
     * @return true if process is killed successfully
     */
    boolean killRunningProcesses();

    /**
     * Timeout for FFprobe process, should be minimum of 10 seconds
     *
     * @param timeout in milliseconds
     */
    void setTimeout(long timeout);

}
