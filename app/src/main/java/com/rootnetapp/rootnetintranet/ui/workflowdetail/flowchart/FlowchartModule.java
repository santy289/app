package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class FlowchartModule {

    @Provides
    FlowchartRepository provideChangeStatusRepository(AppDatabase database, ApiInterface apiInterface) {
        return new FlowchartRepository(database, apiInterface);
    }

    @Provides
    FlowchartViewModelFactory provideChangeStatusViewModelFactory(FlowchartRepository repository) {
        return new FlowchartViewModelFactory(repository);
    }

}
