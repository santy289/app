package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

public class SignatureCustomFieldsViewModel extends ViewModel {

    private SignatureCustomFieldsRepository repository;

    private WorkflowListItem workflowListItem;

    public SignatureCustomFieldsViewModel(SignatureCustomFieldsRepository repository) {
        this.repository = repository;
    }

    public void onStart(WorkflowListItem workflowListItem) {
        this.workflowListItem = workflowListItem;


    }

}
