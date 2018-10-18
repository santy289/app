package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.squareup.moshi.Json;

public class WorkflowTypeResponse {

    @Json(name = "workflow_type")
    private WorkflowType workflowType;

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }
}
