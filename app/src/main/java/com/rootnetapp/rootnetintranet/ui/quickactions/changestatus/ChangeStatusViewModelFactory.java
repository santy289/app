package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by root on 24/04/18.
 */

public class ChangeStatusViewModelFactory implements ViewModelProvider.Factory {

    private ChangeStatusRepository repository;

    public ChangeStatusViewModelFactory(ChangeStatusRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChangeStatusViewModel.class)) {
            return (T) new ChangeStatusViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
