package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;

import java.util.ArrayList;

public class SignatureViewModel extends ViewModel {

    private MutableLiveData<SignatureTemplateState> signatureTemplateState = new MutableLiveData<>();
    private MutableLiveData<SignatureSignersState> signatureSignersState = new MutableLiveData<>();

    private SignatureRepository signatureRepository;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    LiveData<SignatureTemplateState> getSignatureTemplateState() {
        return signatureTemplateState;
    }
    LiveData<SignatureSignersState> getSignatureSignerState() {
        return signatureSignersState;
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

        noSignersFound();
    }

    private void refreshContent(SignatureTemplateState templateState) {
        signatureTemplateState.setValue(templateState);
    }

    private void noSignersFound() {
        SignatureSignersState signersState = new SignatureSignersState(true, null, R.string.signature_signers_message_no_signers);
        signatureSignersState.setValue(signersState);
    }
}