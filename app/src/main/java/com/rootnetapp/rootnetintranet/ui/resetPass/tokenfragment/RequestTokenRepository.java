package com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RequestTokenRepository {

    ApiInterface services;

    public RequestTokenRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<ResetPasswordResponse> requestToken(String username, String client_id) {
        return services.requestToken(username, client_id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
