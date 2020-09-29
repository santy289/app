package com.rootnetapp.rootnetintranet.data.local.db.signature;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "template_signature")
public class TemplateSignature {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int templateId;
    private String name;
    @ColumnInfo(name = "document_status")
    private String documentStatus;
    @ColumnInfo(name = "template_status")
    private String templateStatus;
    @ColumnInfo(name = "workflow_type_id")
    private int workflowTypeId;

    public TemplateSignature(int templateId, int workflowTypeId, String name, String documentStatus, String templateStatus) {
        this.templateId = templateId;
        this.workflowTypeId = workflowTypeId;
        this.name = name;
        this.documentStatus = documentStatus;
        this.templateStatus = templateStatus;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getTemplateStatus() {
        return templateStatus;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }
}
