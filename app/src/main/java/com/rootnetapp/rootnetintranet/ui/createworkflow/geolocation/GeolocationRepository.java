package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.PlaceDetailsResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.AutocompleteResponse;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch.NearbySearchResponse;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GeolocationRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public GeolocationRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }

    protected Observable<NearbySearchResponse> getNearbyPlaces(String apiKey, LatLng latLng) {
        String locationString = String
                .format(Locale.US, "%f,%f", latLng.latitude, latLng.longitude);

        return apiInterface.getNearbyPlaces(locationString, apiKey).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<AutocompleteResponse> getAutocompletePlaces(String apiKey, String input) {
        return apiInterface.getAutocompletePlaces(input, apiKey).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<PlaceDetailsResponse> getPlaceDetails(String apiKey, String placeId) {
        return apiInterface.getPlaceDetails(placeId, apiKey).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
