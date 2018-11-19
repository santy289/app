package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class StatusViewModel extends ViewModel {

    private StatusRepository repository;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public StatusViewModel(StatusRepository statusRepository) {
        this.repository = statusRepository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        repository.clearDisposables();
    }
}
