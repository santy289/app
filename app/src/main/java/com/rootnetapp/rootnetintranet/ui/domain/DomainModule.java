package com.rootnetapp.rootnetintranet.ui.domain;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Propietario on 09/03/2018.
 */

@Module
public class DomainModule {

    @Provides
    DomainRepository provideDomainRepository(ApiInterface service) {
        return new DomainRepository(service);
    }

    @Provides
    DomainViewModelFactory provideDomainViewModelFactory(DomainRepository domainRepository) {
        return new DomainViewModelFactory(domainRepository);
    }


}
