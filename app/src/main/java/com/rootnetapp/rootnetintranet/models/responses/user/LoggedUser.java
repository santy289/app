package com.rootnetapp.rootnetintranet.models.responses.user;

import com.squareup.moshi.Json;

import java.util.Map;

public class LoggedUser {
    @Json(name = "id")
    private int id;

    @Json(name = "user_id")
    private int userId;

    @Json(name = "username")
    private String userName;

    @Json(name = "email")
    private String email;

    @Json(name = "enabled")
    private boolean enabled;

    @Json(name = "full_name")
    private String fullName;

    @Json(name = "picture")
    private String picture;

    @Json(name = "new_permissions")
    private Map<String, Object> permissions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Map<String, Object> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Object> permissions) {
        this.permissions = permissions;
    }
}
