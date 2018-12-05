package com.rootnetapp.rootnetintranet.models.requests.createworkflow;

import com.squareup.moshi.Json;

import java.util.List;

public class EditRequest {

    @Json(name = "workflow_id")
    private int workflowId;
    @Json(name = "workflow_metas")
    private List<WorkflowMetas> workflowMetas = null;

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public List<WorkflowMetas> getWorkflowMetas() {
        return workflowMetas;
    }

    public void setWorkflowMetas(List<WorkflowMetas> workflowMetas) {
        this.workflowMetas = workflowMetas;
    }

}