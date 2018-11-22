package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StatusViewModelFactory implements ViewModelProvider.Factory {

    private StatusRepository statusRepository;

    public StatusViewModelFactory(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StatusViewModel.class)) {
            return (T) new StatusViewModel(statusRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}