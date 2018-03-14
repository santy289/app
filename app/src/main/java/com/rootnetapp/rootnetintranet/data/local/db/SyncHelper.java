package com.rootnetapp.rootnetintranet.data.local.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;

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

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
    }

    public void synchronize(String auth) {
        getUsers(auth);
    }

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

    private void success(Boolean foo) {
        mSyncLiveData.setValue(foo);
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
