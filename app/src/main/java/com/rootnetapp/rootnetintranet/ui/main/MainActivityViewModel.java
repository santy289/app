package com.rootnetapp.rootnetintranet.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;

/**
 * Created by root on 24/04/18.
 */

public class MainActivityViewModel extends ViewModel {

    private MainActivityRepository repository;
    private MutableLiveData<User> mUserLiveData;
    private MutableLiveData<Cursor> mWorkflowsLiveData;
    private MutableLiveData<Workflow> mWorkflowLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mWorkfErrorLiveData;

    public MainActivityViewModel(MainActivityRepository repository) {
        this.repository = repository;
    }

    public void getUser(int id) {
        repository.getUser(id).subscribe(this::onUserSuccess, this::onFailure);
    }

    public void getWorkflowsLike(String text) {
        repository.getWorkflowsLike(text).subscribe(this::onWorkflowsSuccess, this::onWorflowsFailure);
    }

    public void getWorkflow(int id) {
        repository.getWorkflow(id).subscribe(this::onWorkflowSuccess, this::onFailure);
    }

    private void onWorkflowSuccess(Workflow workflow) {
        mWorkflowLiveData.setValue(workflow);
    }

    private void onUserSuccess(User user) {
        mUserLiveData.setValue(user);
    }

    private void onWorkflowsSuccess(Cursor cursor) {
        mWorkflowsLiveData.setValue(cursor);
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    private void onWorflowsFailure(Throwable throwable) {
        mWorkfErrorLiveData.setValue(R.string.failure_connect);
    }

    public LiveData<User> getObservableUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    public LiveData<Cursor> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    public LiveData<Workflow> getObservableWorkflow() {
        if (mWorkflowLiveData == null) {
            mWorkflowLiveData = new MutableLiveData<>();
        }
        return mWorkflowLiveData;
    }

    public LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public LiveData<Integer> getObservableWorkflowError() {
        if (mWorkfErrorLiveData == null) {
            mWorkfErrorLiveData = new MutableLiveData<>();
        }
        return mWorkfErrorLiveData;
    }

}
