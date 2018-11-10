package com.rootnetapp.rootnetintranet.ui.workflowlist;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowRepository;

/**
 * Created by root on 19/03/18.
 */

public class WorkflowViewModelFactory implements ViewModelProvider.Factory {

    private WorkflowRepository workflowRepository;

    public WorkflowViewModelFactory(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WorkflowViewModel.class)) {
            return (T) new WorkflowViewModel(workflowRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
