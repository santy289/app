package com.rootnetapp.rootnetintranet.models.ui.signature;

import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;

import java.util.List;

public class SignatureCustomFieldFormState {
    private String title;
    private List<FieldCustom> fieldCustomList;

    public SignatureCustomFieldFormState(String title, List<FieldCustom> fieldCustomList) {
        this.title = title;
        this.fieldCustomList = fieldCustomList;
    }

    public String getTitle() {
        return title;
    }

    public List<FieldCustom> getFieldCustomList() {
        return fieldCustomList;
    }
}
