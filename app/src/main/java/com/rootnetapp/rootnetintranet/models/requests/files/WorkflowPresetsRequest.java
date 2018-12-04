package com.rootnetapp.rootnetintranet.models.requests.files;

import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 05/04/18.
 */

public class WorkflowPresetsRequest {

    public static final String PRESET_TYPE_FILE = "file";

    @Json(name = "workflowId")
    private int workflowId;
    @Json(name = "presets")
    private List<Integer> presets = null;
    @Json(name = "preset_type")
    private String presetType;
    @Json(name = "file")
    private CommentFile file;

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public List<Integer> getPresets() {
        return presets;
    }

    public void setPresets(List<Integer> presets) {
        this.presets = presets;
    }

    public String getPresetType() {
        return presetType;
    }

    public void setPresetType(String presetType) {
        this.presetType = presetType;
    }

    public CommentFile getFile() {
        return file;
    }

    public void setFile(CommentFile file) {
        this.file = file;
    }
}
