package com.rootnetapp.rootnetintranet.ui.qrtoken;

import dagger.Module;
import dagger.Provides;

@Module
public class QRTokenModule {

    @Provides
    QRTokenViewModelFactory provideQRTokenViewModelFactory() {
        return new QRTokenViewModelFactory();
    }
}
