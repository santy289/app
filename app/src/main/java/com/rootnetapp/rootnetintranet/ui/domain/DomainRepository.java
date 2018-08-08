package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import io.reactivex.Observable;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DomainRepository {
    ApiInterface services;
    private LiveData<WorkStatus> responseApi;


    private static final String TAG = "DomainRepository";

    public DomainRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<ClientResponse> checkDomain(String domain) {
        return services.getDomain(domain).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void checkDomain(String domain, LifecycleOwner lifecycleOwner) {
        OneTimeWorkRequest domainCheck = new OneTimeWorkRequest.Builder(DomainCheckWorker.class).build();

        UUID id = domainCheck.getId();
        WorkManager workManager = WorkManager.getInstance();
        workManager.enqueue(domainCheck);

        workManager.getStatusById(id).observe(lifecycleOwner, workStatus -> {
            String test = "test";
            Log.d(TAG, "checkDomain: here");
        });
    }

}
