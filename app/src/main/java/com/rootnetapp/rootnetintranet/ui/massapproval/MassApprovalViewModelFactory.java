package com.rootnetapp.rootnetintranet.ui.massapproval;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailRepository;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailViewModel;

/**
 * Created by root on 02/04/18.
 */

public class MassApprovalViewModelFactory implements ViewModelProvider.Factory {

    private MassApprovalRepository massApprovalRepository;

    public MassApprovalViewModelFactory(MassApprovalRepository massApprovalRepository){
        this.massApprovalRepository = massApprovalRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MassApprovalViewModel.class)) {
            return (T) new MassApprovalViewModel(massApprovalRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}