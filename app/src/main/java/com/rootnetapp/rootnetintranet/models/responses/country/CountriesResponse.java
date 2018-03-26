package com.rootnetapp.rootnetintranet.models.responses.country;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 26/03/18.
 */

public class CountriesResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "countries")
    private List<Country> countries = null;

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

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

}