package com.rootnetapp.rootnetintranet.ui.main;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    MainActivityRepository provideMainActivityRepository(AppDatabase database, ApiInterface apiInterface) {
        return new MainActivityRepository(database, apiInterface);
    }

    @Provides
    MainActivityViewModelFactory provideMainActivityViewModelFactory(MainActivityRepository repository) {
        return new MainActivityViewModelFactory(repository);
    }

}
