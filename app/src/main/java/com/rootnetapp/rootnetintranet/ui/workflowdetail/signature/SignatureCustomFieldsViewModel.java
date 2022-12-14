package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;
import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureTemplateField;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldFormState;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignatureCustomFieldsViewModel extends ViewModel {
    private static final String TAG = "CUSTOM_FIELDS_FORM";
    private SignatureCustomFieldsRepository repository;
    private WorkflowListItem workflowListItem;
    private final CompositeDisposable disposables;

    private MutableLiveData<DialogBoxState> dialogBoxState;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<List<SignatureCustomFieldFormState>> customFieldsState;
    private MutableLiveData<Boolean> successGoBack;

    private List<SignatureCustomFieldFormState> cachedState;
    private final List<Fields> cachedIncomingFields;
    private List<SignatureTemplateField> cachedRequiredFields;
    private String token;
    private int templateId;

    LiveData<Boolean> getShowLoadingObservable() { return showLoading; }
    LiveData<DialogBoxState> getDialogBoxStateObservable() { return dialogBoxState; }
    LiveData<List<SignatureCustomFieldFormState>> getFieldCustomObservable() { return customFieldsState; }
    LiveData<Boolean> getSuccessGoBackObservable() { return successGoBack; }

    public SignatureCustomFieldsViewModel(SignatureCustomFieldsRepository repository) {
        this.repository = repository;
        this.dialogBoxState = new MutableLiveData<>();
        this.showLoading = new MutableLiveData<>();
        this.customFieldsState = new MutableLiveData<>();
        this.disposables = new CompositeDisposable();
        this.cachedIncomingFields = new ArrayList<>();
        this.successGoBack = new MutableLiveData<>();
        this.cachedRequiredFields = new ArrayList<>();
    }

    /**
     * Starting point for the view model.
     *
     * @param workflowListItem
     * @param templateId
     * @param token
     */
    public void onStart(WorkflowListItem workflowListItem, int templateId, String token) {
        this.workflowListItem = workflowListItem;
        this.templateId = templateId;
        this.token = token;
        refreshContent(workflowListItem);
    }

    public void onDestroy() {
        disposables.clear();
    }

    /**
     * This functions handles the save button click on the UI.
     */
    public void onActionSave() {
        showLoading.setValue(true);

        if (!isValidForm()) {
            showLoading.setValue(false);
            return;
        }

        Disposable disposable = repository
                .initializeWithCustomFields(
                        token,
                        "remote",
                        templateId,
                        workflowListItem.workflowId,
                        cachedIncomingFields)
                .subscribe(this::signatureInitiateSuccessful,
                throwable -> {
                    showLoading.setValue(false);
                    showErrorActionNotCompleted();
                });
        disposables.add(disposable);
    }

    /**
     * Validates the form where the custom form can't have no fields, and required fields can't be null.
     * Also it will compare the required fields against the form state fields at this point before
     * sending a request.
     *
     * @return
     */
    private boolean isValidForm() {
        if (cachedState == null
                || cachedState.size() < 1) {
            return false;
        }

        if (cachedRequiredFields == null || cachedRequiredFields.size() < 1) {
            // Nothing to validate we can accept the form
            return true;
        }

        boolean formIsValid = true;

        List<SignatureCustomFieldFormState> newValues = new ArrayList<>();
        SignatureCustomFieldFormState state;
        for (int i = 0; i < cachedState.size(); i++) {
            state = cachedState.get(i);
            List<FieldCustom> customFields = state.getFieldCustomList();
            for (SignatureTemplateField cachedRequiredField : cachedRequiredFields) {
                for (FieldCustom customField : customFields) {
                    if (!customField.getName().equals(cachedRequiredField.getName())) {
                        continue;
                    }
                    if (!TextUtils.isEmpty(customField.getCustomValue())) {
                        continue;
                    }
                    customField.setValid(false);
                    formIsValid = false;
                }
            }

        }

        if (!formIsValid) {
            SignatureCustomFieldFormState newState = new SignatureCustomFieldFormState(
                    "",
                    null
            );
            newValues.add(newState);
            customFieldsState.setValue(newValues);
        }
        return formIsValid;
    }

    /**
     * Handles a successful document initiation.
     *
     * @param object
     */
    private void signatureInitiateSuccessful(Object object) {
        showLoading.setValue(false);
        dialogBoxState.setValue(new DialogBoxState(
                R.string.workflow_detail_signature_fragment_title,
                R.string.signature_success_initialize,
                R.string.cancel,
                R.string.accept,
                false
        ));
    }

    /**
     * This function works in the background to parse the json string form configuration, and
     * emits a form state update to the UI.
     *
     * @param workflowListItem
     */
    public void refreshContent(WorkflowListItem workflowListItem) {
        showLoading.setValue(true);
        Disposable disposable = Observable.fromCallable(() -> {
            String jsonFieldConfig = workflowListItem.customFieldsJsonConfig;
            Moshi moshi = new Moshi.Builder().build();
            ParameterizedType listData = Types.newParameterizedType(List.class, Fields.class);
            JsonAdapter<List<Fields>> jsonAdapter = moshi.adapter(listData);

            try {

                List<Fields> fields = jsonAdapter.fromJson(jsonFieldConfig);
                if (fields == null
                        || fields.size() < 1) {
                    showErrorActionNotCompleted();
                    return null;
                }

                List<SignatureCustomFieldFormState> recyclerViewItems = new ArrayList<>();
                for (Fields fieldGroup : fields) {

                    String title = "";
                    if (fieldGroup.getUserRequired() != null && fieldGroup.getUserRequired().getFullName() != null) {
                        title = fieldGroup.getUserRequired().getFullName();
                    }

                    if (fieldGroup.getRequiredFields() != null) {
                        cachedRequiredFields = fieldGroup.getRequiredFields();
                    }

                    cachedIncomingFields.add(fieldGroup);

                    recyclerViewItems.add(new SignatureCustomFieldFormState(
                            title,
                            fieldGroup.getCustomFields()
                    ));
                }

                return recyclerViewItems;
            } catch (IOException e) {
                Log.d(TAG, "refreshContent: " + e.getMessage());
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(state -> {
            showLoading.setValue(false);
            if (state == null) {
                return;
            }
            cachedState = state;
            customFieldsState.setValue(state);
        }, throwable -> {
            showLoading.setValue(false);
            showErrorActionNotCompleted();
        });

        disposables.add(disposable);
    }

    /**
     * Handles a positive click on the dialog box.
     *
     * @param message
     */
    public void dialogPositive(int message) {
        if (R.string.signature_success_initialize == message) {
            successGoBack.setValue(true);
        }
    }

    /**
     * Displays a dialog box with an error message.
     */
    private void showErrorActionNotCompleted() {
        dialogBoxState.setValue(new DialogBoxState(
                R.string.workflow_detail_signature_fragment_title,
                R.string.error_action,
                R.string.cancel,
                R.string.accept,
                false
        ));
    }
}
