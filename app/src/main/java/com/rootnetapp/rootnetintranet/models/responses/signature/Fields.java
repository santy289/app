package com.rootnetapp.rootnetintranet.models.responses.signature;

import java.util.List;

public class Fields {
    private List<FieldCustom> customFields;
    private UserRequired userRequired;
    private List<SignatureTemplateField> requiredFields;


    public void setCustomFields(List<FieldCustom> customFields, UserRequired userRequired) {
        this.customFields = customFields;
        this.userRequired = userRequired;
    }

    public List<FieldCustom> getCustomFields() {
        return customFields;
    }

    public UserRequired getUserRequired() {
        return userRequired;
    }

    public List<SignatureTemplateField> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(List<SignatureTemplateField> requiredFields) {
        this.requiredFields = requiredFields;
    }
}
