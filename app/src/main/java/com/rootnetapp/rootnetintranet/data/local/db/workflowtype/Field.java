package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

@Entity(foreignKeys = @ForeignKey(entity = WorkflowTypeDb.class,
                                    parentColumns = "id",
                                    childColumns = "workflow_type_id",
                                    onDelete = ForeignKey.CASCADE),
        indices = {@Index("workflow_type_id")})
public class Field {
    @PrimaryKey
    @Json(name = "id")
    private int id;

    @ColumnInfo(name = "workflow_type_id")
    @Json(name = "workflow_type_id")
    private int workflowTypeId;

    @ColumnInfo(name = "field_id")
    @Json(name = "field_id")
    private int fieldId;

    @ColumnInfo(name = "field_name")
    @Json(name = "field_name")
    private String fieldName;

    @ColumnInfo(name = "field_config")
    @Json(name = "field_config")
    private String fieldConfig;

    @ColumnInfo(name = "required")
    @Json(name = "required")
    private boolean required;

    @ColumnInfo(name = "machine_name")
    @Json(name = "machine_name")
    private String machineName;

    @ColumnInfo(name = "show_form")
    @Json(name = "show_form")
    private boolean showForm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }
}
