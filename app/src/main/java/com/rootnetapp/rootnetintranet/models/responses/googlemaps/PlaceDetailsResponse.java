
package com.rootnetapp.rootnetintranet.models.responses.googlemaps;

import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.Place;
import com.squareup.moshi.Json;

import java.util.List;

public class PlaceDetailsResponse {

    @Json(name = "html_attributions")
    private List<Object> htmlAttributions = null;
    @Json(name = "result")
    private Place place;
    @Json(name = "status")
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
