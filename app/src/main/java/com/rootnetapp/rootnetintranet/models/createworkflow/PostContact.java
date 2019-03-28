package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class PostContact {

    @Json(name = "company")
    private String company;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "id")
    private Integer id;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}