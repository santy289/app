package com.rootnetapp.rootnetintranet.ui.main;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by root on 24/04/18.
 */

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {

    private MainActivityRepository repository;

    public MainActivityViewModelFactory(MainActivityRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
