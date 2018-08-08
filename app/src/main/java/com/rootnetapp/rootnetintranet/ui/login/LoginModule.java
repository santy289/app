package com.rootnetapp.rootnetintranet.ui.login;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    @Provides
    LoginRepository provideLoginRepository(ApiInterface service) {
        return new LoginRepository(service);
    }

    @Provides
    LoginViewModelFactory provideLoginViewModelFactory(LoginRepository loginRepository) {
        return new LoginViewModelFactory(loginRepository);
    }
}
