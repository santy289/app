package com.rootnetapp.rootnetintranet.ui.quickactions;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class QuickActionsModule {

    @Provides
    QuickActionsRepository provideQuickActionsRepository(AppDatabase database, ApiInterface apiInterface) {
        return new QuickActionsRepository(database, apiInterface);
    }

    @Provides
    QuickActionsViewModelFactory provideQuickActionsViewModelFactory(QuickActionsRepository repository) {
        return new QuickActionsViewModelFactory(repository);
    }

}
