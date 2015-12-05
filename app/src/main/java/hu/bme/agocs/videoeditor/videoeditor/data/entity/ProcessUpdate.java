package hu.bme.agocs.videoeditor.videoeditor.data.entity;

import hu.bme.agocs.videoeditor.videoeditor.data.enums.ProcessUpdateType;

/**
 * Created by Agócs Tamás on 2015. 12. 05..
 */
public class ProcessUpdate {
    private ProcessUpdateType type;
    private String output;

    public ProcessUpdate(ProcessUpdateType type, String output) {
        this.type = type;
        this.output = output;
    }

    public ProcessUpdateType getType() {
        return type;
    }

    public String getOutput() {
        return output;
    }
}
