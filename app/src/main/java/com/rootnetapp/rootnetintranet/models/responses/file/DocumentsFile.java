package com.rootnetapp.rootnetintranet.models.responses.file;

import com.squareup.moshi.Json;

/**
 * Created by root on 04/04/18.
 */

public class DocumentsFile {

    @Json(name = "id")
    private int id;
    @Json(name = "preset_id")
    private int presetId;
    @Json(name = "file_id")
    private int fileId;
    @Json(name = "workflow_id")
    private int workflowId;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "uri")
    private Object uri;
    @Json(name = "user")
    private boolean user;
    @Json(name = "name")
    private String name;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUri() {
        try {
            return (String) uri;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public void setUri(Object uri) {
        this.uri = uri;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}