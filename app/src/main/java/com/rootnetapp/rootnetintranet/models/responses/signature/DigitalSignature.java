package com.rootnetapp.rootnetintranet.models.responses.signature;

import com.squareup.moshi.Json;

public class DigitalSignature {
    private String name;
    @Json(name = "provider_id")
    private String providerId;

    public String getName() {
        return name;
    }

    public String getProviderId() {
        return providerId;
    }
}
