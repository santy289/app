package com.rootnetapp.rootnetintranet.models.responses.edituser;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditUserResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "profile")
    private User user;
    @Json(name = "token")
    private String token;

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

    public User getProfile() {
        return user;
    }

    public void setProfile(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
