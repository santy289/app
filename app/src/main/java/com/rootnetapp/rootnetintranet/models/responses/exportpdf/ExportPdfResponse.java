package com.rootnetapp.rootnetintranet.models.responses.exportpdf;

import com.squareup.moshi.Json;

public class ExportPdfResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "project")
    private String project;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

}