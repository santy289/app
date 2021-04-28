package com.rootnetapp.rootnetintranet.models.requests.signature;

import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;

import java.util.List;

public class SignatureInitiateRequest {
    public String signatureType;
    public int templateId;
    public int workflowId;
    public List<Fields> customFields;

    public SignatureInitiateRequest(String signatureType, int templateId, int workflowId) {
        this.signatureType = signatureType;
        this.templateId = templateId;
        this.workflowId = workflowId;
    }

    public SignatureInitiateRequest(String signatureType, int templateId, int workflowId, List<Fields> customFields) {
        this.signatureType = signatureType;
        this.templateId = templateId;
        this.workflowId = workflowId;
        this.customFields = customFields;
    }
}
