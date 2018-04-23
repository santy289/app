package com.rootnetapp.rootnetintranet.services.manager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;


/**
 * Created by root on 23/04/18.
 */

public class WorkflowManagerViewModel extends ViewModel {

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCreateErrorLiveData;
    private WorkflowManagerRepository repository;
    /*//todo REMOVE, solo testing
    private String auth2 = "Bearer " + Utils.testToken;*/

    public WorkflowManagerViewModel(WorkflowManagerRepository repository) {
        this.repository = repository;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Integer> getObservableCreateError() {
        if (mCreateErrorLiveData == null) {
            mCreateErrorLiveData = new MutableLiveData<>();
        }
        return mCreateErrorLiveData;
    }

}
