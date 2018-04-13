package com.rootnetapp.rootnetintranet.ui.timeline;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by root on 10/04/18.
 */

@Module
public class TimelineModule {
    @Provides
    TimelineRepository provideWorkflowDetailRepository(ApiInterface service) {
        return new TimelineRepository(service);
    }

    @Provides
    TimelineViewModelFactory provideWorkflowDetailViewModelFactory(TimelineRepository repository) {
        return new TimelineViewModelFactory(repository);
    }
}
