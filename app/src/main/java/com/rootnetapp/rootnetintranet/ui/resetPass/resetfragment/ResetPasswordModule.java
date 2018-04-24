package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Propietario on 12/03/2018.
 */

@Module
public class ResetPasswordModule {

    @Provides
    ResetPasswordRepository provideResetRepository(ApiInterface service) {
        return new ResetPasswordRepository(service);
    }

    @Provides
    ResetPasswordViewModelFactory provideResetViewModelFactory(ResetPasswordRepository resetPasswordRepository) {
        return new ResetPasswordViewModelFactory(resetPasswordRepository);
    }
}
