package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowSearchRepository {

    private static final String TAG = "WorkflowSearchRepo";
    private static final int PAGE_LIMIT = 20;

    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<WorkflowResponseDb> responseWorkflowList;

    private ApiInterface service;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected WorkflowSearchRepository(ApiInterface service) {
        this.service = service;
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    /**
     * Performs a call to the endpoint requesting the workflows that match the specified text query.
     *
     * @param auth authentication token.
     * @param page number of the page.
     * @param query text to search.
     */
    protected void getWorkflowsBySearchQuery(String auth, int page,
                                             String query) {
        Disposable disposable = service.getWorkflowsBySearchQuery(
                auth,
                PAGE_LIMIT,
                page,
                true,
                query,
                true
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> responseWorkflowList.setValue(success), throwable -> {
                    Log.d(TAG, "workflowListByQuery: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    /**
     * Performs a call to the endpoint requesting the latest workflows.
     *
     * @param auth authentication token.
     */
    protected void getRecentWorkflows(String auth, int pageNumber) {
        Disposable disposable = service.getWorkflowsDb(
                auth,
                PAGE_LIMIT,
                true,
                pageNumber,
                true
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> responseWorkflowList.setValue(success), throwable -> {
                    Log.d(TAG, "recentWorkflows: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    protected LiveData<WorkflowResponseDb> getObservableWorkflowList() {
        if (responseWorkflowList == null) {
            responseWorkflowList = new MutableLiveData<>();
        }
        return responseWorkflowList;
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }
}
