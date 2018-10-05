package com.rootnetapp.rootnetintranet.models.requests.createworkflow;

import android.arch.persistence.room.Ignore;

import com.google.gson.Gson;
import com.squareup.moshi.Json;

import org.json.JSONObject;

public class WorkflowMetas {

    @Json(name = "value")
    private String value;

    @Json(name = "workflow_type_field_id")
    private int WorkflowTypeFieldId;

    private transient String unformattedValue;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWorkflowTypeFieldId() {
        return WorkflowTypeFieldId;
    }

    public void setWorkflowTypeFieldId(int workflowTypeFieldId) {
        WorkflowTypeFieldId = workflowTypeFieldId;
    }

    public String getUnformattedValue() {
        return unformattedValue;
    }

    public void setUnformattedValue(String unformattedValue) {
        this.unformattedValue = unformattedValue;
    }

    public void escapeValue() {
        //value = JSONObject.quote(value);
        Gson gson = new Gson();
        value = gson.toJson(value);
    }

}
