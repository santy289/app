package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class WorkflowType {

    @Json(name = "id")
    public int workflowId;

    @Json(name = "name")
    public String name;

    public int getId() {
        return workflowId;
    }

    public void setId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}