package com.rootnetapp.rootnetintranet.ui.createworkflow;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowViewModelFactory implements ViewModelProvider.Factory {

    private final CreateWorkflowRepository createWorkflowRepository;

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