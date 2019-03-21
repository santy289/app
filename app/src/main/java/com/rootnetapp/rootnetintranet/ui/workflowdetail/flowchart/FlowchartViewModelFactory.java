package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FlowchartViewModelFactory implements ViewModelProvider.Factory {

    private FlowchartRepository repository;

    public FlowchartViewModelFactory(FlowchartRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FlowchartViewModel.class)) {
            return (T) new FlowchartViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
