package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class Comment {

    @Json(name = "id")
    private int id;
    @Json(name = "user_info")
    private UserInfo userInfo;
    @Json(name = "description")
    private String description;
    @Json(name = "files")
    private List<Object> files = null;
    @Json(name = "date")
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Object> getFiles() {
        return files;
    }

    public void setFiles(List<Object> files) {
        this.files = files;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}