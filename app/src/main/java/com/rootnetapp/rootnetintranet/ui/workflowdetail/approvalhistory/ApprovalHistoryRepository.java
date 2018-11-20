package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ApprovalHistoryRepository {

    private static final String TAG = "ApprovalHistoryRepository";

    private MutableLiveData<Boolean> showLoading;

    private ApiInterface service;
    private ProfileDao profileDao;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public ApprovalHistoryRepository(ApiInterface service, AppDatabase database) {
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
    public ProfileInvolved getProfileBy(int id) {
        return profileDao.getProfilesInvolved(id);
    }

    public Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }
}
