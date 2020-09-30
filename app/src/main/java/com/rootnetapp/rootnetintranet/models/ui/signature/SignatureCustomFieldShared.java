package com.rootnetapp.rootnetintranet.models.ui.signature;

public class SignatureCustomFieldShared {
    public int templateId;
    public String jsonFieldConfig;

    public SignatureCustomFieldShared(int templateId, String jsonFieldConfig) {
        this.templateId = templateId;
        this.jsonFieldConfig = jsonFieldConfig;
    }
}
