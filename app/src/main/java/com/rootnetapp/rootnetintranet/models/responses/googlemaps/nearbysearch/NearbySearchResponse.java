
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch;

import com.squareup.moshi.Json;

import java.util.List;

public class NearbySearchResponse {

    @Json(name = "html_attributions")
    private List<Object> htmlAttributions = null;
    @Json(name = "results")
    private List<Place> places = null;
    @Json(name = "status")
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
