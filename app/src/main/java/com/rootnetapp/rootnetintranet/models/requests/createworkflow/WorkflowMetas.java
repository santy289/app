package com.rootnetapp.rootnetintranet.models.requests.createworkflow;

import com.squareup.moshi.Json;

public class WorkflowMetas {

    @Json(name = "value")
    private String Value;

    @Json(name = "workflow_type_field_id")
    private int WorkflowTypeFieldId;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public int getWorkflowTypeFieldId() {
        return WorkflowTypeFieldId;
    }

    public void setWorkflowTypeFieldId(int workflowTypeFieldId) {
        WorkflowTypeFieldId = workflowTypeFieldId;
    }
}
