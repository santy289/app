package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by root on 27/04/18.
 */

public class WorkflowManagerViewModel extends ViewModel {

    private MutableLiveData<WorkflowResponseDb> mWorkflowsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowManagerRepository repository;

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.repository = repository;
    }


    public void getPendingWorkflows(String auth, int page) {
        repository.getPendingWorkflows(auth, page)
                .subscribe(this::onWorkflowsSuccess, this::onFailure);
    }

    private void onWorkflowsSuccess(WorkflowResponseDb workflowResponseDb) {
        mWorkflowsLiveData.setValue(workflowResponseDb);
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    public LiveData<WorkflowResponseDb> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
