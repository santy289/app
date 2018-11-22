package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class StatusModule {
    @Provides
    StatusRepository provideStatusRepository(ApiInterface service, AppDatabase database) {
        return new StatusRepository(service, database);
    }

    @Provides
    StatusViewModelFactory provideStatusViewModelFactory(StatusRepository statusRepository) {
        return new StatusViewModelFactory(statusRepository);
    }
}
