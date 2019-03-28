package com.rootnetapp.rootnetintranet.models.responses.project;

import com.squareup.moshi.Json;

public class Project {

    @Json(name = "project_type_key")
    private String projectTypeKey;
    @Json(name = "id")
    private Integer id;
    @Json(name = "title")
    private String title;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "status")
    private Boolean status;
    @Json(name = "project_type")
    private ProjectType projectType;

    public String getProjectTypeKey() {
        return projectTypeKey;
    }

    public void setProjectTypeKey(String projectTypeKey) {
        this.projectTypeKey = projectTypeKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

}
