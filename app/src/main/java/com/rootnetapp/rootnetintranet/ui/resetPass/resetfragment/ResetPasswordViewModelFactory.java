package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordViewModelFactory implements ViewModelProvider.Factory{

    private ResetPasswordRepository resetPasswordRepository;

    public ResetPasswordViewModelFactory(ResetPasswordRepository resetPasswordRepository){
        this.resetPasswordRepository = resetPasswordRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)  {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel.class)) {
            return (T) new ResetPasswordViewModel(resetPasswordRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
