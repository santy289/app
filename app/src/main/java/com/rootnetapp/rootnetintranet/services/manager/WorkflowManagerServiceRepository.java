package com.rootnetapp.rootnetintranet.services.manager;

import android.os.SystemClock;
import android.util.Log;

import com.rootnetapp.rootnetintranet.commons.PendingWorkflows;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 23/04/18.
 */

public class WorkflowManagerServiceRepository {

    ApiInterface service;
    List<Workflow> pendingWorkflows;

    WorkflowManagerServiceRepository(ApiInterface service) {
        this.service = service;
        //this.pendingWorkflows = PendingWorkflows.getSingleton();
    }

    void getPendingWorkflows(String auth) {
        service.getWorkflows(auth, 200, true, 0, true).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::getData, this::failure);
    }

    private void getData(WorkflowsResponse workflowsResponse) {
        pendingWorkflows.clear();
        pendingWorkflows.addAll(workflowsResponse.getList());
    }

    private void failure(Throwable throwable) {
        Log.d("test", "failure: " + throwable.getMessage());
    }

}