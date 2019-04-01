package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.requests.resetpassword.ResetPasswordRequest;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordRepository {

    ApiInterface services;

    public ResetPasswordRepository(ApiInterface apiService) {
        this.services = apiService;
    }

    public Observable<ResetPasswordResponse> validateToken(String token) {
        return services.validateToken(token).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        return services.resetPassword(resetPasswordRequest).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
