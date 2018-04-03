package com.rootnetapp.rootnetintranet.ui.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 19/03/18.
 */

public class WorkflowRepository {

    private AppDatabase database;
    private ApiInterface service;

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    public Observable<List<Workflow>> getWorkflowsFromInternal() {
        return Observable.fromCallable(()-> database.workflowDao().getAllWorkflows())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowsResponse> getWorkflowsFromService(String auth, int page) {
        return service.getWorkflows(auth, 2, true, page, true).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Workflow>> setWorkflowsOnInternal(List<Workflow> workflows){
        return Observable.fromCallable(() -> {
            database.workflowDao().insertAll(workflows);
            return workflows;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}