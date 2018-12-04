package com.rootnetapp.rootnetintranet.models.requests.files;

import com.squareup.moshi.Json;

import java.util.List;

public class AttachFilesRequest {

    @Json(name = "workflows")
    private List<WorkflowPresetsRequest> workflows = null;

    public List<WorkflowPresetsRequest> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(
            List<WorkflowPresetsRequest> workflows) {
        this.workflows = workflows;
    }
}