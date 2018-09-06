package com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist;

import android.arch.persistence.room.Relation;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

import java.util.List;

public class WorkflowTypeAndWorkflows {
    public int id;
    public String name;
    @Relation(parentColumn = "id", entityColumn = "workflow_type_id", entity = WorkflowDb.class)
    public List<WorkflowListItem> workflowDbs;
}
