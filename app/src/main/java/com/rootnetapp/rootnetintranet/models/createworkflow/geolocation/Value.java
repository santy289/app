package com.rootnetapp.rootnetintranet.models.createworkflow.geolocation;

import com.squareup.moshi.Json;

import java.util.List;

public class Value {

    @Json(name = "address")
    private String address;
    @Json(name = "latLng")
    private List<Double> latLng = null;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Double> getLatLng() {
        return latLng;
    }

    public void setLatLng(List<Double> latLng) {
        this.latLng = latLng;
    }

}