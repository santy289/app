package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by root on 24/04/18.
 */

public class WorkflowSearchViewModelFactory implements ViewModelProvider.Factory {

    private WorkflowSearchRepository repository;

    public WorkflowSearchViewModelFactory(WorkflowSearchRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WorkflowSearchViewModel.class)) {
            return (T) new WorkflowSearchViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
