package com.rootnetapp.rootnetintranet.data.local.db.profile.forms;

import android.arch.persistence.room.ColumnInfo;

public class FormCreateProfile {
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "picture")
    public String picture;

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
