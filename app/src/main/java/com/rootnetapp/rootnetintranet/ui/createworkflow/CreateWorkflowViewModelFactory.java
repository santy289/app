package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowViewModelFactory implements ViewModelProvider.Factory {

    private CreateWorkflowRepository createWorkflowRepository;

    public CreateWorkflowViewModelFactory(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateWorkflowViewModel.class)) {
            return (T) new CreateWorkflowViewModel(createWorkflowRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}