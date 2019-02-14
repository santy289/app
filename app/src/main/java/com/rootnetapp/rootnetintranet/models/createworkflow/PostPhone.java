package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class PostPhone {
    public String value;

    @Json(name = "country_id")
    public int countryId;
}
