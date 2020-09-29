package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSigner;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentListResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureTemplate;
import com.rootnetapp.rootnetintranet.models.responses.signature.Signer;
import com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateMenuItem;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignerItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SignatureViewModel extends ViewModel {

    private MediatorLiveData<SignatureTemplateState> signatureTemplateState;
    private MutableLiveData<SignatureSignersState> signatureSignersState;
    private MutableLiveData<Boolean> showLoading;

    private SignatureRepository signatureRepository;
    private final CompositeDisposable disposables;
    private int workflowTypeId;
    private int workflowId;
    private List<SignatureTemplateMenuItem> cachedMenuItems;
    private List<TemplateSignature> cachedTemplates;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
        this.disposables = new CompositeDisposable();
        this.signatureTemplateState = new MediatorLiveData<>();
        this.signatureSignersState = new MutableLiveData<>();
        this.showLoading = new MutableLiveData<>();
        this.cachedMenuItems = new ArrayList<>();
        this.cachedTemplates = new ArrayList<>();
    }

    LiveData<SignatureTemplateState> getSignatureTemplateState() {
        return signatureTemplateState;
    }

    LiveData<SignatureSignersState> getSignatureSignerState() {
        return signatureSignersState;
    }

    LiveData<Boolean> getShowLoadingObservable() {
        return showLoading;
    }

    public void onStart(String token, int workflowTypeId, int workflowId) {
        this.workflowTypeId = workflowTypeId;
        this.workflowId = workflowId;
        setupTemplatesContent(workflowTypeId, workflowId);
        refreshContentFromNetwork(token, workflowTypeId, workflowId);
        noSignersFound();
    }

    private void refreshContentFromNetwork(String token, int workflowTypeId, int workflowId) {
        showLoading.setValue(true);
        Disposable disposable = signatureRepository.getTemplatesBy(token, workflowTypeId, workflowId)
                .doOnNext(this::refreshOnSuccess)
                .flatMap(response -> signatureRepository.getSignatureDocuments(token, workflowId))
                .doOnNext(this::refreshDocumentOnSuccess)
                .subscribe(response -> showLoading.setValue(false), this::onFailureNetwork);
        disposables.add(disposable);
    }

    private void refreshDocumentOnSuccess(DocumentListResponse documentListResponse) {
        // do something
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

            List<Signer> signers = signatureTemplate.getUsers();
            if (signers == null || signers.size() < 1) {
                continue;
            }

            List<TemplateSigner> templateSignerList = new ArrayList<>();
            TemplateSigner templateSigner;
            for (Signer signer : signers) {
                templateSigner = new TemplateSigner(
                        signer.getId(),
                        this.workflowId,
                        this.workflowTypeId,
                        signatureTemplate.getTemplateId(),
                        signer.isEnabled(),
                        signer.isFieldUser(),
                        signer.getDetails().getFirstName(),
                        signer.getDetails().getLastName(),
                        signer.getDetails().isExternalUser(),
                        signer.getDetails().getEmail(),
                        signer.getDetails().getRole(),
                        signer.getDetails().getFullName()
                );
                templateSignerList.add(templateSigner);
            }
            Disposable disposable = signatureRepository.saveSigners(templateSignerList).subscribe();
            disposables.add(disposable);
        }
        Disposable disposable = signatureRepository.saveTemplates(templateSignatures).subscribe();
        disposables.add(disposable);
    }

    private void onFailureNetwork(Throwable throwable) {
        showLoading.setValue(false);
        int test = 1;
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
                    cachedTemplates = templateSignatures;

                    // not choosing anything by default
                    SignatureTemplateState templateState = new SignatureTemplateState(true,
                            false,
                            R.string.signature_initialize,
                            templateNames);
                    signatureTemplateState.setValue(templateState);
                });
    }

    private void onSuccessSigners(List<TemplateSigner> templateSignerList) {
        if (templateSignerList == null || templateSignerList.size() < 1) {
            noSignersFound();
            return;
        }

        SignerItem signerItem;
        List<SignerItem> listSigners = new ArrayList<>();
        for (TemplateSigner templateSigner : templateSignerList) {
            signerItem = new SignerItem(null,
                    templateSigner.getFullName(),
                    false,
                    templateSigner.isExternalUser() ? "External" : "System",
                    templateSigner.getRole(),
                    "Signature date:");
            listSigners.add(signerItem);
        }

        SignatureSignersState state = new SignatureSignersState(
                false,
                listSigners,
                R.string.signature_signers_message_no_signers);
        signatureSignersState.setValue(state);
    }

    public void onItemSelected(int indexSelected) {
        SignatureTemplateMenuItem item = cachedMenuItems.get(indexSelected);
        TemplateSignature template = cachedTemplates.get(indexSelected);
        SignatureTemplateState templateState = handleTemplateStateUsing(template, null);
        signatureTemplateState.setValue(templateState);
        Disposable disposable = signatureRepository
                .getAllSigners(workflowTypeId, workflowId, item.getTemplateId())
                .subscribe(this::onSuccessSigners, this::onFailure);
        disposables.add(disposable);
    }

    public void onItemNotSelected() {
        noSignersFound();
        SignatureTemplateState templateState = new SignatureTemplateState(
                true,
                false,
                R.string.signature_initialize,
                null
        );
        signatureTemplateState.setValue(templateState);
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