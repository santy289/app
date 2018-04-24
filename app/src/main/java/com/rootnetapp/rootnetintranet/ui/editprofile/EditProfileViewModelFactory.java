package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditProfileViewModelFactory implements ViewModelProvider.Factory{

    private EditProfileRepository editProfileRepository;

    public EditProfileViewModelFactory(EditProfileRepository editProfileRepository) {
        this.editProfileRepository = editProfileRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EditProfileViewModel.class)) {
            return (T) new EditProfileViewModel(editProfileRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
