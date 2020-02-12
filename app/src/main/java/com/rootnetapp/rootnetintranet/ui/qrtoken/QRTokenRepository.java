package com.rootnetapp.rootnetintranet.ui.qrtoken;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.login.TemporaryTokenResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QRTokenRepository {

    private static final String TAG = "QRTokenRepository";

    private final ApiInterface apiInterface;

    protected QRTokenRepository(ApiInterface service) {
        this.apiInterface = service;
    }

    public Observable<TemporaryTokenResponse> requestTemporaryToken(String token) {
        return apiInterface.requestTemporaryToken(token).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
