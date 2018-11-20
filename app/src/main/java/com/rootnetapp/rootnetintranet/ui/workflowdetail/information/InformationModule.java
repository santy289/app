package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class InformationModule {
    @Provides
    InformationRepository provideInformationRepository(ApiInterface service) {
        return new InformationRepository(service);
    }

    @Provides
    InformationViewModelFactory provideInformationViewModelFactory(InformationRepository informationRepository) {
        return new InformationViewModelFactory(informationRepository);
    }
}
