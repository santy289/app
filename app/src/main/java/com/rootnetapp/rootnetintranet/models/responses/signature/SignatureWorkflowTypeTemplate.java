package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

import java.util.List;

public class SignatureWorkflowTypeTemplate {
    private int id;
    private String name;
    @Json(name = "required_fields")
    private List<SignatureTemplateField> requiredFields;
    @Json(name="signatures_selected")
    private List<DigitalSignature> signaturesSelected;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SignatureTemplateField> getRequiredFields() {
        return requiredFields;
    }

    public List<DigitalSignature> getSignaturesSelected() {
        return signaturesSelected;
    }
}
