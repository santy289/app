package com.rootnetapp.rootnetintranet.models.responses.createworkflow;

import com.squareup.moshi.Json;

/**
 * Created by root on 27/03/18.
 */

public class Workflow {

    @Json(name = "id")
    private int id;
    @Json(name = "title")
    private String title;
    @Json(name = "workflow_type_key")
    private String workflowTypeKey;
    @Json(name = "description")
    private String description;
    @Json(name = "start")
    private String start;
    @Json(name = "status")
    private boolean status;
    @Json(name = "open")
    private boolean open;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkflowTypeKey() {
        return workflowTypeKey;
    }

    public void setWorkflowTypeKey(String workflowTypeKey) {
        this.workflowTypeKey = workflowTypeKey;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}