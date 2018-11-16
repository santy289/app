package com.rootnetapp.rootnetintranet.ui.profile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

/**
 * Created by Propietario on 15/03/2018.
 */

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private ProfileRepository profileRepository;

    public ProfileViewModelFactory(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(profileRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}