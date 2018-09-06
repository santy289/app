package com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist;

import android.arch.persistence.room.ColumnInfo;

public class WorkflowListItem {
    public int id;
    public String title;
    @ColumnInfo(name = "workflow_type_key")
    public String workflowTypeKey;
    @ColumnInfo(name = "full_name")
    public String fullName;
    @ColumnInfo(name = "current_status_name")
    public String currentStatusName;
    @ColumnInfo(name = "created_at")
    public String createdAt;
    @ColumnInfo(name = "updated_at")
    public String updatedAt;
}
