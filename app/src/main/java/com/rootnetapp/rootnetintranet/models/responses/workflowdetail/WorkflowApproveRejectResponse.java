package com.rootnetapp.rootnetintranet.models.responses.workflowdetail;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.squareup.moshi.Json;

public class WorkflowApproveRejectResponse {
    @Json(name = "workflow")
    private WorkflowDb workflow;

    public WorkflowDb getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowDb workflow) {
        this.workflow = workflow;
    }
}
