package com.rootnetapp.rootnetintranet.models.responses.attach;

import com.squareup.moshi.Json;

/**
 * Created by root on 05/04/18.
 */

public class UploadedFile {

    @Json(name = "id")
    private int id;
    @Json(name = "preset_id")
    private int presetId;
    @Json(name = "file_id")
    private int fileId;
    @Json(name = "workflow_id")
    private int workflowId;
    @Json(name = "name")
    private String name;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "uri")
    private String uri;
    @Json(name = "username")
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}