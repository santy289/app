package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class SignatureCustomFieldsModule {

    @Provides
    SignatureCustomFieldsRepository provideSignatureCustomFieldsRepository(ApiInterface service, AppDatabase appDatabase) {
        return new SignatureCustomFieldsRepository(service, appDatabase);
    }

    @Provides
    SignatureCustomFieldsViewModelFactory provideSignatureCustomFieldsViewModelFactory(SignatureCustomFieldsRepository repository) {
        return new SignatureCustomFieldsViewModelFactory(repository);
    }

}
