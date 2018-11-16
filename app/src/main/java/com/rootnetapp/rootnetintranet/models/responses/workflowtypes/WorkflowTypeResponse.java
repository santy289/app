package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.squareup.moshi.Json;

public class WorkflowTypeResponse {

    @Json(name = "workflow_type")
    private WorkflowTypeDb workflowType;

    public WorkflowTypeDb getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowTypeDb workflowType) {
        this.workflowType = workflowType;
    }
}
