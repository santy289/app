package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class WorkflowSearchViewModel extends ViewModel {

    private WorkflowSearchRepository repository;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public WorkflowSearchViewModel(WorkflowSearchRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
