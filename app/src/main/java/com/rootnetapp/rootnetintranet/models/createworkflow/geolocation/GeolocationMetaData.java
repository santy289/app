package com.rootnetapp.rootnetintranet.models.createworkflow.geolocation;

import com.squareup.moshi.Json;

public class GeolocationMetaData {

    @Json(name = "value")
    private Value value;

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}