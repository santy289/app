package com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist;

import androidx.room.Relation;

public class WorkflowTypeAndWorkflows {
    public int id;
    public String name;
//    @Relation(parentColumn = "id", entityColumn = "workflow_type_id", entity = WorkflowDb.class)
//    public List<WorkflowListItem> workflowDbs;
}
