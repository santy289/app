package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 02/04/18.
 */

@Module
public class WorkflowDetailModule {
    @Provides
    WorkflowDetailRepository provideWorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        return new WorkflowDetailRepository(service, database);
    }

    @Provides
    WorkflowDetailViewModelFactory provideWorkflowDetailViewModelFactory(WorkflowDetailRepository workflowDetailRepository) {
        return new WorkflowDetailViewModelFactory(workflowDetailRepository);
    }
}
