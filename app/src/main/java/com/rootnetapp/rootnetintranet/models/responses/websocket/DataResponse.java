package com.rootnetapp.rootnetintranet.models.responses.websocket;

import com.squareup.moshi.Json;

public class DataResponse {
    @Json(name = "key")
    private String key;

    @Json(name = "value")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
