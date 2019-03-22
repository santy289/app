package com.rootnetapp.rootnetintranet.models.responses.user;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.squareup.moshi.Json;

public class LoggedProfileResponse {
    @Json(name = "status")
    private String status;
    @Json(name = "code")
    private Integer code;
    @Json(name = "profile")
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setLoggedUser(User user) {
        this.user = user;
    }
}
