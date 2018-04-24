package com.rootnetapp.rootnetintranet.ui.login;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Propietario on 10/03/2018.
 */

public class LoginRepository {

    ApiInterface services;

    public LoginRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<LoginResponse> login(String user, String password) {
        return services.login(user, password).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
