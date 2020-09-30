package com.rootnetapp.rootnetintranet.models.requests.signature;

import com.squareup.moshi.Json;

public class SignatureInitiateRequest {
    public String signatureType;
    public int templateId;
    public int workflowId;

    public SignatureInitiateRequest(String signatureType, int templateId, int workflowId) {
        this.signatureType = signatureType;
        this.templateId = templateId;
        this.workflowId = workflowId;
    }
}
