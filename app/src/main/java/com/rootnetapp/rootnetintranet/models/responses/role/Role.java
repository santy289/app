package com.rootnetapp.rootnetintranet.models.responses.role;

import com.squareup.moshi.Json;

public class Role {
    @Json(name = "id")
    private int id;

    @Json(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
