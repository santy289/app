package com.rootnetapp.rootnetintranet.data.local.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

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
    private ApiInterface apiInterface;
    private AppDatabase database;
    private int totalQueries =2, queriesCompleted =0;
    private List<Workflow> workflows;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+ Utils.testToken;


    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
    }

    public void clearData(String auth) {
        Observable.fromCallable(() -> {
            database.userDao().clearUser();
            database.workflowDao().clearWorkflows();
            return auth;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::getData, this::failure);
    }

    private void getData(String auth) {
        apiInterface.getUsers(auth).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, this::failure);
        getAllWorkflows(auth2, 0);
    }

    private void getAllWorkflows(String auth, int page) {
        apiInterface.getWorkflows(auth, 2, true, page, true).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onWorkflowsSuccess, this::failure);
    }

    private void onUsersSuccess(UserResponse userResponse) {
        Observable.fromCallable(() -> {
            database.userDao().insertAll(userResponse.getProfiles());
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::success, this::failure);
    }

    private void onWorkflowsSuccess(WorkflowResponse workflowResponse) {
        workflows.addAll(workflowResponse.getList());
        if(!workflowResponse.getPager().isIsLastPage()){
            //todo CAMBIAR AUTH
            getAllWorkflows(auth2, workflowResponse.getPager().getNextPage());
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

}