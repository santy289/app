package com.rootnetapp.rootnetintranet.ui.main;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileRepository;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileViewModelFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 24/04/18.
 */

@Module
public class MainActivityModule {

    @Provides
    MainActivityRepository provideMainActivityRepository(AppDatabase database) {
        return new MainActivityRepository(database);
    }

    @Provides
    MainActivityViewModelFactory provideMainActivityViewModelFactory(MainActivityRepository repository) {
        return new MainActivityViewModelFactory(repository);
    }

}
