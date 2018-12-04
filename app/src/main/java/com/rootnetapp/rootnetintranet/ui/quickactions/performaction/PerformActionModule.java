package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class PerformActionModule {
    @Provides
    PerformActionRepository providePerformActionRepository(ApiInterface service) {
        return new PerformActionRepository(service);
    }

    @Provides
    PerformActionViewModelFactory providePerformActionModelFactory(PerformActionRepository performActionRepository) {
        return new PerformActionViewModelFactory(performActionRepository);
    }
}
