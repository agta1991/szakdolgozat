package hu.bme.agocs.videoeditor.videoeditor.data.entity;

import hu.bme.agocs.videoeditor.videoeditor.data.enums.ProcessUpdateType;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class ProcessUpdate {
    private ProcessUpdateType type;
    private String output;
    private Object data;

    public ProcessUpdate(ProcessUpdateType type, String output) {
        this.type = type;
        this.output = output;
    }

    public ProcessUpdate(ProcessUpdateType type, String output, Object data) {
        this.type = type;
        this.output = output;
        this.data = data;
    }

    public ProcessUpdateType getType() {
        return type;
    }

    public String getOutput() {
        return output;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
