package com.rootnetapp.rootnetintranet.ui.manager;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerViewModelFactory implements ViewModelProvider.Factory {

    private WorkflowManagerRepository repository;

    public WorkflowManagerViewModelFactory(WorkflowManagerRepository repository){
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)  {
        if (modelClass.isAssignableFrom(WorkflowManagerViewModel.class)) {
            return (T) new WorkflowManagerViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
