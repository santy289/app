package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 27/04/18.
 */

@Module
public class WorkflowManagerModule {

    @Provides
    WorkflowManagerRepository provideWorkflowManagerRepository(ApiInterface service, AppDatabase database) {
        return new WorkflowManagerRepository(service, database);
    }

    @Provides
    WorkflowManagerViewModelFactory provideWorkflowManagerViewModelFactory(WorkflowManagerRepository repository) {
        return new WorkflowManagerViewModelFactory(repository);
    }

}
