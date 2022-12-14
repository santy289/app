package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.DefaultRoleApprover;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.CurrencyFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.FileMetaData;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePost;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePostDetail;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostBusinessOpportunity;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostContact;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostCurrency;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostPhone;
import com.rootnetapp.rootnetintranet.models.createworkflow.PostSubContact;
import com.rootnetapp.rootnetintranet.models.createworkflow.ProductFormList;
import com.rootnetapp.rootnetintranet.models.createworkflow.StatusSpecific;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.AutocompleteFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseOption;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BooleanFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.CurrencyFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DateFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DisplayFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DoubleMultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DoubleOption;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.FileFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.GeolocationFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.IntentFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.MultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.PhoneFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem.InputType;
import com.rootnetapp.rootnetintranet.models.createworkflow.geolocation.GeolocationMetaData;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.business.BusinessOpportunitiesResponse;
import com.rootnetapp.rootnetintranet.models.responses.business.BusinessOpportunity;
import com.rootnetapp.rootnetintranet.models.responses.contact.Contact;
import com.rootnetapp.rootnetintranet.models.responses.contact.ContactsResponse;
import com.rootnetapp.rootnetintranet.models.responses.contact.SubContact;
import com.rootnetapp.rootnetintranet.models.responses.contact.SubContactsResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.FileUploadResponse;
import com.rootnetapp.rootnetintranet.models.responses.downloadfile.DownloadFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.project.Project;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.SelectedLocation;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static android.app.Activity.RESULT_OK;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_DEFINE_SPECIFIC;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_MY_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_OWN;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_ACTIVE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_CURRENT_STATUS;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_DESCRIPTION;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_END_DATE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_KEY;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_OWNER;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_START_DATE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_STATUS;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_TITLE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_TYPE;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_ACCOUNT;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_BUSINESS_OPPORTUNITY;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.TYPE_CONTACT;

public class CreateWorkflowViewModel extends ViewModel {

    protected static final int REQUEST_FILE_TO_ATTACH = 27;
    protected static final int REQUEST_GEOLOCATION = 28;
    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 72;
    protected static final int TAG_WORKFLOW_TYPE = 80;
    protected static final int TAG_PEOPLE_INVOLVED = 2772;
    protected static final int TAG_OWNER = 2773;
    protected static final int TAG_ADDITIONAL_PROFILES = 2774;
    protected static final int TAG_GLOBAL_APPROVERS = 2775;
    protected static final int TAG_SPECIFIC_APPROVERS = 2776;

    private static final int TAG_STANDARD_TITLE = 200;
    private static final int TAG_STANDARD_KEY = 201;
    private static final int TAG_STANDARD_DESCRIPTION = 202;
    private static final int TAG_STANDARD_START_DATE = 203;
    private static final int TAG_STANDARD_END_DATE = 204;
    private static final int TAG_STANDARD_STATUS = 205;
    private static final int TAG_STANDARD_CURRENT_STATUS = 206;
    private static final int TAG_STANDARD_OWNER = 207;
    private static final int TAG_STANDARD_TYPE = 208;
    private static final int TOTAL_STANDARD_FIELDS = 7;

    public static final int TAG_STANDARD_ACTIVE = 467;

    protected static final int FORM_BASE_INFO = 1;
    protected static final int FORM_PEOPLE_INVOLVED = 2;

    private static final String ENTITY_ROLE = "role";
    private final int UPLOAD_FILE_SIZE_LIMIT = 10;

    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<SingleChoiceFormItem> mAddWorkflowTypeItemLiveData;
    private MutableLiveData<IntentFormItem> mAddPeopleInvolvedItemLiveData;
    private MutableLiveData<BaseFormItem> mAddFormItemLiveData;
    private MutableLiveData<List<BaseFormItem>> mSetFormItemListLiveData;
    private MutableLiveData<BaseFormItem> mAddPeopleInvolvedFormItemLiveData;
    private MutableLiveData<List<BaseFormItem>> mSetPeopleInvolvedFormItemListLiveData;
    private MutableLiveData<BaseFormItem> mValidationUiLiveData;
    private MutableLiveData<DialogMessage> showDialogMessage;
    private MutableLiveData<Boolean> goBack;
    private MutableLiveData<BaseFormItem> mUpdateFormItemLiveData;
    private MutableLiveData<DownloadedFileUiData> mOpenDownloadedFileLiveData;
    private MutableLiveData<Boolean> mEnableSubmitButtonLiveData;
    private MutableLiveData<Boolean> mShowNoPermissionsViewLiveData;
    private MutableLiveData<Boolean> mShowFieldsRecyclerLiveData;
    private MutableLiveData<Boolean> mShowSubmitButtonLiveData;
    private MutableLiveData<AutocompleteFormItem> mSetAutocompleteSuggestionsLiveData;
    private MutableLiveData<Boolean> mShowAutocompleteNoConnectionLiveData;
    private MutableLiveData<Boolean> mShowDynamicFiltersNoTypeLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final CreateWorkflowRepository mRepository;

    private static final String TAG = "CreateViewModel";

    private FormSettings formSettings;

