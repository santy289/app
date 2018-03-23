package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;

import java.util.List;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowViewModel extends ViewModel {

    private MutableLiveData<List<WorkflowType>> mWorkflowsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private CreateWorkflowRepository createWorkflowRepository;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+ Utils.testToken;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
    }

    public void getWorkflowTypes(String auth){
        //todo SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getWorkflowTypes(auth2).subscribe(this::onTypesSuccess, this::onTypesFailure);
    }

    private void onTypesSuccess(WorkflowTypesResponse workflowTypesResponse) {
        mWorkflowsLiveData.setValue(workflowTypesResponse.getList());
    }

    private void onTypesFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);

    }

    protected LiveData<List<WorkflowType>> getObservableWorkflows() {
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
