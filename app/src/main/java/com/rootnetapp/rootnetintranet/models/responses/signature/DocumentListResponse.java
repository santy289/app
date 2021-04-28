package com.rootnetapp.rootnetintranet.models.responses.signature;

import java.util.List;

public class DocumentListResponse {
    private List<DocumentResponse> response;

    public List<DocumentResponse> getResponse() {
        return response;
    }

    public void setResponse(List<DocumentResponse> response) {
        this.response = response;
    }
}
