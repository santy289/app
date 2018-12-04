package com.rootnetapp.rootnetintranet.ui.quickactions;

import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class QuickActionsViewModel extends ViewModel {

    private QuickActionsRepository repository;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public QuickActionsViewModel(QuickActionsRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
