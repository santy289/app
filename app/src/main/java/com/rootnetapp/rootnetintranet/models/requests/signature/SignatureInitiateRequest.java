package com.rootnetapp.rootnetintranet.models.requests.signature;

import com.squareup.moshi.Json;

public class SignatureInitiateRequest {
    public String signatureType;
    public String templateId;
    public String workflowId;
}
