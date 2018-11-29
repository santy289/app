package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import io.reactivex.disposables.CompositeDisposable;

public class PerformActionRepository {

    private static final String TAG = "PerformActionRepository";

    private ApiInterface service;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected PerformActionRepository(ApiInterface service) {
        this.service = service;
    }

    protected void clearDisposables() {
        disposables.clear();
    }
}
