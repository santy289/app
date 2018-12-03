package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PeopleInvolvedRepository {

    private static final String TAG = "PeopleInvolvedRepository";

    private ApiInterface service;
    private ProfileDao profileDao;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected PeopleInvolvedRepository(ApiInterface service, AppDatabase database) {
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

    /**
     * Gets the desired Workflow by the object ID.
     *
     * @param auth       Access token to use for endpoint request.
     * @param workflowId object ID that will be passed on to the endpoint.
     */
    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
