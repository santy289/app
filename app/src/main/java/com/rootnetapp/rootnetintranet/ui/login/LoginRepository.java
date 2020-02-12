package com.rootnetapp.rootnetintranet.ui.login;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginRepository {

    ApiInterface services;

    public LoginRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<LoginResponse> login(String user, String password, String firebaseToken) {
        return services.login(user, password, firebaseToken).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
