package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.squareup.moshi.Json;

public class CommentFileResponse {

    public static final String FILE_ENTITY = "workflowcommentfile"; //https://example.dev.rootnetapp.com/download/workflowcommentfile://21

    @Json(name = "Id")
    private int id;
    @Json(name = "WorkflowCommentId")
    private int workflowCommentId;
    @Json(name = "FileId")
    private int fileId;
    @Json(name = "Name")
    private String name;
    @Json(name = "Extension")
    private String extension;
    @Json(name = "Url")
    private String url;
    @Json(name = "CreatedAt")
    private String createdAt;
    @Json(name = "UpdatedAt")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkflowCommentId() {
        return workflowCommentId;
    }

    public void setWorkflowCommentId(int workflowCommentId) {
        this.workflowCommentId = workflowCommentId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
