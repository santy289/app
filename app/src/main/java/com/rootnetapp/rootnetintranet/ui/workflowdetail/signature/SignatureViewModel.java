package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;

import java.util.ArrayList;

public class SignatureViewModel extends ViewModel {

    private MutableLiveData<SignatureTemplateState> signatureTemplateState = new MutableLiveData<>();
    private SignatureRepository signatureRepository;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    LiveData<SignatureTemplateState> getSignatureTemplateState() {
        return signatureTemplateState;
    }

    public void onStart() {
        ArrayList<String> templateItems = new ArrayList<>();
        templateItems.add("Item 1");
        templateItems.add("Item 2");
        templateItems.add("Item 3");
        templateItems.add("Item 4");

        SignatureTemplateState state = new SignatureTemplateState(
                true,
                true,
                R.string.signature_initialize,
                templateItems);

        signatureTemplateState.setValue(state);
    }

    public void refreshContent(SignatureTemplateState templateState) {
        signatureTemplateState.setValue(templateState);
    }

}