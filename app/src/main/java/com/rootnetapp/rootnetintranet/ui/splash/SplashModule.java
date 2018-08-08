package com.rootnetapp.rootnetintranet.ui.splash;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {
    @Provides
    static SplashRepository provideSplashRepository(ApiInterface service) {
        return new SplashRepository(service);
    }

    @Provides
    static SplashViewModelFactory provideSplashViewModelFactory(SplashRepository splashRepository) {
        return new SplashViewModelFactory(splashRepository);
    }
}
