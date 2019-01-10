package com.rootnetapp.rootnetintranet.services.background;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkflowManagerServiceModule {

    @Provides
    WorkflowManagerServiceRepository provideWorkflowManagerRepository(ApiInterface service) {
        return new WorkflowManagerServiceRepository(service);
    }

}