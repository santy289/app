package com.rootnetapp.rootnetintranet.models.responses.createworkflow;

import com.squareup.moshi.Json;

public class CreateWorkflowResponse {

    @Json(name = "workflow")
    private Workflow workflow;

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

}