package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 22/03/18.
 */

@Module
public class CreateWorkflowModule {

    @Provides
    CreateWorkflowRepository provideCreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        return new CreateWorkflowRepository(service, database);
    }

    @Provides
    CreateWorkflowViewModelFactory provideCreateWorkflowViewModelFactory(CreateWorkflowRepository repository) {
        return new CreateWorkflowViewModelFactory(repository);
    }

}
