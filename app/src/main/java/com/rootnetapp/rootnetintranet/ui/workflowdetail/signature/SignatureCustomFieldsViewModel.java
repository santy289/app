package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;
import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldFormState;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;

public class SignatureCustomFieldsViewModel extends ViewModel {

    private static final String TAG = "CUSTOM_FIELDS_FORM";
    private SignatureCustomFieldsRepository repository;
    private WorkflowListItem workflowListItem;

    private MutableLiveData<DialogBoxState> dialogBoxState;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<SignatureCustomFieldFormState> customFieldsState;

    LiveData<Boolean> getShowLoadingObservable() {
        return showLoading;
    }

    LiveData<DialogBoxState> getDialogBoxStateObservable() {
        return dialogBoxState;
    }

    LiveData<SignatureCustomFieldFormState> getFieldCustomObservable() { return customFieldsState; }

    public SignatureCustomFieldsViewModel(SignatureCustomFieldsRepository repository) {
        this.repository = repository;
        this.dialogBoxState = new MutableLiveData<>();
        this.showLoading = new MutableLiveData<>();
        this.customFieldsState = new MutableLiveData<>();
    }

    public void onStart(WorkflowListItem workflowListItem, int templateId) {
        this.workflowListItem = workflowListItem;
        refreshContent(workflowListItem);
    }

    public void refreshContent(WorkflowListItem workflowListItem) {
        String jsonFieldConfig = workflowListItem.customFieldsJsonConfig;
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Fields> jsonAdapter = moshi.adapter(Fields.class);
        try {
            Fields fields = jsonAdapter.fromJson(jsonFieldConfig);
            if (fields == null
                    || fields.getCustomFields() == null
                    || fields.getCustomFields().size() < 1) {
                showErrorActionNotCompleted();
                return;
            }

            String title = "";
            if (fields.getUserRequired() != null && fields.getUserRequired().getFullName() != null) {
                title = fields.getUserRequired().getFullName();
            }

            SignatureCustomFieldFormState state = new SignatureCustomFieldFormState(
                    title,
                    fields.getCustomFields()
            );

            customFieldsState.setValue(state);
        } catch (IOException e) {
            Log.d(TAG, "refreshContent: " + e.getMessage());
        }
        String test = "do something";
    }

    public void dialogPositive(int message) {

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



}
