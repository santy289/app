package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class PostBusinessOpportunity {

    @Json(name = "id")
    private Integer id;
    @Json(name = "title")
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}