package com.rootnetapp.rootnetintranet.models.responses.signature;

import java.util.List;

public class InitiateSigningContent {
    private String code;
    private List<Fields> fields;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Fields> getFields() {
        return fields;
    }

    public void setFields(List<Fields> fields) {
        this.fields = fields;
    }
}
