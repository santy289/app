package com.rootnetapp.rootnetintranet.ui.qrtoken;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class QRTokenModule {
    @Provides
    QRTokenRepository provideQRTokenRepository(ApiInterface service) {
        return new QRTokenRepository(service);
    }

    @Provides
    QRTokenViewModelFactory provideQRTokenViewModelFactory(QRTokenRepository repository) {
        return new QRTokenViewModelFactory(repository);
    }
}
