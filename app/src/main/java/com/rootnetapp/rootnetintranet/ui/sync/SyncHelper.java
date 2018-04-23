package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Propietario on 14/03/2018.
 */

public class SyncHelper {

    private MutableLiveData<Boolean> mSyncLiveData;
    private MutableLiveData<Integer> mProgressLiveData;
    private ApiInterface apiInterface;
    private AppDatabase database;
    private int totalQueries =2, queriesCompleted =0;
    private List<Workflow> workflows;
    private String auth;
    private String auth2;

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
    }

    public void clearData(String auth) {
        //todo token test
        this.auth2 = "Bearer "+ Utils.testToken;
        this.auth = auth;
        Observable.fromCallable(() -> {
            database.userDao().clearUser();
            database.workflowDao().clearWorkflows();
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::getData, this::failure);
    }

    private void getData(boolean boo) {
        apiInterface.getUsers(auth).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, this::failure);
        getAllWorkflows(0);
    }

    private void getAllWorkflows(int page) {
        apiInterface.getWorkflows(auth2, 50, true, page, true).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onWorkflowsSuccess, this::failure);
    }

    private void onUsersSuccess(UserResponse userResponse) {
        Observable.fromCallable(() -> {
            database.userDao().insertAll(userResponse.getProfiles());
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::success, this::failure);
    }

    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        workflows.addAll(workflowsResponse.getList());
        if(!workflowsResponse.getPager().isIsLastPage()){
            getAllWorkflows(workflowsResponse.getPager().getNextPage());
        }else{
            Observable.fromCallable(() -> {
                database.workflowDao().insertAll(workflows);
                return true;
            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::failure);
        }
    }

    private void success(Object o) {
        queriesCompleted++;
        mProgressLiveData.setValue(queriesCompleted);
        if(totalQueries == queriesCompleted){
            mSyncLiveData.setValue(true);
        }
    }

    private void failure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }

    public LiveData<Boolean> getObservableSync() {
        if (mSyncLiveData == null) {
            mSyncLiveData = new MutableLiveData<>();
        }
        return mSyncLiveData;
    }

    public LiveData<Integer> getObservableProgress() {
        if (mProgressLiveData == null) {
            mProgressLiveData = new MutableLiveData<>();
        }
        return mProgressLiveData;
    }
}