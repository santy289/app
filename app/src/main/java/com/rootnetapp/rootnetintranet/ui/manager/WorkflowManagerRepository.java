package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerRepository {

    private final static String TAG = "WorkflowManagerRepository";

    private ApiInterface service;

    public WorkflowManagerRepository(ApiInterface service) {
        this.service = service;
    }

    public Observable<WorkflowResponseDb> getPendingWorkflows(String auth, int page) {
        return service.getWorkflowsDb(auth, 10, true, page, true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(String token,
                                                                    Map<String, Object> options) {
        return service.getWorkflowsByBaseFilters(
                token,
                50,
                true,
                1,
                false,
                options).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
