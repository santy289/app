package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowRepository {

    ApiInterface service;
    AppDatabase database;

    public CreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    public Observable<WorkflowTypesResponse> getWorkflowTypes(String auth) {
        return service.getWorkflowTypes(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }



}
