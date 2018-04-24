package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.squareup.moshi.Json;

/**
 * Created by root on 03/04/18.
 */

public class WorkflowResponse {

    @Json(name = "workflow")
    private Workflow workflow = null;

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

}
