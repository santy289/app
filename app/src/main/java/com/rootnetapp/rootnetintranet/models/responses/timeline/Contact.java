package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

/**
 * Created by root on 11/04/18.
 */

public class Contact {

    @Json(name = "full_name")
    private String fullName;
    @Json(name = "id")
    private int id;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
