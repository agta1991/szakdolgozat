package hu.bme.agocs.videoeditor.videoeditor.data.entity;

import android.net.Uri;

import hu.bme.agocs.videoeditor.videoeditor.data.enums.MediaType;

/**
 * Created by Agócs Tamás on 2015. 11. 29..
 */
public class MediaObject {
    private MediaType type;
    private String filePath;
    private String title;
    private FFmpegInfo mediaInfo;
    private Uri uri;

    public MediaObject() {
    }

    public MediaObject(MediaType type, String filePath) {
        this.type = type;
        this.filePath = filePath;
    }

    public FFmpegInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(FFmpegInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public MediaType getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
