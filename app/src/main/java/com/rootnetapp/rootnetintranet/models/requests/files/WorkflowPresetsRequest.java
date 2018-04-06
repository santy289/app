package com.rootnetapp.rootnetintranet.models.requests.files;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 05/04/18.
 */

public class WorkflowPresetsRequest {

    @Json(name = "workflowId")
    private int workflowId;
    @Json(name = "presets")
    private List<Integer> presets = null;

    public WorkflowPresetsRequest(int workflowId, List<Integer> presets) {
        this.workflowId = workflowId;
        this.presets = presets;
    }

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

}
