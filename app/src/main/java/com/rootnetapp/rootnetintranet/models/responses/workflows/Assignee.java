package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class Assignee {

    @Json(name = "id")
    private int id;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "picture")
    private String picture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
