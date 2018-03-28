package com.rootnetapp.rootnetintranet.models.requests.createworkflow;

import com.squareup.moshi.Json;

/**
 * Created by root on 28/03/18.
 */

public class CountryData {

    @Json(name = "country_id")
    private int CountryId;
    @Json(name = "value")
    private int Value;

    public int getCountryId() {
        return CountryId;
    }

    public void setCountryId(int countryId) {
        CountryId = countryId;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

}
