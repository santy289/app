package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

import java.util.List;

public class DocumentResponse {
    @Json(name = "template_id")
    private int templateId;

    @Json(name= "expiration_time")
    private String expirationTime;

    @Json(name= "file_name")
    private String fileName;

    @Json(name= "provider_id")
    private int providerId;

    @Json(name = "provider_document_id")
    private String providerDocumentId;

    private String status;

    private List<DocumentSigner> signers;

    public List<DocumentSigner> getSigners() {
        return signers;
    }

    public void setSigners(List<DocumentSigner> signers) {
        this.signers = signers;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderDocumentId() {
        return providerDocumentId;
    }

    public void setProviderDocumentId(String providerDocumentId) {
        this.providerDocumentId = providerDocumentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
