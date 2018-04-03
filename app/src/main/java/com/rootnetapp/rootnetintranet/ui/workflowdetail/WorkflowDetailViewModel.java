package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

/**
 * Created by root on 02/04/18.
 */

public class WorkflowDetailViewModel extends ViewModel {

    private MutableLiveData<WorkflowType> mTypeLiveData;
    private MutableLiveData<Workflow> mWorkflowLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowDetailRepository repository;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+ Utils.testToken;


    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.repository = workflowDetailRepository;
    }

    public void getWorkflowType(int typeId) {
        repository.getWorkflowType(auth2, typeId).subscribe(this::onTypeSuccess, this::onFailure);
    }

    public void getWorkflow(int workflowId) {
        repository.getWorkflow(auth2, workflowId).subscribe(this::onWorkflowSuccess, this::onFailure);
    }

    private void onTypeSuccess(WorkflowTypeResponse response) {
        mTypeLiveData.setValue(response.getWorkflowType());
    }

    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mWorkflowLiveData.setValue(workflowResponse.getWorkflow());
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<WorkflowType> getObservableType() {
        if (mTypeLiveData == null) {
            mTypeLiveData = new MutableLiveData<>();
        }
        return mTypeLiveData;
    }

    protected LiveData<Workflow> getObservableWorkflow() {
        if (mWorkflowLiveData == null) {
            mWorkflowLiveData = new MutableLiveData<>();
        }
        return mWorkflowLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
