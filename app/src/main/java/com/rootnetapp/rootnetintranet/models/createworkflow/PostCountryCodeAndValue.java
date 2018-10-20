package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class PostCountryCodeAndValue {
    public long value;

    @Json(name = "country_id")
    public int countryId;
}
