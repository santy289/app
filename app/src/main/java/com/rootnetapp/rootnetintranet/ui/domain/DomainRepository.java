package com.rootnetapp.rootnetintranet.ui.domain;

import io.reactivex.Observable;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DomainRepository {
    ApiInterface services;

    private static final String TAG = "DomainRepository";

    public DomainRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<ClientResponse> checkDomain(String domain) {
        return services.getDomain(domain).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
