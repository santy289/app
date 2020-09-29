package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureTemplate;
import com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateMenuItem;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SignatureViewModel extends ViewModel {

    private MediatorLiveData<SignatureTemplateState> signatureTemplateState;
    private MutableLiveData<SignatureSignersState> signatureSignersState;

    private SignatureRepository signatureRepository;
    private final CompositeDisposable disposables;
    private int workflowTypeId;
    private int workflowId;
    private List<SignatureTemplateMenuItem> cachedMenuItems;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
        this.disposables = new CompositeDisposable();
        this.signatureTemplateState = new MediatorLiveData<>();
        this.signatureSignersState = new MutableLiveData<>();
        this.cachedMenuItems = new ArrayList<>();
    }

    LiveData<SignatureTemplateState> getSignatureTemplateState() {
        return signatureTemplateState;
    }

    LiveData<SignatureSignersState> getSignatureSignerState() {
        return signatureSignersState;
    }

    private void testTemplateUi() {
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

    public void onStart(String token, int workflowTypeId, int workflowId) {
        this.workflowTypeId = workflowTypeId;
        this.workflowId = workflowId;
        setupTemplatesContent(workflowTypeId, workflowId);
        refreshTemplateContentFromNetwork(token, workflowTypeId, workflowId);
        noSignersFound();
    }

    private void refreshTemplateContentFromNetwork(String token, int workflowTypeId, int workflowId) {
        Disposable disposable = signatureRepository.getTemplatesBy(token, workflowTypeId, workflowId)
                .subscribe(this::refreshOnSuccess, this::onFailure);
        disposables.add(disposable);
    }

    private void refreshOnSuccess(TemplatesResponse templatesResponse) {
        List<TemplateSignature> templateSignatures = new ArrayList<>();
        TemplateSignature templateSignature;
        for (SignatureTemplate signatureTemplate : templatesResponse.getResponse()) {
            templateSignature = new TemplateSignature(
                    signatureTemplate.getTemplateId(),
                    this.workflowTypeId,
                    this.workflowId,
                    signatureTemplate.getName(),
                    signatureTemplate.getDocumentStatus(),
                    signatureTemplate.getTemplateStatus()
            );
            templateSignatures.add(templateSignature);
        }

        Disposable disposable = signatureRepository.saveTemplates(templateSignatures).subscribe();
        disposables.add(disposable);
    }

    private void onFailure(Throwable throwable) {
        int test = 1;
    }


    private void setupTemplatesContent(int workflowTypeId, int workflowId) {
        signatureTemplateState.addSource(
                signatureRepository.getAllTemplatesBy(workflowTypeId, workflowId),
                templateSignatures -> {
                    if (templateSignatures == null || templateSignatures.size() == 0) {
                        noTemplatesFound();
                        return;
                    }
                    ArrayList<SignatureTemplateMenuItem> templateMenuItems = new ArrayList<>();
                    ArrayList<String> templateNames = new ArrayList<>();
                    SignatureTemplateMenuItem menuItem;
                    for (TemplateSignature template : templateSignatures) {
                        menuItem = new SignatureTemplateMenuItem(template.getTemplateId(),
                                template.getName(),
                                template.getTemplateStatus(),
                                template.getDocumentStatus());
                        templateMenuItems.add(menuItem);
                        templateNames.add(menuItem.getName());
                    }
                    cachedMenuItems = templateMenuItems;
                    TemplateSignature templateSignature = templateSignatures.get(0);
                    SignatureTemplateState templateState = handleTemplateStateUsing(templateSignature, templateNames);
                    signatureTemplateState.setValue(templateState);
                });
    }

    private SignatureTemplateState handleTemplateStateUsing(TemplateSignature templateSignature, ArrayList<String> templateNames) {
        if (templateSignature.getDocumentStatus().equals("not_ready") &&
                !templateSignature.getTemplateStatus().equals("ready")) {
            return new SignatureTemplateState(
                    false,
                    false,
                    R.string.signature_initialize,
                    templateNames);
        }

        if (templateSignature.getDocumentStatus().equals("not_ready") &&
                templateSignature.getTemplateStatus().equals("ready")) {
            return new SignatureTemplateState(
                    true,
                    true,
                    R.string.signature_initialize,
                    templateNames);
        }

        if (!templateSignature.getDocumentStatus().equals("not_ready") &&
                templateSignature.getTemplateStatus().equals("ready")) {
            return new SignatureTemplateState(
                    true,
                    true,
                    R.string.signature_overwrite,
                    templateNames);
        }

        return new SignatureTemplateState(
                false,
                false,
                R.string.signature_initialize,
                templateNames);
    }

    protected void onDestroy() {
        disposables.clear();
    }

    private void noTemplatesFound() {
        ArrayList<String> templateNames = new ArrayList<>();
        SignatureTemplateState templateState = new SignatureTemplateState(
                false,
                false,
                R.string.signature_initialize,
                templateNames);
        signatureTemplateState.setValue(templateState);
    }

    private void noSignersFound() {
        SignatureSignersState signersState = new SignatureSignersState(true, null, R.string.signature_signers_message_no_signers);
        signatureSignersState.setValue(signersState);
    }
}