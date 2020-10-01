package com.rootnetapp.rootnetintranet.models.requests.signature;

public class DownloadPdfRequest {
    private int templateId;
    private String fileName;
    private int workflowTypeId;
    private int workflowId;

    public DownloadPdfRequest(int templateId, String fileName, int workflowTypeId, int workflowId) {
        this.templateId = templateId;
        this.fileName = fileName;
        this.workflowTypeId = workflowTypeId;
        this.workflowId = workflowId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public int getWorkflowId() {
        return workflowId;
    }
}
