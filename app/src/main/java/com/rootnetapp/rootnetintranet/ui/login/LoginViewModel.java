package com.rootnetapp.rootnetintranet.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> mLoginLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private LoginRepository loginRepository;

    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void login(String user, String password) {
        loginRepository.login(user, password).subscribe(this::onLoginSuccess, this::onLoginFailure);
    }

    private void onLoginSuccess(LoginResponse loginResponse) {
        mLoginLiveData.setValue(loginResponse);
    }

    private void onLoginFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    public LiveData<LoginResponse> getObservableLogin() {
        if (mLoginLiveData == null) {
            mLoginLiveData = new MutableLiveData<>();
        }
        return mLoginLiveData;
    }

    public LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}