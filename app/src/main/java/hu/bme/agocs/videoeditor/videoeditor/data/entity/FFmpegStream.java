package hu.bme.agocs.videoeditor.videoeditor.data.entity;

/**
 * Created by Agócs Tamás on 2015. 11. 30..
 */
public class FFmpegStream {
    private long index;
    private String codec_name;
    private String codec_long_name;
    private String codec_type;
    private String codec_time_base;
    private String codec_tag_string;
    private String codec_tag;
    private long width;
    private long height;
    private long has_b_frames;
    private String pix_fmt;
    private long level;
    private String is_avc;
    private String nal_length_size;
    private String r_frame_rate;
    private String avg_frame_rate;
    private String time_base;
    private String start_time;
    private String duration;
    private String bit_rate;
    private String nb_frames;
    private String sample_fmt;
    private String sample_rate;
    private long channels;
    private long bits_per_sample;

    public long getIndex() {
        return index;
    }

    public String getCodecName() {
        return codec_name;
    }

    public String getCodecLongName() {
        return codec_long_name;
    }

    public String getCodecType() {
        return codec_type;
    }

    public String getCodecTimeBase() {
        return codec_time_base;
    }

    public String getCodecTagString() {
        return codec_tag_string;
    }

    public String getCodecTag() {
        return codec_tag;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public long getHasBFrames() {
        return has_b_frames;
    }

    public String getPixFmt() {
        return pix_fmt;
    }

    public long getLevel() {
        return level;
    }

    public String getIsAvc() {
        return is_avc;
    }

    public String getNalLengthSize() {
        return nal_length_size;
    }

    public String getRFrameRate() {
        return r_frame_rate;
    }

    public String getAvgFrameRate() {
        return avg_frame_rate;
    }

    public String getTimeBase() {
        return time_base;
    }

    public String getStartTime() {
        return start_time;
    }

    public String getDuration() {
        return duration;
    }

    public String getBitRate() {
        return bit_rate;
    }

    public String getNbFrames() {
        return nb_frames;
    }

    public String getSampleFmt() {
        return sample_fmt;
    }

    public String getSampleRate() {
        return sample_rate;
    }

    public long getChannels() {
        return channels;
    }

    public long getBitsPerSample() {
        return bits_per_sample;
    }
}
