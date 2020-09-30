package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class SignatureCustomFieldsViewModelFactory implements ViewModelProvider.Factory {

    private SignatureCustomFieldsRepository customFieldsRepository;

    public SignatureCustomFieldsViewModelFactory(SignatureCustomFieldsRepository signatureCustomFieldsRepository) {
        this.customFieldsRepository = signatureCustomFieldsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignatureCustomFieldsViewModel.class)) {
            return (T) new SignatureCustomFieldsViewModel(customFieldsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
