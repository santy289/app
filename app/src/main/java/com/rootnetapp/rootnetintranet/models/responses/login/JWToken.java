package com.rootnetapp.rootnetintranet.models.responses.login;

import com.squareup.moshi.Json;

public class JWToken {
    @Json(name = "username")
    private String userName;
    @Json(name = "user_type")
    private String userType;
    @Json(name = "locale")
    private String locale;
    @Json(name = "full_name")
    private String fullName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
