
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch;

import com.squareup.moshi.Json;

public class Northeast {

    @Json(name = "lat")
    private Double lat;
    @Json(name = "lng")
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
