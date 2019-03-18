package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class BaseEntityJsonValue {
    @Json(name = "value")
    String value;
    @Json(name = "workflow_type_field_id")
    int workflowTypeFieldId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWorkflowTypeFieldId() {
        return workflowTypeFieldId;
    }

    public void setWorkflowTypeFieldId(int workflowTypeFieldId) {
        this.workflowTypeFieldId = workflowTypeFieldId;
    }
}
