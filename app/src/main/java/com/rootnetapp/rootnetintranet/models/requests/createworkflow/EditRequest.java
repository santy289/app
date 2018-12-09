package com.rootnetapp.rootnetintranet.models.requests.createworkflow;

import com.squareup.moshi.Json;

import java.util.List;

public class EditRequest {

    @Json(name = "workflow_id")
    private int workflowId;
    @Json(name = "title")
    private String title;
    @Json(name = "description")
    private String description;
    @Json(name = "start")
    private String start;
    @Json(name = "workflow_metas")
    private List<WorkflowMetas> workflowMetas = null;

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public List<WorkflowMetas> getWorkflowMetas() {
        return workflowMetas;
    }

    public void setWorkflowMetas(List<WorkflowMetas> workflowMetas) {
        this.workflowMetas = workflowMetas;
    }

}