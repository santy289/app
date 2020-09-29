package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

import java.util.List;

public class TemplatesResponse {
    @Json(name="response")
    private List<SignatureTemplate> response = null;

    public List<SignatureTemplate> getResponse() {
        return response;
    }

    public void setResponse(List<SignatureTemplate> response) {
        this.response = response;
    }
}
