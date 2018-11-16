package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.squareup.moshi.Json;

public class WorkflowResponse {

    @Json(name = "workflow")
    private WorkflowDb workflow = null;

    public WorkflowDb getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowDb workflow) {
        this.workflow = workflow;
    }

}
