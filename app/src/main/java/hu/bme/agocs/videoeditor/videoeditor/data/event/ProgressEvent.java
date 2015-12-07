package hu.bme.agocs.videoeditor.videoeditor.data.event;

/**
 * Created by Agócs Tamás on 2015. 12. 07..
 */
public class ProgressEvent {
    private boolean isFinished;
    private float progress;
    private long remaining;
    private String message;

    public ProgressEvent(boolean isFinished, float progress, long remaining, String message) {
        this.isFinished = isFinished;
        this.progress = progress;
        this.remaining = remaining;
        this.message = message;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public float getProgress() {
        return progress;
    }

    public long getRemaining() {
        return remaining;
    }

    public String getMessage() {
        return message;
    }
}
