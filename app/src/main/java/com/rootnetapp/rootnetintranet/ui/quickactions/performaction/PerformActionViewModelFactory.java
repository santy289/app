package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PerformActionViewModelFactory implements ViewModelProvider.Factory {

    private PerformActionRepository repository;

    public PerformActionViewModelFactory(PerformActionRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PerformActionViewModel.class)) {
            return (T) new PerformActionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
