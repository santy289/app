package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class GeolocationRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public GeolocationRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }
}
