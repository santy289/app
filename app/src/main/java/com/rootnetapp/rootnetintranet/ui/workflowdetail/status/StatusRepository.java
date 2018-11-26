package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.WorkflowApproveRejectResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StatusRepository {

    private static final String TAG = "StatusRepository";

    private MutableLiveData<WorkflowApproveRejectResponse> responseApproveRejection;
    private MutableLiveData<Boolean> showLoading;

    private ApiInterface service;
    private ProfileDao profileDao;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected StatusRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.profileDao = database.profileDao();
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    /**
     * Gets a profile that is involved to a workflow. Calls ProfileDao object and it is necessary
     * to call in the background. Otherwise will throw and error if it is run on the foreground.
     *
     * @param id Id from Workflow listed as profile involved.
     * @return Returns a ProfileInvolved object with the necessary data.
     */
    protected ProfileInvolved getProfileBy(int id) {
        return profileDao.getProfilesInvolved(id);
    }

    protected Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected void approveWorkflow(String token, int workflowId, boolean isApproved, int nextStatus) {
        Disposable disposable = service.postApproveReject(
                token,
                workflowId,
                isApproved,
                nextStatus
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> responseApproveRejection.setValue(success), throwable -> {
                    Log.d(TAG, "approveWorkflow: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    protected LiveData<WorkflowApproveRejectResponse> getApproveRejectResponse() {
        if (responseApproveRejection == null) {
            responseApproveRejection = new MutableLiveData<>();
        }
        return responseApproveRejection;
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }
}
