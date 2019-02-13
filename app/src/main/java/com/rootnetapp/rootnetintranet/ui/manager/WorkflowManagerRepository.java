package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowoverview.WorkflowOverviewResponse;
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

    private static final String TAG = "WorkflowManagerRepository";
    private static final int ALL_WORKFLOWS_PAGE_LIMIT = 10;
    private static final int WORKFLOWS_DIALOG_PAGE_LIMIT = 50;

    private ApiInterface service;

    public WorkflowManagerRepository(ApiInterface service) {
        this.service = service;
    }

    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(String token, boolean open, int page, int limit,
                                                                    Map<String, Object> options) {
        return service.getWorkflowsByBaseFilters(
                token,
                limit,
                open,
                page,
                true,
                options).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(String token, int page, Map<String, Object> options) {
        return getWorkflowsByBaseFilters(token, true, page, ALL_WORKFLOWS_PAGE_LIMIT, options);
    }

    protected Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(String token, boolean open,
                                                                    Map<String, Object> options) {
        return getWorkflowsByBaseFilters(token, open, 1, WORKFLOWS_DIALOG_PAGE_LIMIT, options);
    }

    protected Observable<WorkflowResponseDb> getWorkflowsByBaseFilters(String token,
                                                                    Map<String, Object> options) {
        return getWorkflowsByBaseFilters(token, true, options);
    }

    protected Observable<WorkflowOverviewResponse> getOverviewWorkflowsCount(String token, String startDate, String endDate) {
        return service.getOverviewWorkflowsCount(
                token,
                startDate,
                endDate,
                true,
                true).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
