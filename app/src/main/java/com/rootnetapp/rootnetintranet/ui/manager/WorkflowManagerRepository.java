package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerRepository {

    ApiInterface service;

    public WorkflowManagerRepository(ApiInterface service) {
        this.service = service;
    }

    public Observable<WorkflowResponseDb> getPendingWorkflows(String auth, int page) {
        return service.getWorkflowsDb(auth, 10, true, page, true).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
