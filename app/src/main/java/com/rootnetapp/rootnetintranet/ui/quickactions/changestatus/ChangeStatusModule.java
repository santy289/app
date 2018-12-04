package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class ChangeStatusModule {

    @Provides
    ChangeStatusRepository provideChangeStatusRepository(AppDatabase database, ApiInterface apiInterface) {
        return new ChangeStatusRepository(database, apiInterface);
    }

    @Provides
    ChangeStatusViewModelFactory provideChangeStatusViewModelFactory(ChangeStatusRepository repository) {
        return new ChangeStatusViewModelFactory(repository);
    }

}
