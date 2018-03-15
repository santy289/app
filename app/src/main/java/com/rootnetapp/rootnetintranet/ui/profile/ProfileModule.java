package com.rootnetapp.rootnetintranet.ui.profile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Propietario on 15/03/2018.
 */

@Module
public class ProfileModule {

    @Provides
    ProfileRepository provideProfileRepository(AppDatabase database) {
        return new ProfileRepository(database);
    }

    @Provides
    ProfileViewModelFactory provideProfileViewModelFactory(ProfileRepository profileRepository) {
        return new ProfileViewModelFactory(profileRepository);
    }

}
