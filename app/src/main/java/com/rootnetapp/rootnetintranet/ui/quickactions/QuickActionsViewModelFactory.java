package com.rootnetapp.rootnetintranet.ui.quickactions;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by root on 24/04/18.
 */

public class QuickActionsViewModelFactory implements ViewModelProvider.Factory {

    private QuickActionsRepository repository;

    public QuickActionsViewModelFactory(QuickActionsRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QuickActionsViewModel.class)) {
            return (T) new QuickActionsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
