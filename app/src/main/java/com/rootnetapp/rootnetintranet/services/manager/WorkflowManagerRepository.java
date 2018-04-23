package com.rootnetapp.rootnetintranet.services.manager;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by root on 23/04/18.
 */

public class WorkflowManagerRepository {

    ApiInterface service;

    public WorkflowManagerRepository(ApiInterface service) {
        this.service = service;
    }

    /*public Observable<Object> getWorkflowTypes(String auth) {
        return service.getWorkflowTypes(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }*/

}
