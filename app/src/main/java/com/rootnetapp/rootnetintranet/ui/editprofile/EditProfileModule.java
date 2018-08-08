package com.rootnetapp.rootnetintranet.ui.editprofile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class EditProfileModule {

    @Provides
    EditProfileRepository provideEditProfileRepository(AppDatabase database, ApiInterface service) {
        return new EditProfileRepository(database, service);
    }

    @Provides
    EditProfileViewModelFactory provideDomainViewModelFactory(EditProfileRepository editProfileRepository) {
        return new EditProfileViewModelFactory(editProfileRepository);
    }

}
