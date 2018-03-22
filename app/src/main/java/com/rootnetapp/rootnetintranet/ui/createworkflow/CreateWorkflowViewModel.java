package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.ViewModel;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowViewModel extends ViewModel {

    //private MutableLiveData<List<Workflow>> mUserLiveData;
    //private MutableLiveData<Integer> mErrorLiveData;
    private CreateWorkflowRepository createWorkflowRepository;
    /*private List<Workflow> workflows, unordered;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+Utils.testToken;*/

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
    }
}
