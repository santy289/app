package com.rootnetapp.rootnetintranet.models.responses.signature;

import java.util.List;

public class Fields {
    private List<FieldCustom> customFields;
    private UserRequired userRequired;


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
}
