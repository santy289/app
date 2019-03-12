
package com.rootnetapp.rootnetintranet.models.responses.googlemaps;

import com.squareup.moshi.Json;

import java.util.List;

public class NearbySearchResponse {

    @Json(name = "html_attributions")
    private List<Object> htmlAttributions = null;
    @Json(name = "results")
    private List<Result> results = null;
    @Json(name = "status")
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
