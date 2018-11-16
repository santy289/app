package com.rootnetapp.rootnetintranet.ui.editprofile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
