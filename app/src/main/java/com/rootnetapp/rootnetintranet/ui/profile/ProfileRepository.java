package com.rootnetapp.rootnetintranet.ui.profile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedProfileResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileRepository {

    private ApiInterface service;
    private AppDatabase database;

    public ProfileRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    protected Observable<LoggedProfileResponse> getLoggedProfile(String auth) {
        return service.getLoggedProfile(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
