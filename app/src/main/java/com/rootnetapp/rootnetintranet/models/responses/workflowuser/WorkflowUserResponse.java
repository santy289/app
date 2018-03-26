package com.rootnetapp.rootnetintranet.models.responses.workflowuser;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 26/03/18.
 */

public class WorkflowUserResponse {

    @Json(name = "status")
    private String status;
    @Json(name = "code")
    private int code;
    @Json(name = "users")
    private List<WorkflowUser> users = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<WorkflowUser> getUsers() {
        return users;
    }

    public void setUsers(List<WorkflowUser> users) {
        this.users = users;
    }

}