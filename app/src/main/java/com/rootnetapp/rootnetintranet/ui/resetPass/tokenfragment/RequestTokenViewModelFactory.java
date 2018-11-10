package com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by Propietario on 12/03/2018.
 */

public class RequestTokenViewModelFactory implements ViewModelProvider.Factory{

    private RequestTokenRepository requestTokenRepository;

    public RequestTokenViewModelFactory(RequestTokenRepository requestTokenRepository){
        this.requestTokenRepository = requestTokenRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)  {
        if (modelClass.isAssignableFrom(RequestTokenViewModel.class)) {
            return (T) new RequestTokenViewModel(requestTokenRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
