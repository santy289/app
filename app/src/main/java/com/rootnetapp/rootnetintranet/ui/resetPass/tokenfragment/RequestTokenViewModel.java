package com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;

/**
 * Created by Propietario on 12/03/2018.
 */

public class RequestTokenViewModel extends ViewModel {

    private MutableLiveData<ResetPasswordResponse> mTokenLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private RequestTokenRepository requestTokenRepository;

    public RequestTokenViewModel(RequestTokenRepository requestTokenRepository) {
        this.requestTokenRepository = requestTokenRepository;
    }

    protected void requestToken(String username) {
        requestTokenRepository.requestToken(username, "1").subscribe(this::onRequestSuccess, this::onRequestFailure);
    }

    private void onRequestSuccess(ResetPasswordResponse resetPasswordResponse) {
        mTokenLiveData.setValue(resetPasswordResponse);
    }

    private void onRequestFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<ResetPasswordResponse> getObservableToken() {
        if (mTokenLiveData == null) {
            mTokenLiveData = new MutableLiveData<>();
        }
        return mTokenLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}