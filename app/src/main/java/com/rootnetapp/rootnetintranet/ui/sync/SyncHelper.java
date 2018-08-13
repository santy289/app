package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SyncHelper {

    private MutableLiveData<Boolean> mSyncLiveData;
    private MutableLiveData<Integer> mProgressLiveData;
    private ApiInterface apiInterface;
    private AppDatabase database;
    private int totalQueries =2, queriesCompleted =0;
    private List<Workflow> workflows;
    private String auth;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final static String TAG = "SyncHelper";

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
    }

    protected void syncData(String token) {
        this.auth = token;
        getUser();
        getAllWorkflows(0);
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    private void getUser() {
        Disposable disposable = apiInterface.getUsers(auth).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, throwable -> {
                    Log.d(TAG, "getData: error " + throwable.getMessage() );
                    mSyncLiveData.setValue(false);
                });
        disposables.add(disposable);
    }

    private void getAllWorkflows(int page) {
        Disposable disposable = apiInterface
                .getWorkflows(auth, 50, true, page, true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowsSuccess, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    mSyncLiveData.setValue(false);
                });
        disposables.add(disposable);
    }

    private void onUsersSuccess(UserResponse userResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<User> users = userResponse.getProfiles();
            if (users == null) {
                return false;
            }
            database.userDao().clearUser();
            database.userDao().insertAll(users);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success, this::failure);
        disposables.add(disposable);
    }

    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        workflows.addAll(workflowsResponse.getList());
        if(!workflowsResponse.getPager().isIsLastPage()){
            getAllWorkflows(workflowsResponse.getPager().getNextPage());
        }else{
            Disposable disposable = Observable.fromCallable(() -> {
                database.workflowDao().clearWorkflows();
                database.workflowDao().insertAll(workflows);
                return true;
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::failure);
            disposables.add(disposable);
        }
    }

    private void success(Boolean o) {
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