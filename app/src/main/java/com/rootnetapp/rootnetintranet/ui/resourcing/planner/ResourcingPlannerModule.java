package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class ResourcingPlannerModule {
    @Provides
    ResourcingPlannerRepository provideResourcingPlannerRepository(ApiInterface service, AppDatabase database) {
        return new ResourcingPlannerRepository(service);
    }

    @Provides
    ResourcingPlannerViewModelFactory provideResourcingPlannerViewModelFactory(ResourcingPlannerRepository resourcingPlannerRepository) {
        return new ResourcingPlannerViewModelFactory(resourcingPlannerRepository);
    }
}