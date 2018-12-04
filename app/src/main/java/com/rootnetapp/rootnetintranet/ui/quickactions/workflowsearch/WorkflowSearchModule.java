package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class WorkflowSearchModule {
    @Provides
    WorkflowSearchRepository provideWorkflowSearchRepository(ApiInterface service) {
        return new WorkflowSearchRepository(service);
    }

    @Provides
    WorkflowSearchViewModelFactory provideWorkflowSearchViewModelFactory(WorkflowSearchRepository workflowSearchRepository) {
        return new WorkflowSearchViewModelFactory(workflowSearchRepository);
    }
}
