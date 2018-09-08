package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
