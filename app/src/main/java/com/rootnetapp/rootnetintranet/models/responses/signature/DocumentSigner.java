package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

public class DocumentSigner {
    private String email;

    @Json(name = "internal_id")
    private String internalId;

    @Json(name = "internal_id_type")
    private String internalIdType;

    private String name;

    private boolean ready;

    @Json(name = "signer_status")
    private String signerStatus;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getInternalIdType() {
        return internalIdType;
    }

    public void setInternalIdType(String internalIdType) {
        this.internalIdType = internalIdType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getSignerStatus() {
        return signerStatus;
    }

    public void setSignerStatus(String signerStatus) {
        this.signerStatus = signerStatus;
    }
}
