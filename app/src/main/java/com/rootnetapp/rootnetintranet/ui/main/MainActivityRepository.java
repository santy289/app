package com.rootnetapp.rootnetintranet.ui.main;

import android.database.Cursor;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivityRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public MainActivityRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }

    public Observable<User> getUser(int id) {
        return Observable.fromCallable(()-> database.userDao().getUserById(id))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Cursor> getWorkflowsLike(String text) {
        return Observable.fromCallable(()-> database.workflowDao().getWorkflowsLike(text))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Workflow> getWorkflow(int id) {
        return Observable.fromCallable(()-> database.workflowDao().getWorkflow(id))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    
    public Observable<LoginResponse> login(String user, String password, String firebaseToken) {
        return apiInterface.login(user, password, firebaseToken).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
