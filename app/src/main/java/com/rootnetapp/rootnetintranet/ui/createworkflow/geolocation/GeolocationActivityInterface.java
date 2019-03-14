package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.Prediction;

public interface GeolocationActivityInterface {
    void selectSuggestion(Prediction prediction);
}
