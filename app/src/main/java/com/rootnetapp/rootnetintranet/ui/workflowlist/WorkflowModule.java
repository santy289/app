package com.rootnetapp.rootnetintranet.ui.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 19/03/18.
 */

@Module
public class WorkflowModule {

    @Provides
    WorkflowRepository provideWorkflowRepository(ApiInterface service, AppDatabase database) {
        return new WorkflowRepository(service, database);
    }

    @Provides
    WorkflowViewModelFactory provideWorkflowViewModelFactory(WorkflowRepository workflowRepository) {
        return new WorkflowViewModelFactory(workflowRepository);
    }
}
