package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.CompositeDisposable;

public class WorkflowSearchRepository {

    private static final String TAG = "WorkflowSearchRepository";

    private MutableLiveData<Boolean> showLoading;

    private ApiInterface service;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected WorkflowSearchRepository(ApiInterface service) {
        this.service = service;
    }

    protected void clearDisposables() {
        disposables.clear();
    }
}
