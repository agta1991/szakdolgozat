package hu.bme.agocs.videoeditor.videoeditor.data.entity;

import java.util.ArrayList;

/**
 * Created by Agócs Tamás on 2015. 11. 30..
 */
public class FFmpegInfo {
    private ArrayList<FFmpegStream> streams;
    private FFmpegFormat format;

    public ArrayList<FFmpegStream> getStreams() {
        return streams;
    }

    public FFmpegFormat getFormat() {
        return format;
    }
}
