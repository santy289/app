package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class GeolocationModule {

    @Provides
    GeolocationRepository provideGeolocationRepository(AppDatabase database, ApiInterface apiInterface) {
        return new GeolocationRepository(database, apiInterface);
    }

    @Provides
    GeolocationViewModelFactory provideGeolocationViewModelFactory(GeolocationRepository repository) {
        return new GeolocationViewModelFactory(repository);
    }

}
