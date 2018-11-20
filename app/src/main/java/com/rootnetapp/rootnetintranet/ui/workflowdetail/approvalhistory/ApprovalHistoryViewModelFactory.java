package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ApprovalHistoryViewModelFactory implements ViewModelProvider.Factory {

    private ApprovalHistoryRepository approvalHistoryRepository;

    public ApprovalHistoryViewModelFactory(ApprovalHistoryRepository approvalHistoryRepository) {
        this.approvalHistoryRepository = approvalHistoryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ApprovalHistoryViewModel.class)) {
            return (T) new ApprovalHistoryViewModel(approvalHistoryRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}