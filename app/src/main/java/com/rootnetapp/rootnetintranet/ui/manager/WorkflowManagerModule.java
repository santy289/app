package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 27/04/18.
 */

@Module
public class WorkflowManagerModule {

    @Provides
    WorkflowManagerRepository provideWorkflowManagerRepository(ApiInterface service) {
        return new WorkflowManagerRepository(service);
    }

    @Provides
    WorkflowManagerViewModelFactory provideWorkflowManagerViewModelFactory(WorkflowManagerRepository repository) {
        return new WorkflowManagerViewModelFactory(repository);
    }

}
