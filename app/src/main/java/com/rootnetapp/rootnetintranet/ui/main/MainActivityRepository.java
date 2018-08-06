package com.rootnetapp.rootnetintranet.ui.main;

import android.database.Cursor;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 24/04/18.
 */

public class MainActivityRepository {

    private AppDatabase database;

    public MainActivityRepository(AppDatabase database) {
        this.database = database;
    }

    public Observable<User> getUser(int id) {
        return Observable.fromCallable(()-> database.userDao().getUserById(id))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Cursor> getWorkflowsLike(String text) {
        return Observable.fromCallable(()-> database.workflowDao().getWorkflowsLike(text))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Workflow> getWorkflow(int id) {
        return Observable.fromCallable(()-> database.workflowDao().getWorkflow(id))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }
}
