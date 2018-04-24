package com.rootnetapp.rootnetintranet.ui.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by Propietario on 10/03/2018.
 */

public class LoginViewModelFactory implements ViewModelProvider.Factory{

    private LoginRepository loginRepository;

    public LoginViewModelFactory(LoginRepository loginRepository){
        this.loginRepository = loginRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)  {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(loginRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
