package hu.bme.agocs.videoeditor.videoeditor.data.entity;

/**
 * Created by Agócs Tamás on 2015. 11. 30..
 */
public class FFmpegFormat {
    private String filename;
    private long nb_streams;
    private String format_name;
    private String format_long_name;
    private String start_time;
    private String duration;
    private String size;
    private String bit_rate;

    public String getFilename() {
        return filename;
    }

    public long getNbStreams() {
        return nb_streams;
    }

    public String getFormatName() {
        return format_name;
    }

    public String getFormatLongName() {
        return format_long_name;
    }

    public String getStartTime() {
        return start_time;
    }

    public String getDuration() {
        return duration;
    }

    public String getSize() {
        return size;
    }

    public String getBitRate() {
        return bit_rate;
    }
}
