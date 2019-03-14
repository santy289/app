
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete;

import com.squareup.moshi.Json;

import java.util.List;

public class AutocompleteResponse {

    @Json(name = "predictions")
    private List<Prediction> predictions = null;
    @Json(name = "status")
    private String status;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
