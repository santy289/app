package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class PerformActionViewModel extends ViewModel {

    private PerformActionRepository mRepository;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public PerformActionViewModel(PerformActionRepository repository) {
        this.mRepository = repository;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }
}
