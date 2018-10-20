package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;


public class Meta {

    @Json(name = "id")
    private int id;
    @Json(name = "workflow_id")
    private int workflowId;
    @Json(name = "workflow_type_field_id")
    private int workflowTypeFieldId;
    @Json(name = "workflow_type_field_machine")
    private String workflowTypeFieldMachine;
    @Json(name = "workflow_type_field_config")
    private String workflowTypeFieldConfig;
    @Json(name = "workflow_type_field_name")
    private String workflowTypeFieldName;
    @Json(name = "workflow_type_field_order")
    private int workflowTypeFieldOrder;
    @Json(name = "value")
    private String value;
    @Json(name = "displayValue")
    private Object displayValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public int getWorkflowTypeFieldId() {
        return workflowTypeFieldId;
    }

    public void setWorkflowTypeFieldId(int workflowTypeFieldId) {
        this.workflowTypeFieldId = workflowTypeFieldId;
    }

    public String getWorkflowTypeFieldMachine() {
        return workflowTypeFieldMachine;
    }

    public void setWorkflowTypeFieldMachine(String workflowTypeFieldMachine) {
        this.workflowTypeFieldMachine = workflowTypeFieldMachine;
    }

    public String getWorkflowTypeFieldConfig() {
        return workflowTypeFieldConfig;
    }

    public void setWorkflowTypeFieldConfig(String workflowTypeFieldConfig) {
        this.workflowTypeFieldConfig = workflowTypeFieldConfig;
    }

    public String getWorkflowTypeFieldName() {
        return workflowTypeFieldName;
    }

    public void setWorkflowTypeFieldName(String workflowTypeFieldName) {
        this.workflowTypeFieldName = workflowTypeFieldName;
    }

    public int getWorkflowTypeFieldOrder() {
        return workflowTypeFieldOrder;
    }

    public void setWorkflowTypeFieldOrder(int workflowTypeFieldOrder) {
        this.workflowTypeFieldOrder = workflowTypeFieldOrder;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(Object displayValue) {
        this.displayValue = displayValue;
    }

}
