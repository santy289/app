package com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Insert;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;

public class FormFieldsByWorkflowType {

    public int id;
    public int fieldId;
    public int workflowTypeId;
    public String workflowTypeName;

    @ColumnInfo(name = "field_name")
    public String fieldName;

    @ColumnInfo(name = "field_config")
    public String fieldConfig;

    @ColumnInfo(name = "show_form")
    public boolean showForm;

    @ColumnInfo(name = "required")
    public boolean required;

    @ColumnInfo(name = "show_filter")
    public boolean showFilter;

    @Ignore
    public FieldConfig fieldConfigObject;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public String getWorkflowTypeName() {
        return workflowTypeName;
    }

    public void setWorkflowTypeName(String workflowTypeName) {
        this.workflowTypeName = workflowTypeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(String fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public FieldConfig getFieldConfigObject() {
        return fieldConfigObject;
    }

    public void setFieldConfigObject(FieldConfig fieldConfig) {
        this.fieldConfigObject = fieldConfig;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isShowFilter() {
        return showFilter;
    }

    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }
}
