package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 02/04/18.
 */

public class WorkflowDetailRepository {

    private AppDatabase database;
    private ApiInterface service;

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    public Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
