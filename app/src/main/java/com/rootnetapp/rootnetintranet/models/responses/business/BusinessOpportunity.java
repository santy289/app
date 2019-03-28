package com.rootnetapp.rootnetintranet.models.responses.business;

import com.squareup.moshi.Json;

public class BusinessOpportunity {

    @Json(name = "id")
    private Integer id;
    @Json(name = "title")
    private String title;
    @Json(name = "active")
    private Boolean active;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}