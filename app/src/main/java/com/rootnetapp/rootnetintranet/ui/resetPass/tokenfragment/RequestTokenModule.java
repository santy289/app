package com.rootnetapp.rootnetintranet.ui.resetPass.tokenfragment;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Propietario on 12/03/2018.
 */

@Module
public class RequestTokenModule {

    @Provides
    RequestTokenRepository provideRequestRepository(ApiInterface service) {
        return new RequestTokenRepository(service);
    }

    @Provides
    RequestTokenViewModelFactory provideRequestViewModelFactory(RequestTokenRepository requestTokenRepository) {
        return new RequestTokenViewModelFactory(requestTokenRepository);
    }
}
