package com.rootnetapp.rootnetintranet.data.local.db.workflow;


import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class Person {

    @Json(name = "id")
     int personId;

    @Json(name = "full_name")
     String fullName;

    @Json(name = "picture")
     String picture;

    public int getId() {
        return personId;
    }

    public void setId(int personId) {
        this.personId = personId;
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
