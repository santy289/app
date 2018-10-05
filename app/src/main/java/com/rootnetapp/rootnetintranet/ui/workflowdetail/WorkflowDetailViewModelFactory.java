package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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