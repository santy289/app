package com.rootnetapp.rootnetintranet.models.responses.user;

import com.squareup.moshi.Json;

public class LoggedProfileResponse {
    @Json(name = "status")
    private String status;
    @Json(name = "code")
    private Integer code;
    @Json(name = "profile")
    private LoggedUser loggedUser;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }
}
