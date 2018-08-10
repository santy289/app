package com.rootnetapp.rootnetintranet.ui.splash;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class SplashViewModelFactory implements ViewModelProvider.Factory {

    private SplashRepository splashRepository;

    public SplashViewModelFactory(SplashRepository splashRepository) {
        this.splashRepository = splashRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(splashRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
