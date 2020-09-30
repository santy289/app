package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldFormState;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
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
    private MutableLiveData<SignatureCustomFieldFormState> customFieldsState;
    private MutableLiveData<Boolean> successGoBack;

    private SignatureCustomFieldFormState cachedState;
    private final List<Fields> cachedIncomingFields;
    private String token;
    private int templateId;

    LiveData<Boolean> getShowLoadingObservable() { return showLoading; }
    LiveData<DialogBoxState> getDialogBoxStateObservable() { return dialogBoxState; }
    LiveData<SignatureCustomFieldFormState> getFieldCustomObservable() { return customFieldsState; }
    LiveData<Boolean> getSuccessGoBackObservable() { return successGoBack; }

    public SignatureCustomFieldsViewModel(SignatureCustomFieldsRepository repository) {
        this.repository = repository;
        this.dialogBoxState = new MutableLiveData<>();
        this.showLoading = new MutableLiveData<>();
        this.customFieldsState = new MutableLiveData<>();
        this.disposables = new CompositeDisposable();
        this.cachedIncomingFields = new ArrayList<>();
        this.successGoBack = new MutableLiveData<>();
    }

    public void onStart(WorkflowListItem workflowListItem, int templateId, String token) {
        this.workflowListItem = workflowListItem;
        this.templateId = templateId;
        this.token = token;
        refreshContent(workflowListItem);
    }

    public void onDestroy() {
        disposables.clear();
    }

    public void onActionSave() {
        showLoading.setValue(true);
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

    public void refreshContent(WorkflowListItem workflowListItem) {
        showLoading.setValue(true);
        Disposable disposable = Observable.fromCallable(() -> {
            String jsonFieldConfig = workflowListItem.customFieldsJsonConfig;
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Fields> jsonAdapter = moshi.adapter(Fields.class);
            try {
                Fields fields = jsonAdapter.fromJson(jsonFieldConfig);
                if (fields == null
                        || fields.getCustomFields() == null
                        || fields.getCustomFields().size() < 1) {
                    showErrorActionNotCompleted();
                    return null;
                }

                String title = "";
                if (fields.getUserRequired() != null && fields.getUserRequired().getFullName() != null) {
                    title = fields.getUserRequired().getFullName();
                }

                cachedIncomingFields.add(fields);
                return new SignatureCustomFieldFormState(
                        title,
                        fields.getCustomFields()
                );
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

    public void dialogPositive(int message) {
        if (R.string.signature_success_initialize == message) {
            successGoBack.setValue(true);
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
}
