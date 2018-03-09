package com.rootnetapp.rootnetintranet.models.responses;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Country {

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