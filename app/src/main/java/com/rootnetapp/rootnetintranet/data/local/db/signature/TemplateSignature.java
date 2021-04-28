package com.rootnetapp.rootnetintranet.data.local.db.signature;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "template_signature", primaryKeys = {"id", "workflow_type_id", "workflow_id"})
public class TemplateSignature {
    @ColumnInfo(name = "id")
    private int templateId;
    private String name;
    @ColumnInfo(name = "document_status")
    private String documentStatus;
    @ColumnInfo(name = "template_status")
    private String templateStatus;
    @ColumnInfo(name = "workflow_type_id")
    private int workflowTypeId;
    @ColumnInfo(name = "workflow_id")
    private int workflowId;
    @ColumnInfo(name = "file_name")
    private String fileName;
    @ColumnInfo(name = "expiration_time")
    private String expirationTime;
    @ColumnInfo(name="provider_document_id")
    private String providerDocumentId;

    public TemplateSignature(int templateId, int workflowTypeId, int workflowId, String name, String documentStatus, String templateStatus) {
        this.templateId = templateId;
        this.workflowTypeId = workflowTypeId;
        this.workflowId = workflowId;
        this.name = name;
        this.documentStatus = documentStatus;
        this.templateStatus = templateStatus;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setProviderDocumentId(String providerDocumentId) {
        this.providerDocumentId = providerDocumentId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public String getProviderDocumentId() {
        return providerDocumentId;
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

    public int getWorkflowId() {
        return workflowId;
    }
}
