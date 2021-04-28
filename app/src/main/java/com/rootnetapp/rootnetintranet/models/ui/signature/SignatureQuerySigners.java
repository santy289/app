package com.rootnetapp.rootnetintranet.models.ui.signature;

public class SignatureQuerySigners {
    private int workflowId;
    private int workflowTypeId;
    private int templateId;

    public SignatureQuerySigners(int workflowId, int workflowTypeId, int templateId) {
        this.workflowId = workflowId;
        this.workflowTypeId = workflowTypeId;
        this.templateId = templateId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public int getTemplateId() {
        return templateId;
    }
}
