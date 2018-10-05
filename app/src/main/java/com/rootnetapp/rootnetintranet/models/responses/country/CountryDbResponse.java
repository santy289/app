package com.rootnetapp.rootnetintranet.models.responses.country;

import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDB;
import com.squareup.moshi.Json;

import java.util.List;

public class CountryDbResponse {
    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "countries")
    private List<CountryDB> countries = null;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CountryDB> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryDB> countries) {
        this.countries = countries;
    }
}
