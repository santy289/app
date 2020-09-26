package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.ViewModel;

public class SignatureViewModel extends ViewModel {

    private SignatureRepository signatureRepository;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }
}