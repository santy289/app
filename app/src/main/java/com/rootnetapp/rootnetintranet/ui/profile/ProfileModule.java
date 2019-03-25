package com.rootnetapp.rootnetintranet.ui.profile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class ProfileModule {

    @Provides
    ProfileRepository provideProfileRepository(ApiInterface service,
                                               AppDatabase database) {
        return new ProfileRepository(service, database);
    }

    @Provides
    ProfileViewModelFactory provideProfileViewModelFactory(ProfileRepository profileRepository) {
        return new ProfileViewModelFactory(profileRepository);
    }

}
