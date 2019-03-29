package com.rootnetapp.rootnetintranet.models.requests.resetpassword;

import com.squareup.moshi.Json;

public class ResetPasswordRequest {

    @Json(name = "token")
    private String token;
    @Json(name = "password")
    private String password;
    @Json(name = "repeat_new_password")
    private String repeatNewPassword;
    @Json(name = "is_validated")
    private Boolean isValidated;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean isValidated) {
        this.isValidated = isValidated;
    }

}
