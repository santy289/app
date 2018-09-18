package com.rootnetapp.rootnetintranet.data.local.db.profile;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

@Entity
public class Profile {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    @Json(name = "user_id")
    private int userId;

    @ColumnInfo(name = "username")
    @Json(name = "username")
    private String userName;

    @ColumnInfo(name = "email")
    @Json(name = "email")
    private String email;

    @ColumnInfo(name = "enabled")
    @Json(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "full_name")
    @Json(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "picture")
    @Json(name = "picture")
    private String picture;

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
}
