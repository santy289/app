package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class SignatureModule {
    @Provides
    SignatureRepository provideSignatureRepository(ApiInterface service, AppDatabase appDatabase) {
        return new SignatureRepository(service, appDatabase);
    }

    @Provides
    SignatureViewModelFactory provideSignatureViewModelFactory(SignatureRepository signatureRepository) {
        return new SignatureViewModelFactory(signatureRepository);
    }
}
