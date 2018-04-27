package com.rootnetapp.rootnetintranet.ui.main;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
