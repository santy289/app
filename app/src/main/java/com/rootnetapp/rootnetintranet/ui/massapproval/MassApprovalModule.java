package com.rootnetapp.rootnetintranet.ui.massapproval;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class MassApprovalModule {
    @Provides
    MassApprovalRepository provideMassApprovalRepository(ApiInterface service, AppDatabase database) {
        return new MassApprovalRepository(service, database);
    }

    @Provides
    MassApprovalViewModelFactory provideMassApprovalViewModelFactory(MassApprovalRepository massApprovalRepository) {
        return new MassApprovalViewModelFactory(massApprovalRepository);
    }
}
