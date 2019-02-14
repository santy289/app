package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkflowSearchModule {
    @Provides
    WorkflowSearchRepository provideWorkflowSearchRepository(ApiInterface service, AppDatabase database) {
        return new WorkflowSearchRepository(service, database);
    }

    @Provides
    WorkflowSearchViewModelFactory provideWorkflowSearchViewModelFactory(WorkflowSearchRepository workflowSearchRepository) {
        return new WorkflowSearchViewModelFactory(workflowSearchRepository);
    }
}
