package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class WorkflowType {

    @Json(name = "id")
    int workflowId;

    @Json(name = "name")
    String name;

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