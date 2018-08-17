package com.rootnetapp.rootnetintranet.ui.main;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;

import dagger.Module;
import dagger.Provides;

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
