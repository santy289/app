package com.rootnetapp.rootnetintranet.models.responses.workflowdetail;

import com.squareup.moshi.Json;

public class DeleteWorkflowResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}