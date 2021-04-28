package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SignatureViewModelFactory implements ViewModelProvider.Factory {

    private SignatureRepository signatureRepository;

    public SignatureViewModelFactory(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignatureViewModel.class)) {
            return (T) new SignatureViewModel(signatureRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
