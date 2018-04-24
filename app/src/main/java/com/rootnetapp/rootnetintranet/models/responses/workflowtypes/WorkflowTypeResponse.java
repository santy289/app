package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

/**
 * Created by root on 02/04/18.
 */

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
