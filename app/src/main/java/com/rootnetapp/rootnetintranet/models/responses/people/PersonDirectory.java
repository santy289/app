package com.rootnetapp.rootnetintranet.models.responses.people;

import com.squareup.moshi.Json;

public class PersonDirectory {

    @Json(name = "id")
    private int id;
    @Json(name = "user")
    private int userId;
    @Json(name = "rolePrimary")
    private String rolePrimary;

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

    public String getRolePrimary() {
        return rolePrimary;
    }

    public void setRolePrimary(String rolePrimary) {
        this.rolePrimary = rolePrimary;
    }
}
