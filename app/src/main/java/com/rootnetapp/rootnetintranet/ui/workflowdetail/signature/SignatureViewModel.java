package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.text.TextUtils;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSigner;
import com.rootnetapp.rootnetintranet.models.requests.signature.SignatureInitiateRequest;
import com.rootnetapp.rootnetintranet.models.responses.signature.DigitalSignature;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentListResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;
import com.rootnetapp.rootnetintranet.models.responses.signature.InitiateSigningResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureTemplateField;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureWorkflowTypeTemplate;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureWorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldShared;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignerItem;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SignatureViewModel extends ViewModel {

    private MediatorLiveData<SignatureTemplateState> signatureTemplateState;
    private MediatorLiveData<SignatureSignersState> signatureSignersState;
    private MutableLiveData<DialogBoxState> dialogBoxState;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<SignatureCustomFieldShared> goToCustomFieldsForm;

    private SignatureRepository signatureRepository;
    private final CompositeDisposable disposables;
    private int workflowTypeId;
    private int workflowId;
    private List<TemplateSignature> cachedTemplates;
    private SignatureTemplateState cachedTemplateState;
    private int cachedIndexSelected = 0;
    private List<DigitalSignature> cachedSignaturesSelected;
    private List<SignatureTemplateField> cachedRequiredFields;
    private String token;

    public SignatureViewModel(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
        this.disposables = new CompositeDisposable();
        this.signatureTemplateState = new MediatorLiveData<>();
        this.signatureSignersState = new MediatorLiveData<>();
        this.dialogBoxState = new MutableLiveData<>();
        this.showLoading = new MutableLiveData<>();
        this.cachedTemplates = new ArrayList<>();
        this.goToCustomFieldsForm = new MutableLiveData<>();
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

    LiveData<DialogBoxState> getDialogBoxStateObservable() {
        return dialogBoxState;
    }

    LiveData<SignatureCustomFieldShared> getGoToCustomFieldFormObservable() {
        return goToCustomFieldsForm;
    }

    /**
     * Starting point to update the UI for the first time.
     *
     * @param token
     * @param workflowTypeId
     * @param workflowId
     */
    public void onStart(String token, int workflowTypeId, int workflowId) {
        this.token = token;
        this.workflowTypeId = workflowTypeId;
        this.workflowId = workflowId;
        setupTemplatesContent(workflowTypeId, workflowId);
        refreshContentFromNetwork(token, workflowTypeId, workflowId);
        noSignersFound();
    }

    public void handleBackFromCustomFieldForm(boolean result) {
        if (!result) {
            return;
        }
        refreshContentFromNetwork(token, workflowTypeId, workflowId);
    }

    protected void onDestroy() {
        disposables.clear();
    }

    public void templateActionClicked() {
        // do something with the cached template state is overwrite then show dialog
        int resTitle = cachedTemplateState.getTemplateActionTitleResId();
        switch (resTitle) {
            case R.string.signature_overwrite:
                dialogBoxState.setValue(new DialogBoxState(
                        R.string.workflow_detail_signature_fragment_title,
                        R.string.signature_overwrite_warning,
                        R.string.cancel,
                        R.string.accept,
                        true
                ));
                break;
            case R.string.signature_initialize:
                handleInitiateSigning();
                break;
            default:
                showErrorActionNotCompleted();
                break;
        }
    }

    public void dialogPositive(@StringRes int message) {
        if (message == R.string.signature_overwrite_warning) {
            handleDocumentOverwrite();
        }
    }

    private void showErrorActionNotCompleted() {
        dialogBoxState.setValue(new DialogBoxState(
                R.string.workflow_detail_signature_fragment_title,
                R.string.error_action,
                R.string.cancel,
                R.string.accept,
                false
        ));
    }

    private TemplateSignature getTemplateCurrentlySelected() {
        if (cachedTemplates == null || cachedTemplates.size() < 1) {
            return null;
        }

        return cachedTemplates.get(cachedIndexSelected);
    }

    private void handleInitiateSigning() {
        if (cachedTemplates == null || cachedTemplates.size() < 1) {
            showErrorActionNotCompleted();
            return;
        }

        showLoading.setValue(true);

        TemplateSignature templateSignature = cachedTemplates.get(cachedIndexSelected);
        SignatureInitiateRequest initiateRequest = new SignatureInitiateRequest(
                "remote",
                templateSignature.getTemplateId(),
                workflowId
        );

        Disposable disposable = signatureRepository
                .initiateSigning(token, initiateRequest)
                .subscribe(this::initiateHandleResponse, this::onFailureNetwork);

        disposables.add(disposable);
    }

    private void initiateHandleResponse(InitiateSigningResponse response) {
        showLoading.setValue(false);

        if (response.getResponse() == null || TextUtils.isEmpty(response.getResponse().getCode())) {
            showErrorActionNotCompleted();
            return;
        }

        String code = response.getResponse().getCode();
        if (code.equals("PENDING_CUSTOM_FIELDS")) {
            handlePendingCustomFields(response.getResponse().getFields());
            return;
        }

    }

    private void handlePendingCustomFields(List<Fields> fields) {
        if (getTemplateCurrentlySelected() == null || fields == null || fields.size() < 1) {
            showErrorActionNotCompleted();
            return;
        }

        Fields list = fields.get(0);
        if (cachedRequiredFields != null) {
            list.setRequiredFields(cachedRequiredFields);
        }
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Fields> jsonAdapter = moshi.adapter(Fields.class);
        String json = jsonAdapter.toJson(list);

        TemplateSignature template = getTemplateCurrentlySelected();
        SignatureCustomFieldShared customFieldShared = new SignatureCustomFieldShared(
                template.getTemplateId(),
                json
        );

        goToCustomFieldsForm.setValue(customFieldShared);

    }

    private void handleDocumentOverwrite() {
        if (cachedTemplateState == null || cachedTemplates == null || cachedTemplates.size() < 1) {
            return;
        }

        showLoading.setValue(true);
        TemplateSignature templateSignature = cachedTemplates.get(cachedIndexSelected);
        Disposable disposable = signatureRepository
                .overwriteDocument(token, workflowId, templateSignature.getTemplateId())
                .subscribe(response -> {
                    showLoading.setValue(false);
                    refreshContentFromNetwork(token, workflowTypeId, workflowId);
                }, throwable -> {
                    showLoading.setValue(false);
                    showErrorActionNotCompleted();
                });
        disposables.add(disposable);
    }

    /**
     * This function manages the click event when the user chooses a menu item from the list of templates.
     *
     * @param indexSelected
     */
    public void onItemSelected(int indexSelected) {
        cachedIndexSelected = indexSelected;
        TemplateSignature template = cachedTemplates.get(indexSelected);
        SignatureTemplateState templateState = handleTemplateStateUsing(template, null);
        cachedTemplateState = templateState;
        signatureTemplateState.setValue(templateState);
        setupSignersContent(workflowTypeId, workflowId, template.getTemplateId());
    }

    /**
     * Given the token, workflowTypeId, workflowId it will fetch from the network the templates,
     * and documents signed if any.
     *
     * @param token
     * @param workflowTypeId
     * @param workflowId
     */
    private void refreshContentFromNetwork(String token, int workflowTypeId, int workflowId) {
        showLoading.setValue(true);
        Disposable disposable = signatureRepository.getTemplatesBy(token, workflowTypeId, workflowId)
                .doOnNext(this::refreshOnSuccess)
                .flatMap(response -> signatureRepository.getTemplatesInWorkflowType(token,workflowTypeId))
                .doOnNext(this::workflowTypeTemplateSuccess)
                .flatMap(response -> signatureRepository.getSignatureDocuments(token, workflowId))
                .doOnNext(this::refreshDocumentOnSuccess)
                .subscribe(response -> showLoading.setValue(false), this::onFailureNetwork);
        disposables.add(disposable);
    }

    private void workflowTypeTemplateSuccess(SignatureWorkflowTypesResponse response) {
        if (response.getResult() == null || response.getResult().size() < 1) {
            return;
        }

        List<SignatureWorkflowTypeTemplate> templates = response.getResult();
        SignatureWorkflowTypeTemplate template = templates.get(0);

        if(template.getSignaturesSelected() != null) {
            cachedSignaturesSelected = template.getSignaturesSelected();
        }

        if(template.getRequiredFields() != null) {
            cachedRequiredFields = template.getRequiredFields();
        }
    }

    private void refreshDocumentOnSuccess(DocumentListResponse documentListResponse) {
        if (documentListResponse.getResponse() == null || documentListResponse.getResponse().size() < 1) {
            return;
        }

        Disposable disposable = signatureRepository
                .saveSignatureDocuments(documentListResponse, workflowTypeId, workflowId)
                .subscribe();
        disposables.add(disposable);
    }

    private void refreshOnSuccess(TemplatesResponse templatesResponse) {
        if (templatesResponse.getResponse() == null || templatesResponse.getResponse().size() < 1) {
            return;
        }

        Disposable disposable = signatureRepository
                .processAndTemplateResponse(templatesResponse, workflowId, workflowTypeId).
                subscribe();
        disposables.add(disposable);
    }

    /**
     * Handles if during the networking request something fails. This is used for any network
     * failures.
     *
     * @param throwable
     */
    private void onFailureNetwork(Throwable throwable) {
        showLoading.setValue(false);
        int test = 1;
    }

    private void onFailure(Throwable throwable) {
        int test = 1;
    }

    private void setupSignersContent(int workflowTypeId, int workflowId, int templateId) {
        signatureSignersState.addSource(
                signatureRepository.getAllSignersBy(workflowTypeId, workflowId, templateId),
                templateSignerList -> {
                    //processDatabaseResultToUiModel(templateSignerList);
                    SignerItem signerItem;
                    List<SignerItem> listSigners = new ArrayList<>();
                    for (TemplateSigner templateSigner : templateSignerList) {
                        signerItem = new SignerItem(null,
                                templateSigner.getFullName(),
                                templateSigner.isReady(),
                                templateSigner.isExternalUser() ? "External" : "System",
                                templateSigner.getRole(),
                                templateSigner.getOperationTime());
                        listSigners.add(signerItem);
                    }

                    SignatureSignersState state = new SignatureSignersState(
                            false,
                            listSigners,
                            R.string.signature_signers_message_no_signers);
                    signatureSignersState.setValue(state);
                }
        );
    }

    private void setupTemplatesContent(int workflowTypeId, int workflowId) {
        signatureTemplateState.addSource(
                signatureRepository.getAllTemplatesBy(workflowTypeId, workflowId),
                templateSignatures -> {
                    if (templateSignatures == null || templateSignatures.size() == 0) {
                        noTemplatesFound();
                        return;
                    }
                    ArrayList<String> templateNames = new ArrayList<>();
                    for (TemplateSignature template : templateSignatures) {
                        templateNames.add(template.getName());
                    }
                    cachedTemplates = templateSignatures;

                    // not choosing anything by default
                    SignatureTemplateState state = new SignatureTemplateState(true,
                            false,
                            R.string.signature_initialize,
                            templateNames);
                    cachedTemplateState = state;
                    signatureTemplateState.setValue(state);
                });
    }

    /**
     * This function handles all the logic behind of how to update the templates select box, and
     * action button.
     *
     * @param templateSignature
     * @param templateNames
     * @return
     */
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

    /**
     * This function will disable the menu box, and also the action button for the templates.
     */
    private void noTemplatesFound() {
        ArrayList<String> templateNames = new ArrayList<>();
        SignatureTemplateState state = new SignatureTemplateState(
                false,
                false,
                R.string.signature_initialize,
                templateNames);
        cachedTemplateState = state;
        signatureTemplateState.setValue(state);
    }

    /**
     * This function is used when we want to set a signers not found message instead of the signers
     * list.
     */
    private void noSignersFound() {
        SignatureSignersState state = new SignatureSignersState(true, null, R.string.signature_signers_message_no_signers);
        signatureSignersState.setValue(state);
    }
}