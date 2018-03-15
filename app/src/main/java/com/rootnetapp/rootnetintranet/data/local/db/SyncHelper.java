package com.rootnetapp.rootnetintranet.data.local.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;

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
    private int totalSyncs =1, actualSyncs =0;

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
    }

    public void synchronize(String auth) {
        Observable.fromCallable(() -> {
            database.userDao().deleteAll();
            return auth;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::getUsers, this::failure);
        /*Observable.fromCallable(() -> {
            //database.workflowDao().deleteAll();
            return auth;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::getWorkflows, this::failure);
*/
    }

    /*private void getWorkflows(String auth) {
        apiInterface.getWorkflows(auth).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onWorkflowsSuccess, this::failure);
    }

    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        Observable.fromCallable(() -> {
            database.userDao().insertAll(workflowsResponse);
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::success, this::failure);
    }*/

    private void getUsers(String auth) {
        apiInterface.getUsers(auth).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, this::failure);
    }

    private void onUsersSuccess(UserResponse userResponse) {
        Observable.fromCallable(() -> {
            database.userDao().insertAll(userResponse.getProfiles());
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::success, this::failure);
    }

    private void success(Object o) {
        actualSyncs++;
        if(totalSyncs == actualSyncs){
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