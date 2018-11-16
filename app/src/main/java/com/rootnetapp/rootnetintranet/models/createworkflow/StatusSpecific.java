package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class StatusSpecific {
    @Json(name = "user")
    public int user;

    @Json(name = "status")
    public int status;
}
