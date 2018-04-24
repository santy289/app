package com.rootnetapp.rootnetintranet.models.responses.workflowuser;

import com.squareup.moshi.Json;

/**
 * Created by root on 26/03/18.
 */

public class WorkflowUser {

    @Json(name = "id")
    private int id;
    @Json(name = "username")
    private String username;
    @Json(name = "status")
    private boolean status;
    @Json(name = "email")
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}