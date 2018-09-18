package com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform;

import android.arch.persistence.room.ColumnInfo;

public class FormFieldsByWorkflowType {

    public int fieldId;
    public int workflowTypeId;
    public String workflowTypeName;

    @ColumnInfo(name = "field_name")
    public String fieldName;

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
}
