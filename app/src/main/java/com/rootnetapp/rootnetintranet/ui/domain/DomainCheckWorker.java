package com.rootnetapp.rootnetintranet.ui.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.di.NetModule;

import javax.inject.Inject;

import androidx.work.Worker;

public class DomainCheckWorker extends Worker {

    private ApiInterface apiInterface;
    private static final String TAG = "DomainCheckWorker";


    @NonNull
    @Override
    public Result doWork() {

        Log.d(TAG, "doWork: IN DOWORK FOR DOMAINCHECKWORKER");
        Context app = getApplicationContext();


        return Result.SUCCESS;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }
}
