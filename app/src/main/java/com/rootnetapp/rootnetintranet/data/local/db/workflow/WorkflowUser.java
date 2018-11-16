package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import androidx.room.ColumnInfo;

import com.squareup.moshi.Json;

public class WorkflowUser {
    @ColumnInfo(name = "user_id")
    @Json(name = "id")
    private int userId;

    @ColumnInfo(name = "full_name")
    @Json(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "picture")
    @Json(name = "picture")
    private String picture;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
