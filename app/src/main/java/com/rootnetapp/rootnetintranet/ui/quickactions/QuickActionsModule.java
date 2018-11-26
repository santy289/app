package com.rootnetapp.rootnetintranet.ui.quickactions;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class QuickActionsModule {
    @Provides
    QuickActionsRepository provideQuickActionsRepository(ApiInterface service, AppDatabase database) {
        return new QuickActionsRepository(service, database);
    }

    @Provides
    QuickActionsViewModelFactory provideQuickActionsViewModelFactory(QuickActionsRepository quickActionsRepository) {
        return new QuickActionsViewModelFactory(quickActionsRepository);
    }
}
