package com.rootnetapp.rootnetintranet.ui.quickactions;

import com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.ApprovalHistoryRepository;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.ApprovalHistoryViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class QuickActionsViewModelFactory implements ViewModelProvider.Factory {

    private QuickActionsRepository quickActionsRepository;

    public QuickActionsViewModelFactory(QuickActionsRepository quickActionsRepository) {
        this.quickActionsRepository = quickActionsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ApprovalHistoryViewModel.class)) {
            return (T) new QuickActionsViewModel(quickActionsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}