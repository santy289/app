package com.rootnetapp.rootnetintranet.data.local.db.workflow.detail;

import androidx.room.ColumnInfo;

public class WorkflowTypeId {
    @ColumnInfo(name = "workflow_type_id")
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
