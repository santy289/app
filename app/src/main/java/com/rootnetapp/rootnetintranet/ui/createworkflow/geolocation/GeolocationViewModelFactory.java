package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by root on 24/04/18.
 */

public class GeolocationViewModelFactory implements ViewModelProvider.Factory {

    private GeolocationRepository repository;

    public GeolocationViewModelFactory(GeolocationRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GeolocationViewModel.class)) {
            return (T) new GeolocationViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
