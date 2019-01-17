package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class PostCurrency {
    public double value;

    @Json(name = "country_id")
    public int countryId;
}
