package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by root on 02/04/18.
 */

public class WorkflowDetailViewModelFactory implements ViewModelProvider.Factory {

    private WorkflowDetailRepository workflowDetailRepository;

    public WorkflowDetailViewModelFactory(WorkflowDetailRepository workflowDetailRepository) {
        this.workflowDetailRepository = workflowDetailRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WorkflowDetailViewModel.class)) {
            return (T) new WorkflowDetailViewModel(workflowDetailRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}