package com.rootnetapp.rootnetintranet.ui.domain;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import androidx.work.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DomainCheckWorker extends Worker {

    private ApiInterface apiInterface;
    private static final String TAG = "DomainCheckWorker";

    public DomainCheckWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }


//    @NonNull
//    @Override
//    public Result doWork() {
//
//        Log.d(TAG, "doWork: IN DOWORK FOR DOMAINCHECKWORKER");
//        Context app = getApplicationContext();
//
//
//        return Result.SUCCESS;
//    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }
}
