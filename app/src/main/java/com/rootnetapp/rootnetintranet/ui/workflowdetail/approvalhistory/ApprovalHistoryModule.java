package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import dagger.Module;
import dagger.Provides;

@Module
public class ApprovalHistoryModule {
    @Provides
    ApprovalHistoryRepository provideApprovalHistoryRepository(ApiInterface service, AppDatabase database) {
        return new ApprovalHistoryRepository(service, database);
    }

    @Provides
    ApprovalHistoryViewModelFactory provideApprovalHistoryViewModelFactory(ApprovalHistoryRepository approvalHistoryRepository) {
        return new ApprovalHistoryViewModelFactory(approvalHistoryRepository);
    }
}
