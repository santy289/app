package com.rootnetapp.rootnetintranet.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;

/**
 * Created by Propietario on 10/03/2018.
 */

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> mLoginLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private LoginRepository loginRepository;

    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    protected void login(String user, String password) {
        loginRepository.login(user, password).subscribe(this::onLoginSuccess, this::onLoginFailure);
    }

    private void onLoginSuccess(LoginResponse loginResponse) {
        mLoginLiveData.setValue(loginResponse);
    }

    private void onLoginFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<LoginResponse> getObservableLogin() {
        if (mLoginLiveData == null) {
            mLoginLiveData = new MutableLiveData<>();
        }
        return mLoginLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}