    private String mToken;
    private Integer mClientId;
    private WorkflowListItem mWorkflowListItem;
    private WorkflowDb mWorkflow;
    private final Moshi moshi;
    private FileFormItem mCurrentRequestingFileFormItem;
    private GeolocationFormItem mCurrentRequestingGeolocationFormItem;
    private List<FileFormItem> mFilesToUpload;
    private int mQueuedFile;
    private int mUserId;
    private int mFieldCount, mFieldCompleted;
    private boolean hasDefineSpecificApproverPermissions;
    private boolean hasEditPermissions;
    private List<WorkflowTypeDb> mWorkflowTypeDbList;
    private WorkflowTypeDb mSelectedWorkflowType;
    private AutocompleteFormItem mCurrentQueryAutocompleteFormItem;
    private @FormType int mFormType;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.mRepository = createWorkflowRepository;
        goBack = new MutableLiveData<>();
        moshi = new Moshi.Builder().build();
    }

    protected void setFormType(@FormType int formType) {
        this.mFormType = formType;
    }

    protected void initForm(String token, Integer clientId, @Nullable WorkflowListItem item,
                            String userId, String userPermissions) {
        showLoading.setValue(true);
        if (formSettings == null) {
            formSettings = new FormSettings();
        }
        this.mToken = token;
        this.mClientId = clientId;
        this.mWorkflowListItem = item;

        if (userId != null && !userId.isEmpty()) mUserId = Integer.parseInt(userId);

        if (!isFilterFragment()) checkPermissions(mUserId, userPermissions);

        if (mFormType == FormType.STANDARD_FILTERS) {
            showStandardFieldsOnly();
        } else {
            createWorkflowTypeItem();
        }

        if (mFormType == FormType.DYNAMIC_FILTERS) {
            mShowSubmitButtonLiveData.setValue(false);
            mShowDynamicFiltersNoTypeLiveData.setValue(true);
        }
    }

    protected void onCleared() {
        mDisposables.clear();
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link CommentsFragment}.
     * Hide the UI related to the unauthorized actions.
     *
     * @param permissionsString users permissions.
     */
    private void checkPermissions(int userId, String permissionsString) {
        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        List<String> permissionsToCheck = new ArrayList<>();

        if (mWorkflowListItem != null && mWorkflowListItem.getOwnerId() == userId) {
            permissionsToCheck.add(WORKFLOW_EDIT_MY_OWN);
            permissionsToCheck.add(WORKFLOW_EDIT_OWN);
        } else {
            permissionsToCheck.add(WORKFLOW_EDIT_ALL);
        }

        hasEditPermissions = permissionsUtils.hasPermissions(permissionsToCheck);

        hasDefineSpecificApproverPermissions = permissionsUtils
                .hasPermission(WORKFLOW_DEFINE_SPECIFIC);
    }

    protected int getFragmentTitle() {
        switch (mFormType) {
            case FormType.CREATE:
                return R.string.create_workflow;
            case FormType.EDIT:
                return R.string.edit_workflow;
            case FormType.STANDARD_FILTERS:
            case FormType.DYNAMIC_FILTERS:
            default:
                return 0;
        }
    }

    protected int getSubmitButtonText() {
        switch (mFormType) {
            case FormType.CREATE:
                return R.string.create_workflow;
            case FormType.EDIT:
                return R.string.edit_workflow;
            case FormType.STANDARD_FILTERS:
                return R.string.apply_filters;
            case FormType.DYNAMIC_FILTERS:
                return R.string.apply_dynamic_filters;
            default:
                return 0;
        }
    }

    protected boolean isFilterFragment() {
        return mFormType == FormType.STANDARD_FILTERS || mFormType == FormType.DYNAMIC_FILTERS;
    }

    private WorkflowMetas createMetaData(BaseFormItem formItem) {
        WorkflowMetas workflowMeta = new WorkflowMetas();

        int workflowTypeFieldId = formItem.getTag();
        String value = formItem.getStringValue();
        workflowMeta.setUnformattedValue(value);
        workflowMeta.setWorkflowTypeFieldId(workflowTypeFieldId);

        formSettings.formatMetaData(workflowMeta, formItem);

        return workflowMeta;
    }

    private void postWorkflow() {
        showLoading.setValue(true);

        //check if we have a file to upload first and then continue with the rest.
        uploadAllFiles();
    }

    private void startSendingWorkflow(ArrayMap<String, Integer> baseInfo,
                                      List<BaseFormItem> formItems) {
        String title = null;
        String description = null;
        String start = null;
        if (baseInfo != null) {
            Integer titleTag = baseInfo.get(FormSettings.MACHINE_NAME_TITLE);
            Integer descriptionTag = baseInfo.get(FormSettings.MACHINE_NAME_DESCRIPTION);
            Integer startTag = baseInfo.get(FormSettings.MACHINE_NAME_START_DATE);

            if (titleTag != null) {
                title = ((TextInputFormItem) formSettings.findItem(titleTag)).getValue();
            }
            if (descriptionTag != null) {
                description = ((TextInputFormItem) formSettings.findItem(descriptionTag))
                        .getValue();
            }
            if (startTag != null) {
                start = Utils.getDatePostFormat(
                        ((DateFormItem) formSettings.findItem(startTag)).getValue());
            }
        }

        int workflowTypeId = formSettings.getWorkflowTypeIdSelected();

        SingleChoiceFormItem ownerFormItem = (SingleChoiceFormItem) formSettings
                .findItem(TAG_OWNER);
        Option ownerValue = ownerFormItem == null ? null : ownerFormItem.getValue();
        Integer owner = ownerValue == null ? null : ownerValue.getId();

        List<Integer> profilesInvolved = new ArrayList<>();
        MultipleChoiceFormItem profileInvolvedFormItem = (MultipleChoiceFormItem) formSettings
                .findItem(TAG_ADDITIONAL_PROFILES);
        List<BaseOption> selectedProfileInvolvedValues = profileInvolvedFormItem == null ? new ArrayList<>() : profileInvolvedFormItem
                .getValues();
        for (BaseOption option : selectedProfileInvolvedValues) {
            profilesInvolved.add(((Option) option).getId());
        }

        List<Integer> globalApprovers = new ArrayList<>();
        MultipleChoiceFormItem globalApproversFormItem = (MultipleChoiceFormItem) formSettings
                .findItem(TAG_GLOBAL_APPROVERS);
        List<BaseOption> selectedGlobalApproversValues = globalApproversFormItem == null ? new ArrayList<>() : globalApproversFormItem
                .getValues();
        for (BaseOption option : selectedGlobalApproversValues) {
            globalApprovers.add(((Option) option).getId());
        }

        List<StatusSpecific> specificApprovers = new ArrayList<>();
        DoubleMultipleChoiceFormItem specificApprovesFormItem = (DoubleMultipleChoiceFormItem) formSettings
                .findItem(TAG_SPECIFIC_APPROVERS);
        List<BaseOption> selectedSpecificApproversValues = specificApprovesFormItem == null ? new ArrayList<>() : specificApprovesFormItem
                .getValues();
        for (BaseOption option : selectedSpecificApproversValues) {
            DoubleOption doubleOption = (DoubleOption) option;

            StatusSpecific statusSpecific = new StatusSpecific();
            statusSpecific.user = doubleOption.getFirstOption().getId();
            statusSpecific.status = doubleOption.getSecondOption().getId();

            specificApprovers.add(statusSpecific);
        }

        Map<String, Object> roleApprovers = new HashMap<>();
        // List<BaseFormItem> test = formSettings.getPeopleInvolvedFormItems(); // supposdely also this one has more roles
        for (BaseFormItem formItem : formSettings.getRoleApproversFormItems()) {
            Option roleApprover = ((SingleChoiceFormItem) formItem).getValue();
            if (roleApprover == null) continue; //not selected
            roleApprovers.put(String.valueOf(formItem.getTag()), roleApprover.getId());
        }

        List<WorkflowMetas> metas = new ArrayList<>();

        for (int i = 0; i < formItems.size(); i++) {
            BaseFormItem formItem = formItems.get(i);

            WorkflowMetas workflowMetas = createMetaData(formItem);
            if (workflowMetas == null) {
                showLoading.setValue(false);
                return;
            }

            //we allow the FileFormItem even though the value is null because of the editing mode, when the user tries to delete a file.
            if ((workflowMetas.getValue() != null && !workflowMetas.getValue().isEmpty())
                    || formItem instanceof FileFormItem) {
                metas.add(workflowMetas);
            }
        }

        // remove empty fields
        ArrayList<Integer> removeIndex = new ArrayList<>();
        WorkflowMetas testMeta;
        for (int i = 0; i < metas.size(); i++) {
            testMeta = metas.get(i);
            if (TextUtils.isEmpty(testMeta.getValue()) || testMeta.getValue()
                    .equals("0") || testMeta.getValue().equals("[]")) {
                removeIndex.add(testMeta.getWorkflowTypeFieldId());
            }
        }

        for (int i = 0; i < removeIndex.size(); i++) {
            int id = removeIndex.get(i);
            for (int j = 0; j < metas.size(); j++) {
                if (id == metas.get(j).getWorkflowTypeFieldId()) {
                    metas.remove(metas.get(j));
                    break;
                }
            }
        }

        if (mWorkflowListItem == null) {
            // new workflow
            postCreateToServer(metas, workflowTypeId, title, start, description, owner,
                    profilesInvolved, globalApprovers, specificApprovers, roleApprovers);
        } else {
            //edit workflow
            patchEditToServer(metas, mWorkflowListItem.getWorkflowId(), title, start, description,
                    owner, profilesInvolved, globalApprovers, specificApprovers, roleApprovers);
        }
    }

    private void postCreateToServer(List<WorkflowMetas> metas, int workflowTypeId, String title,
                                    String start, String description, Integer owner,
                                    List<Integer> profilesInvolved, List<Integer> globalApprovers,
                                    List<StatusSpecific> specificApprovers,
                                    Map<String, Object> roleApprovers) {

        //we cannot use a POJO for this request because the role approvers is an object with dynamic fields.
        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("workflow_type_id", workflowTypeId);
        mapBody.put("title", title);
        mapBody.put("workflow_metas", metas);
        mapBody.put("start", start);
        mapBody.put("description", description);
        if (owner != null) {
            mapBody.put("owner", owner);
        }
        mapBody.put("profilesInvolved", profilesInvolved);

        Map<String, Object> specificApproversMap = new HashMap<>();
        specificApproversMap.put("global", globalApprovers);
        specificApproversMap.put("statusSpecific", specificApprovers);
        specificApproversMap.put("role", roleApprovers);

        mapBody.put("specific_approvers", specificApproversMap);

        mEnableSubmitButtonLiveData.setValue(false);

        // Accepts object
        Disposable disposable = mRepository
                .createWorkflow(mToken, mapBody)
                .subscribe(this::onCreateSuccess, this::onCreateFailure);

        mDisposables.add(disposable);
    }

    private void patchEditToServer(List<WorkflowMetas> metas, int workflowId, String title,
                                   String start, String description, Integer owner,
                                   List<Integer> profilesInvolved, List<Integer> globalApprovers,
                                   List<StatusSpecific> specificApprovers,
                                   Map<String, Object> roleApprovers) {
        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("workflow_id", workflowId);
        mapBody.put("title", title);
        mapBody.put("workflow_metas", metas);
        mapBody.put("start", start);
        mapBody.put("description", description);
        if (owner != null) {
            mapBody.put("owner", owner);
        }
        mapBody.put("profilesInvolved", profilesInvolved);

        Map<String, Object> specificApproversMap = new HashMap<>();
        specificApproversMap.put("global", globalApprovers);
        specificApproversMap.put("statusSpecific", specificApprovers);
        specificApproversMap.put("role", roleApprovers);

        mapBody.put("specific_approvers", specificApproversMap);

        mEnableSubmitButtonLiveData.setValue(false);

        // Accepts object
        Disposable disposable = mRepository
                .editWorkflow(mToken, workflowId, mapBody)
                .subscribe(this::onEditSuccess, this::onEditFailure);

        mDisposables.add(disposable);
    }

    protected void generateFieldsByType(String typeName) {
        generateFieldsByType(formSettings.findIdByTypeName(typeName));
    }

    protected void updateStandardFilterFieldTagsUsing(final int workflowTypeId) {
        if (mFormType != FormType.STANDARD_FILTERS) {
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = mRepository
                    .getFieldsByWorkflowType(workflowTypeId);
            if (fields == null || fields.size() < 1) {
                return null;
            }
            FormFieldsByWorkflowType field;
            FieldConfig fieldConfig;
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
            for (int i = 0; i < fields.size(); i++) {
                field = fields.get(i);
                fieldConfig = jsonAdapter.fromJson(field.getFieldConfig());
                field.setFieldConfigObject(fieldConfig);
            }
            formSettings.updateTagsForFormItems(fields);
            return formSettings;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formSettings -> Log.d(TAG, "updateStandardFilterFieldTagsUsing: done"),
                        throwable -> showLoading.setValue(false));
        mDisposables.add(disposable);
    }

    protected void generateFieldsByType(int id) {
        if (id == 0 || mWorkflowTypeDbList == null || mWorkflowTypeDbList.isEmpty()) {
            showLoading.setValue(false);
            return;
        }

        mSelectedWorkflowType = mWorkflowTypeDbList.stream().filter(
                workflowTypeDb -> id == workflowTypeDb.getId()).findAny().orElse(null);

        showLoading.setValue(true);
        clearForm();
        formSettings.setWorkflowTypeIdSelected(id);
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = mRepository
                    .getFieldsByWorkflowType(id);
            if (fields == null || fields.size() < 1) {
                return false;
            }
            FormFieldsByWorkflowType field;
            FieldConfig fieldConfig;
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
            for (int i = 0; i < fields.size(); i++) {
                field = fields.get(i);
                fieldConfig = jsonAdapter.fromJson(field.getFieldConfig());
                field.setFieldConfigObject(fieldConfig);
            }
            formSettings.setFields(fields);
            return formSettings;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ignored -> createPeopleInvolvedItem(),
                        throwable -> showLoading.setValue(false));
        mDisposables.add(disposable);

    }

    /**
     * Removes all of the current form items and sends the data to the UI to remove them aswell.
     */
    protected void clearForm() {
        formSettings.clearFormItems();
        mSetFormItemListLiveData.setValue(formSettings.getFormItems());
        mSetPeopleInvolvedFormItemListLiveData.setValue(formSettings.getPeopleInvolvedFormItems());
    }

    private int getFieldIdFor(String machineName, int defaultFieldId) {
        Field field = mRepository.getFirstFieldBy(machineName);
        return field == null ? defaultFieldId : field.getFieldId();
    }

    private int getFieldIdForForm(String machineName, int defaultFieldId) {
        if (mFormType == FormType.STANDARD_FILTERS) {
            return getFieldIdFor(machineName, defaultFieldId);
        } else {
            return defaultFieldId;
        }
    }

    /**
     * Generates the standard fields. This is used for the filters fragment only, standard filters
     * section.
     */
    private void showStandardFieldsOnly() {
        //used to make sure all fields are created before proceeding
        mFieldCount = TOTAL_STANDARD_FIELDS;
        mFieldCompleted = 0;

        TextInputFormItem textInputFormItem;
        DateFormItem dateFormItem;
        BooleanFormItem booleanFormItem;

        textInputFormItem = new TextInputFormItem.Builder()
                .setTitleRes(R.string.title)
                .setTag(TAG_STANDARD_TITLE)
                .setFieldId(TAG_STANDARD_TITLE)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_TITLE)
                .setInputType(InputType.TEXT)
                .build();
        formSettings.getFormItems().add(textInputFormItem);
        buildFieldCompleted();


        textInputFormItem = new TextInputFormItem.Builder()
                .setTitleRes(R.string.key)
                .setTag(TAG_STANDARD_KEY)
                .setFieldId(TAG_STANDARD_KEY)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_KEY)
                .setInputType(InputType.TEXT)
                .build();
        formSettings.getFormItems().add(textInputFormItem);
        buildFieldCompleted();

        textInputFormItem = new TextInputFormItem.Builder()
                .setTitleRes(R.string.description)
                .setTag(TAG_STANDARD_DESCRIPTION)
                .setFieldId(TAG_STANDARD_DESCRIPTION)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_DESCRIPTION)
                .setInputType(InputType.TEXT)
                .build();
        formSettings.getFormItems().add(textInputFormItem);
        buildFieldCompleted();

        textInputFormItem = new TextInputFormItem.Builder()
                .setTitleRes(R.string.current_status)
                .setTag(TAG_STANDARD_CURRENT_STATUS)
                .setFieldId(TAG_STANDARD_CURRENT_STATUS)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_CURRENT_STATUS)
                .setInputType(InputType.TEXT)
                .build();
        formSettings.getFormItems().add(textInputFormItem);
        buildFieldCompleted();

        textInputFormItem = new TextInputFormItem.Builder()
                .setTitleRes(R.string.type)
                .setTag(TAG_STANDARD_TYPE)
                .setFieldId(TAG_STANDARD_TYPE)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_TYPE)
                .setInputType(InputType.TEXT)
                .build();
        formSettings.getFormItems().add(textInputFormItem);
        buildFieldCompleted();

        dateFormItem = new DateFormItem.Builder()
                .setTitleRes(R.string.start_date)
                .setTag(TAG_STANDARD_START_DATE)
                .setFieldId(TAG_STANDARD_START_DATE)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_START_DATE)
                .build();
        formSettings.getFormItems().add(dateFormItem);
        buildFieldCompleted();

        dateFormItem = new DateFormItem.Builder()
                .setTitleRes(R.string.end_date)
                .setTag(TAG_STANDARD_END_DATE)
                .setFieldId(TAG_STANDARD_END_DATE)
                .setEscaped(true)
                .setMachineName(MACHINE_NAME_END_DATE)
                .build();
        formSettings.getFormItems().add(dateFormItem);
        buildFieldCompleted();

        updateStandardFilters();

        createStandardOwnerFormItem();

        mEnableSubmitButtonLiveData.setValue(true);
        mShowSubmitButtonLiveData.setValue(true);
        mShowDynamicFiltersNoTypeLiveData.setValue(false);
        showLoading.setValue(false);
    }

    private void updateStandardFilters() {
        if (mFormType != FormType.STANDARD_FILTERS) {
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            setupStandardFieldsIdBy(MACHINE_NAME_TITLE);
            setupStandardFieldsIdBy(MACHINE_NAME_KEY);
            setupStandardFieldsIdBy(MACHINE_NAME_DESCRIPTION);
            setupStandardFieldsIdBy(MACHINE_NAME_CURRENT_STATUS);
            setupStandardFieldsIdBy(MACHINE_NAME_TYPE);
            setupStandardFieldsIdBy(MACHINE_NAME_START_DATE);
            setupStandardFieldsIdBy(MACHINE_NAME_END_DATE);
            return formSettings;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formSettings -> Log.d(TAG, "updateStandardFilters: done"),
                        throwable -> Log.e(TAG, "updateStandardFilters: Something went wrong updating field ids"));
        mDisposables.add(disposable);
    }

    private void setupStandardFieldsIdBy(String machineName) {
        Field field = mRepository.getFirstFieldBy(machineName);
        if (field == null) {
            return;
        }
        formSettings.updateStandardFieldBy(machineName, field.getFieldId());
    }

    private void createStandardOwnerFormItem() {

        Disposable disposable = mRepository
                .getProfiles(mToken, true)
                .subscribe(profileResponse -> {

                    List<Profile> profiles = profileResponse.getProfiles();
                    if (profiles == null || profiles.isEmpty()) return;

                    List<Option> userOptions = new ArrayList<>();
                    for (int i = 0; i < profiles.size(); i++) {
                        String name = profiles.get(i).getFullName();
                        Integer id = profiles.get(i).getId();

                        Option option = new Option(id, name);
                        userOptions.add(option);
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitleRes(R.string.owner)
                            .setTag(TAG_STANDARD_OWNER)
                            .setFieldId(TAG_STANDARD_OWNER)
                            .setOptions(userOptions)
                            .setMachineName(MACHINE_NAME_OWNER)
                            .build();

                    formSettings.getFormItems().add(singleChoiceFormItem);
                    buildFieldCompleted();

                }, throwable -> Log
                        .e(TAG, "createStandardOwnerFormItem: error " + throwable.getMessage()));

        mDisposables.add(disposable);
    }

    /**
     * Creates all of the form items according to their params. Sends each form item to the UI.
     *
     * @param formSettings holder of the current form params.
     */
    private void showFields(FormSettings formSettings) {
        List<FormFieldsByWorkflowType> fields = formSettings.getFields();
        List<FormFieldsByWorkflowType> fieldsToBuild = new ArrayList<>();

        //group the fields to build
        for (int i = 0; i < fields.size(); i++) {
            FormFieldsByWorkflowType field = fields.get(i);
            FieldConfig fieldConfig = field.getFieldConfigObject();
            String machineName = fieldConfig.getMachineName();

            if (!field.isShowForm()) {
                mFieldCount--;
                continue;
            }

            //do not show the pre-calculated fields
            if (fieldConfig.isPrecalculated()) {
                mFieldCount--;
                continue;
            }

            //do not show the Status field
            if (machineName != null && machineName.equals(MACHINE_NAME_STATUS)) {
                mFieldCount--;
                continue;
            }

            //do not show the Active field
            if (machineName != null && machineName.equals(MACHINE_NAME_ACTIVE)) {
                mFieldCount--;
                continue;
            }

            //do not show the Owner field (it is separated on the people involved form)
            if (machineName != null && machineName.equals(MACHINE_NAME_OWNER)) {
                mFieldCount--;
                continue;
            }

            if (mFormType == FormType.DYNAMIC_FILTERS) {
                //do not show the Title field
                if ((machineName != null && machineName.equals(MACHINE_NAME_TITLE))
                        //do not show the Description field
                        || (machineName != null && machineName.equals(MACHINE_NAME_DESCRIPTION))
                        //do not show the Start Date field
                        || (machineName != null && machineName.equals(MACHINE_NAME_START_DATE))) {
                    mFieldCount--;
                    continue;
                }
            }

            fieldsToBuild.add(field);
        }

        //used to make sure all fields are created before proceeding
        mFieldCount = fieldsToBuild.size();
        mFieldCompleted = 0;

        //build all of the wanted fields
        fieldsToBuild.forEach(this::buildField);

        showLoading.setValue(false); //the fields will be created on the background
        mShowDynamicFiltersNoTypeLiveData.setValue(false);

        mEnableSubmitButtonLiveData.setValue(true);
        mShowSubmitButtonLiveData.setValue(true);
    }

    /**
     * Create a specific form item according to its params.
     *
     * @param field the form item params.
     */
    private void buildField(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

        switch (typeInfo.getType()) {

            case FormSettings.TYPE_TEXT:
            case FormSettings.TYPE_TEXT_AREA:
                createTextInputFormItem(field);
                break;

            case FormSettings.TYPE_LINK:
                //server value type is "text", does not work for us, so we change it to "link"
                typeInfo.setValueType(InputType.LINK);
                createTextInputFormItem(field);
                break;

            case FormSettings.TYPE_DATE:
            case FormSettings.TYPE_BIRTH_DATE:
                createDateFormItem(field);
                break;

            case FormSettings.TYPE_CHECKBOX:
                createBooleanFormItem(field);
                break;

            case FormSettings.TYPE_PRODUCT:
                createProductsFormItem(field);
                break;

            case FormSettings.TYPE_ROLE:
                createRolesFormItem(field);
                break;

            case FormSettings.TYPE_SERVICE:
                createServicesFormItem(field);
                break;

            case FormSettings.TYPE_PROJECT:
                createProjectsFormItem(field);
                break;

            case FormSettings.TYPE_SYSTEM_USERS:
                createSystemUsersFormItem(field);
                break;

            case FormSettings.TYPE_LIST:
                if (field.getFieldConfigObject().getMultiple()) {
                    createCustomMultipleListFormItem(field);
                } else {
                    createCustomListFormItem(field);
                }
                break;
            case FormSettings.TYPE_CURRENCY:
                if (isFilterFragment()) {
                    //do not include in the dynamic filters
                    buildFieldCompleted();
                    break;
                }

                createCurrencyFormItem(field);
                break;
            case FormSettings.TYPE_PHONE:
                if (isFilterFragment()) {
                    //do not include in the dynamic filters
                    buildFieldCompleted();
                    break;
                }

                createPhoneFormItem(field);
                break;
            case FormSettings.TYPE_FILE:
                if (isFilterFragment()) {
                    //do not include in the dynamic filters
                    buildFieldCompleted();
                    break;
                }

                createFileFormItem(field);
                break;
            case FormSettings.TYPE_GEOLOCATION:
                if (isFilterFragment()) {
                    //do not include in the dynamic filters
                    buildFieldCompleted();
                    break;
                }

                createGeolocationFormItem(field);
                break;
            case FormSettings.TYPE_ACCOUNT:
            case FormSettings.TYPE_BUSINESS_OPPORTUNITY:
            case FormSettings.TYPE_CONTACT:
                createAutocompleteFormItem(field);
                break;
            default:
                Log.d(TAG, "buildField: Not a generic type: " + typeInfo
                        .getType() + " value: " + typeInfo.getValueType());

                buildFieldCompleted();
                break;
        }
    }

    private void buildFieldCompleted() {
        mFieldCompleted++;

        if (mFieldCompleted >= mFieldCount) {
            mFieldCount = mFieldCompleted = 0;

            mSetFormItemListLiveData.setValue(formSettings.getFormItems());

            //create mode
            if (mWorkflowListItem == null) {
                return;
            }

            // edit mode
            getWorkflow(mWorkflowListItem.getWorkflowId());
        }
    }

    //region Create Form Items

    /**
     * Creates the WorkflowType form item. Performs a request to the repo to retrieve the options
     * and then send the form item to the UI.
     */
    private void createWorkflowTypeItem() {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .getAllowedWorkflowTypes(mToken)
                .subscribe(this::onWorkflowTypesSuccess, this::onWorkflowTypesFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowTypesSuccess(WorkflowTypeDbResponse workflowTypeDbResponse) {
        mWorkflowTypeDbList = workflowTypeDbResponse.getList();
        if (mWorkflowTypeDbList == null || mWorkflowTypeDbList.isEmpty()) {
            showLoading.setValue(false);
            return;
        }

        List<WorkflowTypeItemMenu> types = new ArrayList<>();

        for (WorkflowTypeDb workflowTypeDb : mWorkflowTypeDbList) {
            types.add(new WorkflowTypeItemMenu(workflowTypeDb));
        }

        Option selectedOption = null; //used only in edit mode
        List<Option> options = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            String name = types.get(i).getName();
            int id = types.get(i).getId();
            formSettings.setId(id);
            formSettings.setName(name);

            Option option = new Option(id, name);
            options.add(option);

            if (mWorkflowListItem != null && mWorkflowListItem.getWorkflowTypeId() == id) {
                selectedOption = option;
            }
        }

        showLoading.setValue(false);

        if (isFilterFragment()) {
            //do not show the workflow type field for the filter fragment
            return;
        }

        SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                .setTitleRes(R.string.create_workflow_form_workflow_type)
                .setRequired(true)
                .setTag(TAG_WORKFLOW_TYPE)
                .setFieldId(TAG_WORKFLOW_TYPE)
                .setOptions(options)
                .setValue(selectedOption)
                .setEnabled(selectedOption == null) //if we are in edit mode, disable it
                .setVisible(selectedOption == null) //if we are in edit mode, hide it
                .setMachineName(MACHINE_NAME_TYPE)
                .build();

        formSettings.getFormItems().add(singleChoiceFormItem);

        mAddWorkflowTypeItemLiveData.setValue(singleChoiceFormItem);
    }

    private void onWorkflowTypesFailure(Throwable throwable) {
        Log.d(TAG, "onWorkflowTypesFailure: " + throwable.getMessage());
        showLoading.setValue(false);
        mShowFieldsRecyclerLiveData.setValue(false);

        //check if the error was due to lack of permissions
        if (throwable instanceof HttpException) {
            int httpCode = ((HttpException) throwable).code();
            if (httpCode == 403) {
                mShowNoPermissionsViewLiveData.setValue(true);
                mShowSubmitButtonLiveData.setValue(false);
                return;
            }
        }

        //show general error message if the error is not due to lack of permissions
        onFailure(throwable);
    }

    /**
     * Creates the People Involved form item. Performs a request to the repo to retrieve the
     * workflow type and then send the form item to the UI.
     */
    private void createPeopleInvolvedItem() {
        if (isFilterFragment()) {
            //do not create people involved item if it's from dynamic filters.
            showFields(formSettings);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            WorkflowTypeDb workflowTypeDbSingle = mRepository
                    .getWorklowType(formSettings.getWorkflowTypeIdSelected());

            return new IntentFormItem.Builder()
                    .setTitleRes(R.string.people_involved)
                    .setButtonActionTextRes(R.string.people_involved_action)
                    .setRequired(workflowTypeDbSingle != null && workflowTypeDbSingle.isDefineRoles())
//                    .setVisible(mWorkflowListItem == null) //hide in edit mode
                    .setTag(TAG_PEOPLE_INVOLVED)
                    .setFieldId(TAG_PEOPLE_INVOLVED)
                    .build();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(intentFormItem -> {
                    mAddPeopleInvolvedItemLiveData.setValue(intentFormItem);
                    showFields(formSettings);
                }, throwable -> {
                    Log.d(TAG, "createPeopleInvolvedItem: error " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        mDisposables.add(disposable);
    }

    /**
     * Creates the Products form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createProductsFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getProducts(mToken)
                .subscribe(productsResponse -> {
                    buildFieldCompleted();

                    if (productsResponse.getCode() != 200) {
                        return;
                    }
                    List<ProductFormList> list = productsResponse.getList();

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getName();
                        Integer id = list.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    if (options.isEmpty()) return;

                    //check if multiple selection
                    if (field.getFieldConfigObject().getMultiple()) {
                        MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitle(field.getFieldName())
                                .setRequired(field.isRequired())
                                .setTag(field.getId())
                                .setFieldId(field.getFieldId())
                                .setOptions(options)
                                .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                                .setMachineName(field.getFieldConfigObject().getMachineName())
                                .build();
                        mAddFormItemLiveData.setValue(multipleChoiceFormItem);

                        return;
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "createProjectsFormItem: " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates the Roles form item. Performs a request to the repo to retrieve the options and then
     * send the form item to the UI.
     */
    private void createRolesFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getRoles(mToken)
                .subscribe(roleResponse -> {
                    buildFieldCompleted();

                    if (roleResponse.getCode() != 200) {
                        return;
                    }

                    List<Role> list = roleResponse.getList();

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getName();
                        Integer id = list.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    if (options.isEmpty()) return;

                    //check if multiple selection
                    if (field.getFieldConfigObject().getMultiple()) {
                        MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitle(field.getFieldName())
                                .setRequired(field.isRequired())
                                .setTag(field.getId())
                                .setFieldId(field.getFieldId())
                                .setOptions(options)
                                .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                                .setMachineName(field.getFieldConfigObject().getMachineName())
                                .build();
                        mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                        return;
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "createRolesFormItem: " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates the Services form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createServicesFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getServices(mToken)
                .subscribe(servicesResponse -> {
                    buildFieldCompleted();

                    if (servicesResponse.getCode() != 200) {
                        return;
                    }

                    List<Service> list = servicesResponse.getList();
                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getName();
                        Integer id = list.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    if (options.isEmpty()) return;

                    //check if multiple selection
                    if (field.getFieldConfigObject().getMultiple()) {
                        MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitle(field.getFieldName())
                                .setRequired(field.isRequired())
                                .setTag(field.getId())
                                .setFieldId(field.getFieldId())
                                .setOptions(options)
                                .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                                .setMachineName(field.getFieldConfigObject().getMachineName())
                                .build();
                        mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                        return;
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG,
                            "createServicesFormItem: can't get service: " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });
        mDisposables.add(disposable);
    }

    /**
     * Creates the Projects form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createProjectsFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getProjects(mToken)
                .subscribe(projectResponse -> {
                    buildFieldCompleted();

                    List<Project> list = projectResponse.getProjects();

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getTitle();
                        Integer id = list.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    if (options.isEmpty()) return;

                    //sort alphabetically
                    options = options
                            .stream()
                            .sorted((o1, o2) -> o1.getName().toLowerCase()
                                    .compareTo(o2.getName().toLowerCase()))
                            .collect(Collectors.toList());

                    //check if multiple selection
                    if (field.getFieldConfigObject().getMultiple()) {
                        MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitle(field.getFieldName())
                                .setRequired(field.isRequired())
                                .setTag(field.getId())
                                .setFieldId(field.getFieldId())
                                .setOptions(options)
                                .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                                .setMachineName(field.getFieldConfigObject().getMachineName())
                                .build();
                        mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                        return;
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "createProjectsFormItem: " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates the SystemUsers form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createSystemUsersFormItem(FormFieldsByWorkflowType field) {
        if (mClientId == null) {
            buildFieldCompleted();
            return;
        }

        Disposable disposable = mRepository
                .getUsers(mToken, mClientId)
                .subscribe(workflowUsersResponse -> {
                    buildFieldCompleted();

                    if (workflowUsersResponse.getCode() != 200) {
                        return;
                    }

                    List<WorkflowUser> list = workflowUsersResponse.getUsers();
                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        WorkflowUser workflowUser = list.get(i);

                        String name = workflowUser.getUsername();
                        Integer id = workflowUser.getId();

                        Option option = new Option(id, name);
                        options.add(option);

                        formSettings.addWorkflowUser(workflowUser);
                    }

                    if (options.isEmpty()) return;

                    //check if multiple selection
                    if (field.getFieldConfigObject().getMultiple()) {
                        MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitle(field.getFieldName())
                                .setRequired(field.isRequired())
                                .setTag(field.getId())
                                .setFieldId(field.getFieldId())
                                .setOptions(options)
                                .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                                .setMachineName(field.getFieldConfigObject().getMachineName())
                                .build();
                        mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                        return;
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "createSystemUsersFormItem: can't get users: " + throwable
                            .getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });
        mDisposables.add(disposable);
    }

    /**
     * Creates a custom list form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createCustomListFormItem(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        int listId = fieldConfig.getListInfo().getId();

        Disposable disposable = mRepository
                .getList(mToken, listId)
                .subscribe(listsResponse -> {
                    buildFieldCompleted();

                    List<ListItem> list = listsResponse.getItems();
                    if (list == null || list.isEmpty()) return;

                    List<ListItem> listChildren = list.get(0).getChildren();

                    if (listChildren == null || listChildren.isEmpty()) return;

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < listChildren.size(); i++) {
                        String name = listChildren.get(i).getName();
                        Integer id = listChildren.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates a custom list form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createCustomMultipleListFormItem(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        int listId = fieldConfig.getListInfo().getId();

        Disposable disposable = mRepository
                .getList(mToken, listId)
                .subscribe(listsResponse -> {
                    buildFieldCompleted();

                    List<ListItem> list = listsResponse.getItems();
                    if (list == null || list.isEmpty()) return;

                    List<ListItem> listChildren = list.get(0).getChildren();

                    if (listChildren == null || listChildren.isEmpty()) return;

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < listChildren.size(); i++) {
                        String name = listChildren.get(i).getName();
                        Integer id = listChildren.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                }, throwable -> {
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    buildFieldCompleted();
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates a custom TextInput item with the specified params and sends the item to the UI. Adds
     * a regex validation for some specific {@link InputType}s.
     *
     * @param field item params.
     */
    private void createTextInputFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        String valueType = typeInfo.getValueType();

        TextInputFormItem item = new TextInputFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setTypeInfo(typeInfo)
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setInputType(valueType)
                .build();

        switch (item.getInputType()) {
            case InputType.EMAIL:
                item.setRegex(Patterns.EMAIL_ADDRESS.toString());
                break;
            case InputType.LINK:
                item.setRegex(Patterns.WEB_URL.toString());
                break;
        }

        formSettings.getFormItems().add(item);

        buildFieldCompleted();
    }

    /**
     * Creates a custom Date item with the specified params and sends the item to the UI.
     *
     * @param field item params.
     */
    private void createDateFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        String valueType = typeInfo.getValueType();

        buildFieldCompleted();

        if (!valueType.equals(FormSettings.VALUE_DATE)) {
            Log.d(TAG, "createDateFormItem: Value not recognized " + valueType);
            return;
        }

        Date defaultValue = null;
        if (field.getFieldConfigObject().getMachineName() != null
                && field.getFieldConfigObject().getMachineName().equals(MACHINE_NAME_START_DATE)
                && mFormType == FormType.CREATE) {
            defaultValue = Calendar.getInstance().getTime();
        }

        DateFormItem item = new DateFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .setValue(defaultValue)
                .build();

        formSettings.getFormItems().add(item);
    }

    /**
     * Creates a custom Boolean/Checkbox item with the specified params and sends the item to the
     * UI.
     *
     * @param field item params.
     */
    private void createBooleanFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        String valueType = typeInfo.getValueType();

        buildFieldCompleted();

        if (!valueType.equals(FormSettings.VALUE_BOOLEAN)) {
            Log.d(TAG, "createBooleanFormItem: Value not recognized: " + valueType);
            return;
        }

        boolean defaultValue = field.getFieldConfigObject().getMachineName() != null
                && field.getFieldConfigObject().getMachineName().equals(MACHINE_NAME_STATUS);

        BooleanFormItem item = new BooleanFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .setValue(defaultValue)
                .build();

        formSettings.getFormItems().add(item);
    }

    /**
     * Creates a currency form item. Performs a request to the repo to retrieve the options and then
     * send the form item to the UI.
     */
    private void createCurrencyFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getCurrencyCodes()
                .subscribe(currencyList -> {
                    buildFieldCompleted();

                    if (currencyList == null || currencyList.size() < 1) {
                        return;
                    }

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < currencyList.size(); i++) {
                        CurrencyFieldData currencyData = currencyList.get(i);
                        String name = currencyData.description + " - " + currencyData.currency;
                        Integer id = currencyData.countryId;

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    CurrencyFormItem currencyFormItem = new CurrencyFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setEscaped(escape(field.getFieldConfigObject()))
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(currencyFormItem);

                }, throwable -> {
                    buildFieldCompleted();
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    Log.e(TAG, "handleCurrency: problem getting currency list " + throwable
                            .getMessage());
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates a phone form item. Performs a request to the repo to retrieve the options and then
     * send the form item to the UI.
     */
    private void createPhoneFormItem(FormFieldsByWorkflowType field) {
        Disposable disposable = mRepository
                .getCountryCodes()
                .subscribe(phoneFieldDataList -> {
                    buildFieldCompleted();

                    if (phoneFieldDataList == null || phoneFieldDataList.size() < 1) {
                        return;
                    }

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < phoneFieldDataList.size(); i++) {
                        PhoneFieldData phoneFieldData = phoneFieldDataList.get(i);
                        String name = phoneFieldData.phoneCode + " - " + phoneFieldData.description;
                        Integer id = phoneFieldData.countryId;

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    PhoneFormItem phoneFormItem = new PhoneFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setFieldId(field.getFieldId())
                            .setEscaped(escape(field.getFieldConfigObject()))
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(phoneFormItem);

                }, throwable -> {
                    buildFieldCompleted();
                    mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
                    Log.e(TAG,
                            "handlePhone: problem getting country list " + throwable.getMessage());
                });

        mDisposables.add(disposable);
    }

    /**
     * Creates a file form item with the specified params and sends the item to the UI.
     *
     * @param field item params.
     */
    private void createFileFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

        FileFormItem item = new FileFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .build();

        formSettings.getFormItems().add(item);

        buildFieldCompleted();
    }

    /**
     * Creates a geolocation form item with the specified params and sends the item to the UI.
     *
     * @param field item params.
     */
    private void createGeolocationFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

        GeolocationFormItem item = new GeolocationFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .build();

        formSettings.getFormItems().add(item);

        buildFieldCompleted();
    }

    /**
     * Creates an autocomplete form item with the specified params and sends the item to the UI.
     *
     * @param field item params.
     */
    private void createAutocompleteFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

        AutocompleteFormItem item = new AutocompleteFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setFieldId(field.getFieldId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .build();

        formSettings.getFormItems().add(item);

        buildFieldCompleted();
    }
    //endregion

    //region Fill Data

    /**
     * Performs a request to the remote repository to fetch the workflow information in order to
     * fill the form items.
     *
     * @param workflowId ID of the workflow that is being edited.
     */
    private void getWorkflow(int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(mToken, workflowId)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        showLoading.setValue(false);

        mWorkflow = workflowResponse.getWorkflow();
        updateWorkflowInformation(mWorkflow);
    }

    /**
     * Fills the form data with the workflow info
     *
     * @param workflow Workflow with info to display on the form.
     */
    private void updateWorkflowInformation(WorkflowDb workflow) {
        TextInputFormItem titleItem = (TextInputFormItem) formSettings
                .findItem(FormSettings.MACHINE_NAME_TITLE);
        if (titleItem != null) titleItem.setValue(workflow.getTitle());

        TextInputFormItem descriptionItem = (TextInputFormItem) formSettings
                .findItem(FormSettings.MACHINE_NAME_DESCRIPTION);
        if (descriptionItem != null) descriptionItem.setValue(workflow.getDescription());

        DateFormItem startDateItem = (DateFormItem) formSettings
                .findItem(FormSettings.MACHINE_NAME_START_DATE);
        Date startDate = Utils.getDateFromString(workflow.getStart(), Utils.SERVER_DATE_FORMAT);
        if (startDateItem != null) startDateItem.setValue(startDate);

        if (workflow.getMetas().isEmpty()) {
            mSetFormItemListLiveData.setValue(formSettings.getFormItems());
            return;
        }

        List<Meta> metaList = workflow.getMetas();
        Meta meta;
        Moshi moshi = new Moshi.Builder().build();
        FieldConfig fieldConfig;
        TypeInfo typeInfo;
        JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
        for (int i = 0; i < metaList.size(); i++) {
            meta = metaList.get(i);
            try {
                fieldConfig = jsonAdapter.fromJson(meta.getWorkflowTypeFieldConfig());
                typeInfo = fieldConfig.getTypeInfo();
                if (typeInfo == null) {
                    continue;
                }

                switch (typeInfo.getType()) {
                    case FormSettings.TYPE_TEXT:
                    case FormSettings.TYPE_TEXT_AREA:
                    case FormSettings.TYPE_LINK:
                        fillTextInputFormItem(meta);
                        break;

                    case FormSettings.TYPE_CHECKBOX:
                        fillBooleanFormItem(meta);
                        break;

                    case FormSettings.TYPE_DATE:
                        fillDateFormItem(meta);
                        break;

                    case FormSettings.TYPE_LIST:
                    case FormSettings.TYPE_SERVICE:
                    case FormSettings.TYPE_PROJECT:
                        if (fieldConfig.getMultiple()) {
                            fillMultipleChoiceFormItem(meta);
                        } else {
                            fillSingleChoiceFormItem(meta);
                        }
                        break;

                    case FormSettings.TYPE_SYSTEM_USERS:
                        fillUsersFormItem(meta);
                        break;

                    case FormSettings.TYPE_CURRENCY:
                        fillCurrencyFormItem(meta);
                        break;

                    case FormSettings.TYPE_PHONE:
                        fillPhoneFormItem(meta);
                        break;

                    case FormSettings.TYPE_FILE:
                        fillFileFormItem(meta);
                        break;

                    case FormSettings.TYPE_GEOLOCATION:
                        fillGeolocationFormItem(meta);
                        break;

                    case FormSettings.TYPE_ACCOUNT:
                        fillAccountFormItem(meta);
                        break;

                    case FormSettings.TYPE_BUSINESS_OPPORTUNITY:
                        fillBusinessOpportunityFormItem(meta);
                        break;

                    case FormSettings.TYPE_CONTACT:
                        fillSubContactFormItem(meta);
                        break;
                    default:
                        Log.d(TAG, "format: invalid type. Not Known.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "updateWorkflowInformation: " + e.getMessage());
            }
        }

        mSetFormItemListLiveData.setValue(formSettings.getFormItems());
    }

    private void fillTextInputFormItem(Meta meta) {
        String value = String.valueOf(meta.getDisplayValue());

        TextInputFormItem textInputFormItem = (TextInputFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (textInputFormItem != null) {
            textInputFormItem.setValue(value);
        }
    }

    private void fillBooleanFormItem(Meta meta) {
        String value = meta.getValue();

        BooleanFormItem booleanFormItem = (BooleanFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (booleanFormItem != null) {
            booleanFormItem.setValue(Boolean.valueOf(value));
        }
    }

    private void fillDateFormItem(Meta meta) {
        String value = String.valueOf(meta.getDisplayValue()); // now returns "10/25/2018"

        DateFormItem startDateItem = (DateFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());
        Date startDate = Utils.getDateFromString(value, "dd/MM/yyyy");

        if (startDateItem != null) {
            startDateItem.setValue(startDate);
        }
    }

    private void fillSingleChoiceFormItem(Meta meta) {
        Integer intValue = null;
        String stringValue = null;

        if (meta.getDisplayValue() instanceof String) {
            stringValue = (String) meta.getDisplayValue();
        } else if (meta.getDisplayValue() instanceof List) {
            List<String> values = (List<String>) meta.getDisplayValue();

            if (values == null || values.isEmpty()) {
                //check if we have the ID value
                if (meta.getValue() == null || meta.getValue().isEmpty()
                        && !Utils.isInteger(meta.getValue())) {
                    return;
                }

                intValue = Integer.valueOf(meta.getValue());
            } else {
                //use display value
                stringValue = values.get(0);
            }
        }

        if (intValue == null && stringValue == null) return;

        SingleChoiceFormItem singleChoiceFormItem = (SingleChoiceFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (singleChoiceFormItem != null) {
            Option value;
            if (intValue != null) {
                //find by id
                value = formSettings.findOption(singleChoiceFormItem.getOptions(), intValue);
            } else {
                //find by string
                value = formSettings.findOption(singleChoiceFormItem.getOptions(), stringValue);
            }

            singleChoiceFormItem.setValue(value);
        }
    }

    private void fillMultipleChoiceFormItem(Meta meta) {
        List<String> values = (List<String>) meta.getDisplayValue();
        boolean isIntValues = false;
        if (values == null || values.isEmpty()) {
            //check if we have the ID values
            if (meta.getValue() == null || meta.getValue().isEmpty()
                    && !Utils.isInteger(meta.getValue())) {
                return;
            }

            values = formSettings.parseMultipleSelectionRawValue(meta.getValue());
            isIntValues = true;
        }

        MultipleChoiceFormItem multipleChoiceFormItem = (MultipleChoiceFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (multipleChoiceFormItem != null) {
            for (String stringValue : values) {
                Option value;
                if (isIntValues) {
                    //find by id
                    value = formSettings.findOption(multipleChoiceFormItem.getOptions(),
                            Integer.parseInt(stringValue));
                } else {
                    //find by string
                    value = formSettings
                            .findOption(multipleChoiceFormItem.getOptions(), stringValue);
                }
                if (value == null) continue;
                multipleChoiceFormItem.addValue(value);
            }
        }
    }

    private void fillCurrencyFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()) return;

        JsonAdapter<PostCurrency> jsonAdapter = moshi.adapter(PostCurrency.class);
        PostCurrency postCurrency = jsonAdapter.fromJson(meta.getValue());

        if (postCurrency == null) return;

        CurrencyFormItem currencyFormItem = (CurrencyFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (currencyFormItem != null) {
            Option value = formSettings
                    .findOption(currencyFormItem.getOptions(), postCurrency.countryId);
            if (value == null) return;

            currencyFormItem.setSelectedOption(value);
            currencyFormItem.setValue(postCurrency.value);
        }
    }

    private void fillPhoneFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()) return;

        JsonAdapter<PostPhone> jsonAdapter = moshi.adapter(PostPhone.class);
        PostPhone postPhone = jsonAdapter.fromJson(meta.getValue());

        if (postPhone == null) return;

        PhoneFormItem phoneFormItem = (PhoneFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (phoneFormItem != null) {
            Option value = formSettings
                    .findOption(phoneFormItem.getOptions(), postPhone.countryId);
            if (value == null) return;

            phoneFormItem.setSelectedOption(value);
            phoneFormItem.setValue(postPhone.value);
        }
    }

    private void fillFileFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<FileMetaData> jsonAdapter = moshi.adapter(FileMetaData.class);
        FileMetaData fileMetaData = jsonAdapter.fromJson(meta.getValue());

        if (fileMetaData == null) return;

        FileFormItem fileFormItem = (FileFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (fileFormItem != null) {
            fileFormItem.setValue(fileMetaData.name); //to avoid failing the isValid() check
            fileFormItem.setFileName(fileMetaData.name);
            fileFormItem.setFileId(fileMetaData.value);
        }
    }

    private void fillGeolocationFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<GeolocationMetaData> jsonAdapter = moshi.adapter(GeolocationMetaData.class);
        GeolocationMetaData geolocationMetaData = jsonAdapter.fromJson(meta.getValue());

        if (geolocationMetaData == null
                || geolocationMetaData.getValue() == null
                || geolocationMetaData.getValue().getLatLng() == null
                || geolocationMetaData.getValue().getLatLng().size() < 2) {
            return;
        }

        GeolocationFormItem geolocationFormItem = (GeolocationFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (geolocationFormItem != null) {
            LatLng latLng = new LatLng(geolocationMetaData.getValue().getLatLng().get(0),
                    geolocationMetaData.getValue().getLatLng().get(1));
            geolocationFormItem.setValue(latLng);
            geolocationFormItem.setName(geolocationMetaData.getValue().getAddress());
        }
    }

    private void fillUsersFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<FormCreateProfile> jsonAdapter = moshi.adapter(FormCreateProfile.class);
        FormCreateProfile usersMetaData = jsonAdapter.fromJson(meta.getValue());

        if (usersMetaData == null) return;

        SingleChoiceFormItem singleChoiceFormItem = (SingleChoiceFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (singleChoiceFormItem != null) {
            //find by id
            Option value = formSettings
                    .findOption(singleChoiceFormItem.getOptions(), usersMetaData.getId());

            singleChoiceFormItem.setValue(value);
        }
    }

    private void fillAccountFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<PostContact> jsonAdapter = moshi.adapter(PostContact.class);
        PostContact contactMetaData = jsonAdapter.fromJson(meta.getValue());

        if (contactMetaData == null) return;

        //pass the selected value to the FormSettings list
        Contact contact = new Contact();
        contact.setId(contactMetaData.getId());
        contact.setCompany(contactMetaData.getCompany());
        contact.setFullName(contactMetaData.getFullName());
        List<Contact> contactList = new ArrayList<>();
        contactList.add(contact);
        formSettings.setContacts(contactList);

        //find the form item
        AutocompleteFormItem autocompleteFormItem = (AutocompleteFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (autocompleteFormItem != null) {
            Option value = new Option(contactMetaData.getId(), contactMetaData.getCompany());

            autocompleteFormItem.setValue(value);
        }
    }

    private void fillBusinessOpportunityFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<PostBusinessOpportunity> jsonAdapter = moshi
                .adapter(PostBusinessOpportunity.class);
        PostBusinessOpportunity businessOpportunityMeta = jsonAdapter.fromJson(meta.getValue());

        if (businessOpportunityMeta == null) return;

        //pass the selected value to the FormSettings list
        BusinessOpportunity businessOpportunity = new BusinessOpportunity();
        businessOpportunity.setId(businessOpportunityMeta.getId());
        businessOpportunity.setTitle(businessOpportunityMeta.getTitle());
        List<BusinessOpportunity> businessOpportunityList = new ArrayList<>();
        businessOpportunityList.add(businessOpportunity);
        formSettings.setBusinessOpportunities(businessOpportunityList);

        //find the form item
        AutocompleteFormItem autocompleteFormItem = (AutocompleteFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (autocompleteFormItem != null) {
            Option value = new Option(businessOpportunityMeta.getId(),
                    businessOpportunityMeta.getTitle());

            autocompleteFormItem.setValue(value);
        }
    }

    private void fillSubContactFormItem(Meta meta) throws IOException {
        if (meta.getValue() == null || meta.getValue().isEmpty()
                || meta.getValue().equals("\"\"")) {
            return;
        }

        JsonAdapter<PostSubContact> jsonAdapter = moshi
                .adapter(PostSubContact.class);
        PostSubContact postSubContact = jsonAdapter.fromJson(meta.getValue());

        if (postSubContact == null) return;

        //pass the selected value to the FormSettings list
        SubContact subContact = new SubContact();
        subContact.setId(postSubContact.getId());
        subContact.setFullName(postSubContact.getFullName());
        List<SubContact> subContactList = new ArrayList<>();
        subContactList.add(subContact);
        formSettings.setSubContacts(subContactList);

        //find the form item
        AutocompleteFormItem autocompleteFormItem = (AutocompleteFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        if (autocompleteFormItem != null) {
            Option value = new Option(postSubContact.getId(), postSubContact.getFullName());

            autocompleteFormItem.setValue(value);
        }
    }
    //endregion

    protected GeolocationFormItem getCurrentRequestingGeolocationFormItem() {
        return mCurrentRequestingGeolocationFormItem;
    }

    protected void setCurrentRequestingGeolocationFormItem(
            GeolocationFormItem currentRequestingGeolocationFormItem) {
        this.mCurrentRequestingGeolocationFormItem = currentRequestingGeolocationFormItem;
    }

    //region File Upload
    protected FileFormItem getCurrentRequestingFileFormItem() {
        return mCurrentRequestingFileFormItem;
    }

    protected void setCurrentRequestingFileFormItem(FileFormItem currentRequestingFileFormItem) {
        this.mCurrentRequestingFileFormItem = currentRequestingFileFormItem;
    }

    /**
     * Handles the result of the file chooser intent. Retrieves information about the selected file
     * and sends that info to the UI.
     *
     * @param context     used to retrieve the file name and size.
     * @param requestCode ActivityResult requestCode.
     * @param resultCode  ActivityResult resultCode.
     * @param data        the file URI that was selected.
     */
    protected void handleActivityResult(Context context, int requestCode, int resultCode,
                                        Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_TO_ATTACH:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();

                        if (uri == null) {
                            mToastMessageLiveData.setValue(R.string.select_file);
                            return;
                        }

                        String fileType = Utils.getMimeType(data.getData(), context);

                        if (fileType.contains(Utils.VIDEO_MIME_TYPE_CHECK)) {
                            mToastMessageLiveData.setValue(R.string.file_video_not_allowed);
                            return;
                        }

                        if (!Utils.checkFileSize(UPLOAD_FILE_SIZE_LIMIT, new File(uri.getPath()))) {
                            DialogMessage message = new DialogMessage();
                            message.message = R.string.file_too_big;
                            message.title = R.string.warning;
                            showDialogMessage.setValue(message);
                            return;
                        }

                        Cursor returnCursor = context.getContentResolver()
                                .query(uri, null, null, null, null);

                        if (returnCursor == null) {
                            mToastMessageLiveData.setValue(R.string.error_selecting_file);
                            return;
                        }

                        returnCursor.moveToFirst();

                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        int size = (int) returnCursor.getLong(sizeIndex);

                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String fileName = returnCursor.getString(nameIndex);

                        returnCursor.close();

                        byte[] bytes = Utils.fileToByte(context.getContentResolver(), uri);

                        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);

                        FileFormItem formItem = getCurrentRequestingFileFormItem();
                        formItem.setValue(encodedFile);
                        formItem.setFileName(fileName);
                        formItem.setFileSize(size);
                        formItem.setFileType(fileType);
                        formItem.setFilePath(uri.getPath());

                        mUpdateFormItemLiveData.setValue(formItem);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_GEOLOCATION:
                if (resultCode == RESULT_OK) {
                    SelectedLocation selectedLocation = data
                            .getParcelableExtra(GeolocationViewModel.EXTRA_REQUESTED_LOCATION);
                    GeolocationFormItem formItem = getCurrentRequestingGeolocationFormItem();
                    formItem.setValue(selectedLocation.getLatLng());
                    formItem.setName(selectedLocation.getName());
                    mUpdateFormItemLiveData.setValue(formItem);
                }
                break;
        }
    }

    /**
     * Begins to upload every {@link FileFormItem} to the server. This will start a series of
     * methods that will upload the files one by one until the queue is empty. After everything was
     * uploaded or the queue is empty, it will begin to send the workflow.
     */
    private void uploadAllFiles() {
        mFilesToUpload = new ArrayList<>();
        for (BaseFormItem item : formSettings.getFormItems()) {
            if (item instanceof FileFormItem && ((FileFormItem) item).getValue() != null) {
                mFilesToUpload.add((FileFormItem) item);
            }
        }

        boolean isUploading = uploadFirstFile();
        if (!isUploading) {
            List<BaseFormItem> items = formSettings.getFormItemsToPost();
            ArrayMap<String, Integer> baseInfo = formSettings.getBaseMachineNamesAndIds();

            startSendingWorkflow(baseInfo, items);
        }
    }

    /**
     * Uploads the first index of {@link #mFilesToUpload} array unless it's empty.
     *
     * @return false - empty array, no file will be uploaded; true - a file will be uploaded.
     */
    private boolean uploadFirstFile() {
        if (mFilesToUpload.isEmpty()) return false;

        FileFormItem fileToUpload = mFilesToUpload.get(0);
        setCurrentRequestingFileFormItem(fileToUpload);
        postFileRequest(fileToUpload);
        mFilesToUpload.remove(fileToUpload);

        return true;
    }

    /**
     * Sends a request to the server to upload the file.
     *
     * @param fileFormItem form item containing the file to be uploaded.
     */
    private void postFileRequest(FileFormItem fileFormItem) {
        FilePostDetail filePostDetail = new FilePostDetail();
        filePostDetail.setFile(fileFormItem.getValue());
        filePostDetail.setType(fileFormItem.getFileType());
        filePostDetail.setName(fileFormItem.getFileName());

        FilePost filePost = new FilePost();
        filePost.setFile(filePostDetail);

        Disposable disposable = mRepository.uploadFile(mToken, filePost)
                .subscribe(this::successUpload, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "postFileRequest: file upload failed: " + throwable.getMessage());
                });

        mDisposables.add(disposable);
    }

    /**
     * Called after a successful file upload, it will try to upload the next queued file. If there's
     * no remaining files to be uploaded, this method will call {@link
     * #startSendingWorkflow(ArrayMap, List)} to send the workflow.
     *
     * @param fileUploadResponse WS response
     */
    private void successUpload(FileUploadResponse fileUploadResponse) {
        List<BaseFormItem> items = formSettings.getFormItemsToPost();
        ArrayMap<String, Integer> baseInfo = formSettings.getBaseMachineNamesAndIds();

        FileFormItem formItem = getCurrentRequestingFileFormItem();
        formItem.setFileId(fileUploadResponse.getFileId());

        boolean isUploading = uploadFirstFile();
        if (!isUploading) {
            startSendingWorkflow(baseInfo, items);
        }
    }
    //endregion

    //region File Download
    public int getQueuedFile() {
        return mQueuedFile;
    }

    public void setQueuedFile(int queuedFile) {
        this.mQueuedFile = queuedFile;
    }

    /**
     * Checks if the requested permissions were granted and then proceed to open the file.
     *
     * @param requestCode  to identify the request
     * @param grantResults array containing the request results.
     */
    protected void handleRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS: {
                // check for both permissions
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permissions granted
                    int fileId = getQueuedFile();
                    if (fileId == 0) return; //file was not set
                    downloadFile(fileId);

                } else {
                    // at least one permission was denied
                    mToastMessageLiveData.setValue(
                            R.string.workflow_detail_activity_permissions_not_granted);
                }
            }
        }
    }

    /**
     * Prepares a request to the endpoint for the desired file.
     *
     * @param fileId the file ID to download.
     */
    protected void downloadFile(int fileId) {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .downloadFile(mToken, FileUploadResponse.FILE_ENTITY,
                        fileId)
                .subscribe(this::onDownloadSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Callback for the success file download. Converts and saves it to a local file and sends it
     * back to the UI for displaying purposes.
     *
     * @param downloadFileResponse the downloaded file response.
     */
    private void onDownloadSuccess(DownloadFileResponse downloadFileResponse) {
        showLoading.setValue(false);

        // the API will return a base64 string representing the file

        String base64 = downloadFileResponse.getFile().getContent();
        if (base64 == null || base64.isEmpty()) {
            mToastMessageLiveData.setValue(R.string.error);
            return;
        }

        String fileName = downloadFileResponse.getFile().getFilename();
        try {
            DownloadedFileUiData attachmentUiData = new DownloadedFileUiData(
                    Utils.decodeFileFromBase64Binary(base64, fileName),
                    downloadFileResponse.getFile().getMime());
            mOpenDownloadedFileLiveData.setValue(attachmentUiData);

        } catch (IOException e) {
            Log.e(TAG, "downloadFile: ", e);
            mToastMessageLiveData.setValue(R.string.error);
        }
    }
    //endregion

    //region People Involved Form
    protected void getWorkflowTypeInfo() {
        //check if the form has already been created
        if (!formSettings.getPeopleInvolvedFormItems().isEmpty()) return;

        showLoading.setValue(true);

        int workflowTypeId = formSettings.getWorkflowTypeIdSelected();

        Disposable disposable = mRepository
                .getWorkflowType(mToken, workflowTypeId)
                .subscribe(this::onWorkflowTypeSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onWorkflowTypeSuccess(WorkflowTypeResponse workflowTypeResponse) {
        showPeopleInvolvedFields(workflowTypeResponse.getWorkflowType());
    }

    /**
     * Creates all of the people involved form items according to their params. Sends each form item
     * to the UI.
     */
    private void showPeopleInvolvedFields(WorkflowTypeDb workflowTypeDb) {
        createProfilesFormItems(workflowTypeDb);
    }

    /**
     * Generates every field that belongs to the People Involved form.
     *
     * @param workflowTypeDb object containing the WorkflowType info.
     */
    private void createProfilesFormItems(WorkflowTypeDb workflowTypeDb) {

        Disposable disposable = mRepository
                .getProfiles(mToken, true)
                .subscribe(profileResponse -> {
                    showLoading.setValue(false);

                    List<Profile> profiles = profileResponse.getProfiles();
                    if (profiles == null || profiles.isEmpty()) return;

                    Comparator<Profile> compareById = (Profile o1, Profile o2) -> o1.getFullName().compareToIgnoreCase(o2.getFullName());
                    profiles.sort(compareById);

                    Option selection = null; //check for current user (default owner)
                    List<Option> userOptions = new ArrayList<>();
                    for (int i = 0; i < profiles.size(); i++) {
                        String name = profiles.get(i).getFullName();
                        Integer id = profiles.get(i).getId();

                        Option option = new Option(id, name);
                        userOptions.add(option);

                        if (option.getId() == mUserId) selection = option;
                    }

                    //region Owner
                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitleRes(R.string.owner)
                            .setRequired(true)
                            .setTag(TAG_OWNER)
                            .setOptions(userOptions)
                            .setValue(selection)
                            //enable only if the user has permissions
                            .setEnabled(hasEditPermissions)
                            .setMachineName(MACHINE_NAME_OWNER)
                            .build();

                    mAddPeopleInvolvedFormItemLiveData.setValue(singleChoiceFormItem);
                    //endregion

                    //region Additional People Involved
                    //verify selected values
                    List<BaseOption> peopleInvolvedValues = null;
                    if (mWorkflow != null) {
                        peopleInvolvedValues = new ArrayList<>();
                        for (Integer id : mWorkflow.getProfilesInvolved()) {
                            Profile profile = Profile.getProfileByIdFromList(profiles, id);

                            if (profile == null) continue;

                            Option option = new Option(id, profile.getFullName());
                            peopleInvolvedValues.add(option);
                        }
                    }

                    MultipleChoiceFormItem multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                            .setTitleRes(R.string.additional_profiles)
                            .setRequired(false)
                            .setTag(TAG_ADDITIONAL_PROFILES)
                            .setOptions(userOptions)
                            .setValues(peopleInvolvedValues)
                            .build();

                    mAddPeopleInvolvedFormItemLiveData.setValue(multipleChoiceFormItem);
                    //endregion

                    //region Global Approvers
                    //check for user permissions
                    if (hasDefineSpecificApproverPermissions) {
                        //verify selected values
                        List<BaseOption> globalApproversValues = null;
                        if (mWorkflow != null) {
                            globalApproversValues = new ArrayList<>();
                            for (Integer id : mWorkflow.getSpecificApprovers().global) {
                                Profile profile = Profile.getProfileByIdFromList(profiles, id);

                                if (profile == null) continue;

                                Option option = new Option(id, profile.getFullName());
                                globalApproversValues.add(option);
                            }
                        }

                        multipleChoiceFormItem = new MultipleChoiceFormItem.Builder()
                                .setTitleRes(R.string.global_approvers_form)
                                .setRequired(false)
                                .setTag(TAG_GLOBAL_APPROVERS)
                                .setOptions(userOptions)
                                .setValues(globalApproversValues)
                                .build();

                        mAddPeopleInvolvedFormItemLiveData.setValue(multipleChoiceFormItem);
                    }
                    //endregion

                    //region Specific Approvers
                    //check for user permissions
                    if (hasDefineSpecificApproverPermissions) {
                        List<Status> statuses = workflowTypeDb.getStatus();
                        if (statuses != null && !statuses.isEmpty()) {
                            //verify selected values
                            List<BaseOption> specificApproversValues = null;
                            if (mWorkflow != null) {
                                specificApproversValues = new ArrayList<>();
                                for (StatusSpecific statusSpecific : mWorkflow
                                        .getSpecificApprovers().statusSpecific) {
                                    Profile profile = Profile
                                            .getProfileByIdFromList(profiles, statusSpecific.user);
                                    Status status = Status
                                            .getStatusByIdFromList(statuses, statusSpecific.status);

                                    if (profile == null || status == null) continue;

                                    Option userOption = new Option(profile.getId(),
                                            profile.getFullName());
                                    Option statusOption = new Option(status.getId(),
                                            status.getName());
                                    DoubleOption doubleOption = new DoubleOption(userOption,
                                            statusOption);
                                    specificApproversValues.add(doubleOption);
                                }
                            }

                            List<Option> statusOptions = new ArrayList<>();
                            for (int i = 0; i < statuses.size(); i++) {
                                String name = statuses.get(i).getName();
                                Integer id = statuses.get(i).getId();

                                Option option = new Option(id, name);
                                statusOptions.add(option);
                            }

                            DoubleMultipleChoiceFormItem doubleMultipleChoiceFormItem = new DoubleMultipleChoiceFormItem.Builder()
                                    .setTitleRes(R.string.specific_approvers_form)
                                    .setRequired(false)
                                    .setTag(TAG_SPECIFIC_APPROVERS)
                                    .setFirstOptions(userOptions)
                                    .setSecondOptions(statusOptions)
                                    .setValues(specificApproversValues)
                                    .build();

                            mAddPeopleInvolvedFormItemLiveData
                                    .setValue(doubleMultipleChoiceFormItem);
                        }
                    }
                    //endregion

                    //region Approvers by Role
                    List<Approver> approvers = workflowTypeDb.getDistinctApprovers();
                    boolean isFirst = true;
                    for (Approver approver : approvers) {
                        //only add editable items for roles
                        if (!approver.entityType.equalsIgnoreCase(ENTITY_ROLE)) {
                            DisplayFormItem displayFormItem = new DisplayFormItem.Builder()
                                    .setTitleRes(isFirst ? R.string.approvers_involved : 0)
                                    .setTag(approver.entityId)
                                    .setValue(approver.entityName == null ? approver.entityType : approver.entityName)
                                    .setImage(approver.entityAvatar)
                                    .build();

                            isFirst = false;

                            mAddPeopleInvolvedFormItemLiveData.setValue(displayFormItem);
                            continue;
                        }

                        List<Integer> profileIds = workflowTypeDb
                                .getRoleApproverProfileIds(approver.entityId);

                        if (profileIds == null || profileIds.isEmpty()) continue;

                        Option value = null;
                        if (mWorkflow != null) {
                            //verify selected values
                            Object profileId = mWorkflow.getSpecificApprovers().getRole()
                                    .get(String.valueOf(approver.entityId));

                            //sometimes it returns a Double value
                            if (profileId instanceof Double) {
                                profileId = ((Double) profileId).intValue();
                            }

                            //check if the id is an integer
                            if (profileId instanceof Integer) {
                                Profile profile = Profile
                                        .getProfileByIdFromList(profiles, (Integer) profileId);
                                if (profile != null) {
                                    value = new Option(profile.getId(), profile.getFullName());
                                }
                            }
                        }

                        //check for default approvers if value is null
                        DefaultRoleApprover defaultApprover = null;
                        if (value == null && mSelectedWorkflowType != null) {
                            defaultApprover = mSelectedWorkflowType.getDefaultRoleApprovers()
                                    .stream().filter(
                                            defaultRoleApprover -> approver.entityId == defaultRoleApprover
                                                    .getRoleId()).findAny().orElse(null);
                        }

                        //get options for each role
                        List<Option> approverOptions = new ArrayList<>();
                        for (int j = 0; j < profileIds.size(); j++) {
                            int profileId = profileIds.get(j);
                            Profile profile = Profile.getProfileByIdFromList(profiles, profileId);

                            if (profile == null) continue;

                            String name = profile.getFullName();
                            Integer id = profile.getId();

                            Option option = new Option(id, name);
                            approverOptions.add(option);

                            //verify if there is a default approver
                            if (defaultApprover != null && defaultApprover.getProfileId() == option
                                    .getId()) {
                                value = option;
                            }
                        }

                        //ignore item if there are no options
                        if (approverOptions.isEmpty()) continue;

                        singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                                .setTitle(approver.entityName)
                                .setRequired(workflowTypeDb.isDefineRoles())
                                .setTag(approver.entityId)
                                .setOptions(approverOptions)
                                //check for user permissions
                                .setEnabled(hasDefineSpecificApproverPermissions)
                                .setValue(value)
                                .build();

                        //we need to keep these separated
                        formSettings.getRoleApproversFormItems().add(singleChoiceFormItem);

                        mAddPeopleInvolvedFormItemLiveData.setValue(singleChoiceFormItem);

                        isFirst = false;
                    }
                    //endregion
                }, throwable -> Log
                        .e(TAG, "createProfilesFormItems: error " + throwable.getMessage()));

        mDisposables.add(disposable);
    }

    /**
     * Performs the validation for every form item present in the People Involved form. Each item's
     * method is in charge of handling the validation, here we simply check whether one of them is
     * invalid to prevent the action from being completed.
     *
     * @return whether all of the form items are valid.
     */
    protected boolean validatePeopleInvolvedFormItems() {
        boolean isValid = true;

        for (BaseFormItem item : formSettings.getPeopleInvolvedFormItems()) {
            if (!item.isValid()) {
                isValid = false;
            }
        }

        if (isValid) {
            //every item inside the People Involved form are valid, so we set the item to completed
            BaseFormItem item = formSettings.findItem(TAG_PEOPLE_INVOLVED);
            ((IntentFormItem) item).setCompleted(true);
        }

        return isValid;
    }
    //endregion

    protected List<BaseFormItem> getFormItemsToFilter() {
        return formSettings.getFormItemsToFilter();
    }

    /**
     * This is called by the View when the user submits the form.
     */
    protected void handleCreateWorkflowAction() {
        validatePeopleInvolvedFormItems();

        if (validateFormItems()) {
            // all of the items are valid.
            postWorkflow();
        }
    }

    /**
     * Performs the validation for every form item present in the current form. Each item's method
     * is in charge of handling the validation, here we simply check whether one of them is invalid
     * to prevent the action from being completed.
     *
     * @return whether all of the form items are valid.
     */
    private boolean validateFormItems() {
        for (BaseFormItem item : formSettings.getFormItems()) {
            if (!item.isValid()) {
                //at least one item is not valid
                mValidationUiLiveData.setValue(formSettings.findFirstInvalidItem());
                return false;
            }
        }

        return true;
    }

    private static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean escape(FieldConfig fieldConfig) {
        TypeInfo typeInfo = fieldConfig.getTypeInfo();
        if (typeInfo == null) {
            return false;
        }
        String value = typeInfo.getValueType();
        String type = typeInfo.getType();
        if (value.equals(FormSettings.VALUE_STRING)
                || value.equals(FormSettings.VALUE_TEXT)
                || value.equals(FormSettings.VALUE_EMAIL)
                || value.equals(FormSettings.VALUE_INTEGER)
                || value.equals(FormSettings.VALUE_DATE)
                || value.equals(FormSettings.VALUE_COORDS)) {
            return true;
        }
        return value.equals(FormSettings.VALUE_LIST) && type.equals(FormSettings.TYPE_SYSTEM_USERS);
    }

    private void onCreateSuccess(CreateWorkflowResponse createWorkflowResponse) {
        mEnableSubmitButtonLiveData.setValue(true);

        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.created;
        dialogMessage.message = R.string.workflow_created;
        dialogMessage.messageAggregate = createWorkflowResponse.getWorkflow().getWorkflowTypeKey();
        showDialogMessage.setValue(dialogMessage);
        goBack.setValue(true);
    }

    private void onEditSuccess(CreateWorkflowResponse createWorkflowResponse) {
        mEnableSubmitButtonLiveData.setValue(true);

        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.edited;
        dialogMessage.message = R.string.workflow_edit;
        if (createWorkflowResponse != null && createWorkflowResponse.getWorkflow() != null) {
            dialogMessage.messageAggregate = createWorkflowResponse.getWorkflow()
                    .getWorkflowTypeKey();
        }
        showDialogMessage.setValue(dialogMessage);
        goBack.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: " + throwable.getMessage());
        showLoading.setValue(false);
        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
    }

    private void onCreateFailure(Throwable throwable) {
        mEnableSubmitButtonLiveData.setValue(true);

        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.error;
        dialogMessage.message = R.string.error_create_workflow;
        showDialogMessage.setValue(dialogMessage);

        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));

        Log.d(TAG, "onFailure: " + throwable.getMessage());
    }

    private void onEditFailure(Throwable throwable) {
        mEnableSubmitButtonLiveData.setValue(true);

        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.error;
        dialogMessage.message = R.string.error_edit_workflow;
        showDialogMessage.setValue(dialogMessage);

        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));

        Log.d(TAG, "onFailure: " + throwable.getMessage());
    }

    protected void queryAutocompleteFormItem(AutocompleteFormItem formItem) {
        if (formItem.getQuery() == null) return;

        mCurrentQueryAutocompleteFormItem = formItem;

        switch (formItem.getTypeInfo().getType()) {
            case TYPE_ACCOUNT:
                queryForContacts(formItem);
                break;
            case TYPE_BUSINESS_OPPORTUNITY:
                queryForBusinessOpportunities(formItem);
                break;
            case TYPE_CONTACT:
                queryForSubContacts(formItem);
                break;
        }
    }

    private void queryForContacts(AutocompleteFormItem formItem) {
        Disposable disposable = mRepository
                .getContacts(mToken, formItem.getQuery())
                .subscribe(this::onContactsQuerySuccess, this::onAutocompleteFailure);

        mDisposables.add(disposable);
    }

    private void onContactsQuerySuccess(ContactsResponse contactsResponse) {
        List<Contact> contacts = contactsResponse.getList();

        formSettings.setContacts(contacts);

        //update options
        List<Option> options = contacts.stream()
                .map(contact -> new Option(contact.getId(), contact.getCompany()))
                .collect(Collectors.toList());

        onAutocompleteSuccess(options);
    }

    private void queryForBusinessOpportunities(AutocompleteFormItem formItem) {
        Disposable disposable = mRepository
                .getBusinessOpportunities(mToken, formItem.getQuery())
                .subscribe(this::onBusinessOpportunitiesQuerySuccess, this::onAutocompleteFailure);

        mDisposables.add(disposable);
    }

    private void onBusinessOpportunitiesQuerySuccess(
            BusinessOpportunitiesResponse businessOpportunitiesResponse) {
        List<BusinessOpportunity> businessOpportunities = businessOpportunitiesResponse.getList();

        formSettings.setBusinessOpportunities(businessOpportunities);

        //update options
        List<Option> options = businessOpportunities.stream()
                .map(businessOpportunity -> new Option(businessOpportunity.getId(),
                        businessOpportunity.getTitle()))
                .collect(Collectors.toList());

        onAutocompleteSuccess(options);
    }

    private void queryForSubContacts(AutocompleteFormItem formItem) {
        Disposable disposable = mRepository
                .postSearchSubContacts(mToken, formItem.getQuery())
                .subscribe(this::onSubContactsQuerySuccess, this::onAutocompleteFailure);

        mDisposables.add(disposable);
    }

    private void onSubContactsQuerySuccess(SubContactsResponse subContactsResponse) {
        List<SubContact> subContacts = subContactsResponse.getList();

        formSettings.setSubContacts(subContacts);

        //update options
        List<Option> options = subContacts.stream()
                .map(businessOpportunity -> new Option(businessOpportunity.getId(),
                        businessOpportunity.getFullName()))
                .collect(Collectors.toList());

        onAutocompleteSuccess(options);
    }

    private void onAutocompleteSuccess(List<Option> options) {
        if (mCurrentQueryAutocompleteFormItem == null) return;

        mCurrentQueryAutocompleteFormItem.setOptions(options);
        mSetAutocompleteSuggestionsLiveData.setValue(mCurrentQueryAutocompleteFormItem);
        mShowAutocompleteNoConnectionLiveData.setValue(false);

        mCurrentQueryAutocompleteFormItem = null; //clear reference
    }

    private void onAutocompleteFailure(Throwable throwable) {
        Log.d(TAG, "onAutocompleteFailure: " + throwable.getMessage());
        mShowAutocompleteNoConnectionLiveData.setValue(true);
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

    protected LiveData<DialogMessage> getObservableShowDialogMessage() {
        if (showDialogMessage == null) {
            showDialogMessage = new MutableLiveData<>();
        }
        return showDialogMessage;
    }

    protected LiveData<Boolean> getObservableGoBack() {
        if (goBack == null) {
            goBack = new MutableLiveData<>();
        }
        return goBack;
    }

    protected LiveData<SingleChoiceFormItem> getObservableAddWorkflowTypeItem() {
        if (mAddWorkflowTypeItemLiveData == null) {
            mAddWorkflowTypeItemLiveData = new MutableLiveData<>();
        }
        return mAddWorkflowTypeItemLiveData;
    }

    protected LiveData<IntentFormItem> getObservableAddPeopleInvolvedItem() {
        if (mAddPeopleInvolvedItemLiveData == null) {
            mAddPeopleInvolvedItemLiveData = new MutableLiveData<>();
        }
        return mAddPeopleInvolvedItemLiveData;
    }

    protected LiveData<BaseFormItem> getObservableAddFormItem() {
        if (mAddFormItemLiveData == null) {
            mAddFormItemLiveData = new MutableLiveData<>();
        }
        return mAddFormItemLiveData;
    }

    protected LiveData<List<BaseFormItem>> getObservableSetFormItemList() {
        if (mSetFormItemListLiveData == null) {
            mSetFormItemListLiveData = new MutableLiveData<>();
        }
        return mSetFormItemListLiveData;
    }

    protected LiveData<BaseFormItem> getObservableAddPeopleInvolvedFormItem() {
        if (mAddPeopleInvolvedFormItemLiveData == null) {
            mAddPeopleInvolvedFormItemLiveData = new MutableLiveData<>();
        }
        return mAddPeopleInvolvedFormItemLiveData;
    }

    protected LiveData<List<BaseFormItem>> getObservableSetPeopleInvolvedFormItemList() {
        if (mSetPeopleInvolvedFormItemListLiveData == null) {
            mSetPeopleInvolvedFormItemListLiveData = new MutableLiveData<>();
        }
        return mSetPeopleInvolvedFormItemListLiveData;
    }

    protected LiveData<BaseFormItem> getObservableValidationUi() {
        if (mValidationUiLiveData == null) {
            mValidationUiLiveData = new MutableLiveData<>();
        }
        return mValidationUiLiveData;
    }

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
    }

    protected LiveData<BaseFormItem> getObservableUpdateFormItem() {
        if (mUpdateFormItemLiveData == null) {
            mUpdateFormItemLiveData = new MutableLiveData<>();
        }
        return mUpdateFormItemLiveData;
    }

    protected LiveData<Boolean> getObservableEnableSubmitButton() {
        if (mEnableSubmitButtonLiveData == null) {
            mEnableSubmitButtonLiveData = new MutableLiveData<>();
        }
        return mEnableSubmitButtonLiveData;
    }

    protected LiveData<Boolean> getObservableShowNoPermissionsView() {
        if (mShowNoPermissionsViewLiveData == null) {
            mShowNoPermissionsViewLiveData = new MutableLiveData<>();
        }
        return mShowNoPermissionsViewLiveData;
    }

    protected LiveData<Boolean> getObservableShowFieldsRecycler() {
        if (mShowFieldsRecyclerLiveData == null) {
            mShowFieldsRecyclerLiveData = new MutableLiveData<>();
        }
        return mShowFieldsRecyclerLiveData;
    }

    protected LiveData<Boolean> getObservableShowSubmitButton() {
        if (mShowSubmitButtonLiveData == null) {
            mShowSubmitButtonLiveData = new MutableLiveData<>();
        }
        return mShowSubmitButtonLiveData;
    }

    protected LiveData<DownloadedFileUiData> getObservableDownloadedFileUiData() {
        if (mOpenDownloadedFileLiveData == null) {
            mOpenDownloadedFileLiveData = new MutableLiveData<>();
        }
        return mOpenDownloadedFileLiveData;
    }

    protected LiveData<AutocompleteFormItem> getObservableSetAutocompleteSuggestions() {
        if (mSetAutocompleteSuggestionsLiveData == null) {
            mSetAutocompleteSuggestionsLiveData = new MutableLiveData<>();
        }
        return mSetAutocompleteSuggestionsLiveData;
    }

    protected LiveData<Boolean> getObservableShowAutocompleteNoConnection() {
        if (mShowAutocompleteNoConnectionLiveData == null) {
            mShowAutocompleteNoConnectionLiveData = new MutableLiveData<>();
        }
        return mShowAutocompleteNoConnectionLiveData;
    }

    protected LiveData<Boolean> getObservableShowDynamicFiltersNoType() {
        if (mShowDynamicFiltersNoTypeLiveData == null) {
            mShowDynamicFiltersNoTypeLiveData = new MutableLiveData<>();
        }
        return mShowDynamicFiltersNoTypeLiveData;
    }
}
