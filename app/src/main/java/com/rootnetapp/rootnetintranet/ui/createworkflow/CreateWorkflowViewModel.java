package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.CreateRequest;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePost;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePostDetail;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.createworkflow.PendingFileUpload;
import com.rootnetapp.rootnetintranet.models.createworkflow.ProductFormList;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BooleanFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.DateFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.MultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem.InputType;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.EditRequest;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.FileUploadResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.riddhimanadib.formmaster.FormBuilder;

import static com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings.MACHINE_NAME_TYPE;

public class CreateWorkflowViewModel extends ViewModel {

    private MutableLiveData<WorkflowTypesResponse> mWorkflowsLiveData;
    private MutableLiveData<ListsResponse> mListLiveData;
    private MutableLiveData<ProductsResponse> mProductLiveData;
    private MutableLiveData<ServicesResponse> mServiceLiveData;
    private MutableLiveData<WorkflowUserResponse> mUserLiveData;
    private MutableLiveData<CountriesResponse> mCountriesLiveData;
    private MutableLiveData<CreateWorkflowResponse> mCreateLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCreateErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<SingleChoiceFormItem> mAddWorkflowTypeItemLiveData;
    private MutableLiveData<BaseFormItem> mAddFormItemLiveData;
    private MutableLiveData<List<BaseFormItem>> mSetFormItemListLiveData;
    private MutableLiveData<BaseFormItem> mValidationUiLiveData;
    private MutableLiveData<DialogMessage> showDialogMessage;
    private MutableLiveData<Boolean> goBack;
    private List<WorkflowTypeItemMenu> workflowTypeMenuItems;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    protected MutableLiveData<FieldData> setFileUploadField;
    protected MutableLiveData<Boolean> showUploadButton;
    protected MutableLiveData<Boolean> chooseFile;

    private CreateWorkflowRepository mRepository;

    private static final String TAG = "CreateViewModel";

    private FormSettings formSettings;

    private String mToken;
    private WorkflowListItem mWorkflowListItem;
    private WorkflowDb mWorkflow;

    protected static final int TAG_WORKFLOW_TYPE = 80;

    private final int UPLOAD_FILE_SIZE_LIMIT = 10;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.mRepository = createWorkflowRepository;
        goBack = new MutableLiveData<>();
        showUploadButton = new MutableLiveData<>();
        chooseFile = new MutableLiveData<>();
    }

    protected void initForm(String token, @Nullable WorkflowListItem item) {
        showLoading.setValue(true);
        if (formSettings == null) {
            formSettings = new FormSettings();
        }
        this.mToken = token;
        this.mWorkflowListItem = item;

        createWorkflowTypeItem();
    }

    protected void onCleared() {
        mDisposables.clear();
    }

    private void handleInvalidEmail() {
        DialogMessage dialog = new DialogMessage();
        dialog.title = R.string.warning;
        dialog.message = R.string.form_invalid_email;
        showDialogMessage.setValue(dialog);
    }

    private WorkflowMetas createMetaData(BaseFormItem formItem) {
//        int fieldId = fieldData.tag;
//        TypeInfo typeInfo = formSettings.findFieldDataById(fieldId);
//        if (typeInfo.getValueType().equals(FormSettings.VALUE_EMAIL)) {
//            // TODO put back this value after debugging
//            if(!TextUtils.isEmpty(baseFormElement.getValue())
//                    && !isValidEmail(baseFormElement.getValue())) {
//               handleInvalidEmail();
//               return null;
//            }
//        }

        WorkflowMetas workflowMeta = new WorkflowMetas();
        int workflowTypeFieldId = formItem.getTag();
        String value = formItem.getStringValue();
        workflowMeta.setUnformattedValue(value);
        workflowMeta.setWorkflowTypeFieldId(workflowTypeFieldId);
        formSettings.formatMetaData(workflowMeta, formItem);
        return workflowMeta;
    }

    protected void showUploadFilePicker() {
        chooseFile.setValue(true);
    }

    protected void selectUploadFile(String path, File file) {
        if (!Utils.checkFileSize(UPLOAD_FILE_SIZE_LIMIT, file)) {
            DialogMessage message = new DialogMessage();
            message.message = R.string.file_too_big;
            message.title = R.string.warning;
            showDialogMessage.setValue(message);
            return;
        }

        PendingFileUpload pendingFileUpload = formSettings.getPendingFileUpload();
        pendingFileUpload.fileName = path.substring(path.lastIndexOf("/") + 1);
        pendingFileUpload.path = path;
        ListFieldItemMeta field = new ListFieldItemMeta(0, pendingFileUpload.fileName);
        ArrayList<ListFieldItemMeta> items = new ArrayList<>();
        items.add(field);
        pendingFileUpload.fieldData.list = items;
        pendingFileUpload.file = file;

        setFileUploadField.setValue(pendingFileUpload.fieldData);

        //todo check

//        formSettings.addFieldDataItem(pendingFileUpload.fieldData);
    }

    private void postFileRequest(String path, String encodedFileString) {
        FilePostDetail filePostDetail = new FilePostDetail();
        filePostDetail.setFile(encodedFileString);
        String extension = path.substring(path.lastIndexOf("."));
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        filePostDetail.setType(extension);
        filePostDetail.setName(fileName);
        filePostDetail.setFile(encodedFileString);

        FilePost filePost = new FilePost();
        filePost.setFile(filePostDetail);

        showLoading.setValue(true);
        Disposable disposable = mRepository.uploadFile(mToken, filePost)
                .subscribe(fileUploadResponse -> {
                    showLoading.setValue(false);
                    successUpload(fileUploadResponse);

                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "postFileRequest: file upload failed: " + throwable.getMessage());
                });

        mDisposables.add(disposable);
    }

    private void successUpload(FileUploadResponse fileUploadResponse) {
        //todo implement
        /*showLoading.setValue(true);
        ArrayList<FieldData> fieldItems = formSettings.getFormItemsToPost();
        ArrayMap<String, Integer> baseInfo = formSettings.getBaseMachineNamesAndIds();
        PendingFileUpload pendingFileUpload = formSettings.getPendingFileUpload();
        int id = fileUploadResponse.getFileId();
        pendingFileUpload.fileId = id;
        startSendingWorkflow(baseInfo, formSettings.getFormBuilder(), fieldItems);*/
    }

    private void postWorkflow() {
        List<BaseFormItem> formItemsForPost = formSettings.getFormItemsToPost();
        ArrayMap<String, Integer> baseInfo = formSettings.getBaseMachineNamesAndIds();

        if (baseInfo.isEmpty()) {
            Log.d(TAG, "postWorkflow: Need to initalize baseInfo");
            DialogMessage dialogMessage = new DialogMessage();
            dialogMessage.message = R.string.choose_a_workflow;
            dialogMessage.title = R.string.required_fields;
            showDialogMessage.setValue(dialogMessage);
            return;
        }

        showLoading.setValue(true);

        // TODO check if we have a file to upload first and then continue with the rest.
        if (formSettings.getPendingFileUpload() == null || formSettings
                .getPendingFileUpload().file == null) {
            startSendingWorkflow(baseInfo, formItemsForPost);
            return;
        }

        //for using later during actual post.
        //todo implement
       /* formSettings.setFormBuilder(formBuilder);
        PendingFileUpload pendingFileUpload = formSettings.getPendingFileUpload();

        Disposable disposable = Observable.fromCallable(() -> {
            String fileString = Utils.encodeFileToBase64Binary(pendingFileUpload.file);
            return fileString;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(encodedFileString -> {
                    showLoading.setValue(false);
                    postFileRequest(pendingFileUpload.path, encodedFileString);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "uploadFile: Error while encoding to Base64");
                });
        mDisposables.add(disposable);*/

    }

    private void startSendingWorkflow(ArrayMap<String, Integer> baseInfo,
                                      List<BaseFormItem> formItems) {
        int titleTag = baseInfo.get(FormSettings.MACHINE_NAME_TITLE);
        int descriptionTag = baseInfo.get(FormSettings.MACHINE_NAME_DESCRIPTION);
        int startTag = baseInfo.get(FormSettings.MACHINE_NAME_START_DATE);

        int workflowTypeId = formSettings.getWorkflowTypeIdSelected();
        String title = ((TextInputFormItem) formSettings.findItem(titleTag)).getValue();
        String description = ((TextInputFormItem) formSettings.findItem(descriptionTag)).getValue();
        String start = Utils
                .getDatePostFormat(((DateFormItem) formSettings.findItem(startTag)).getValue());

        List<WorkflowMetas> metas = new ArrayList<>();
        WorkflowMetas workflowMetas;
        String value;

        for (int i = 0; i < formItems.size(); i++) {
            BaseFormItem formItem = formItems.get(i);

            /*int fieldId = fieldData.tag;
            // Check for phone and currency fields
            if (!hasValidFields(fieldId, baseFormElement.getValue())) {
                showLoading.setValue(false);
                return;
            }

            int customId = baseFormElement.getTag();
            if (customId == FormSettings.FIELD_CODE_ID) {
                Log.d(TAG, "postWorkflow: found");
                formSettings.setCountryCode(baseFormElement.getValue(), fieldData);
                continue;
            }

            if (customId == FormSettings.FIELD_CURRENCY_ID) {
                Log.d(TAG, "postWorkflow: found");
                formSettings.setCurrencyType(baseFormElement.getValue(), fieldData);
                continue;
            }*/

            workflowMetas = createMetaData(formItem);
            if (workflowMetas == null) {
                showLoading.setValue(false);
                return;
            }

            if (workflowMetas.getValue() != null && !workflowMetas.getValue().isEmpty()) {
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
            postCreateToServer(metas, workflowTypeId, title, start, description);

        } else {
            //edit workflow
            patchEditToServer(metas, mWorkflowListItem.getWorkflowId(), title, start, description);
        }
    }

    private boolean hasValidFields(int fieldId, String value) {
        //todo check
//        TypeInfo typeInfo = formSettings.findFieldDataById(fieldId);
        TypeInfo typeInfo = new TypeInfo();
        DialogMessage dialogMessage;
        if (typeInfo != null
                && typeInfo.getType().equals(FormSettings.TYPE_PHONE)
                && !TextUtils.isEmpty(value)
                && !formSettings.hasValidCountryCode()
                ) {
            dialogMessage = new DialogMessage();
            dialogMessage.message = R.string.fill_country_code;
            dialogMessage.title = R.string.warning;
            showDialogMessage.setValue(dialogMessage);
            return false;
        }

        if (typeInfo != null
                && typeInfo.getType().equals(FormSettings.TYPE_CURRENCY)
                && !TextUtils.isEmpty(value)
                && !formSettings.hasValidCountryCurrency()
                ) {
            dialogMessage = new DialogMessage();
            dialogMessage.message = R.string.fill_currency_code;
            dialogMessage.title = R.string.warning;
            showDialogMessage.setValue(dialogMessage);
            return false;
        }

        if (typeInfo != null
                && typeInfo.getValueType().equals(FormSettings.VALUE_EMAIL)) {
            // TODO put back this value after debugging
            if (!TextUtils.isEmpty(value)
                    && !isValidEmail(value)) {
                handleInvalidEmail();
                return false;
            }
        }

        return true;
    }

    private void postCreateToServer(List<WorkflowMetas> metas, int workflowTypeId, String title,
                                    String start, String description) {
        CreateRequest createRequest = new CreateRequest();
        createRequest.workflowTypeId = workflowTypeId;
        createRequest.title = title;
        createRequest.metas = metas;
        createRequest.start = start;
        createRequest.description = description;

        // TODO remove this block later only for debugging
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CreateRequest> jsonAdapter = moshi.adapter(CreateRequest.class);
        String jsonString = jsonAdapter.toJson(createRequest);

        // Accepts object
        Disposable disposable = mRepository
                .createWorkflow(mToken, createRequest)
                .subscribe(this::onCreateSuccess, this::onCreateFailure);

        mDisposables.add(disposable);
    }

    private void patchEditToServer(List<WorkflowMetas> metas, int workflowId, String title,
                                   String start, String description) {
        EditRequest editRequest = new EditRequest();
        editRequest.setWorkflowId(workflowId);
        editRequest.setTitle(title);
        editRequest.setStart(start);
        editRequest.setDescription(description);
        editRequest.setWorkflowMetas(metas);

        // TODO remove this block later only for debugging
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<EditRequest> jsonAdapter = moshi.adapter(EditRequest.class);
        String jsonString = jsonAdapter.toJson(editRequest);

        // Accepts object
        Disposable disposable = mRepository
                .editWorkflow(mToken, editRequest)
                .subscribe(this::onEditSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    protected void generateFieldsByType(String typeName) {
        int id = formSettings.findIdByTypeName(typeName);
        if (id == 0) {
            showLoading.setValue(false);
            return;
        }

        showLoading.setValue(true);
        clearForm();
        formSettings.setWorkflowTypeIdSelected(id);
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = mRepository
                    .getFiedsByWorkflowType(id);
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
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formSettings -> {
                    FormSettings settings = (FormSettings) formSettings;
                    showFields(settings);
                }, throwable -> {
                    showLoading.setValue(false);
                });
        mDisposables.add(disposable);

    }

    /**
     * Removes all of the current form items and sends the data to the UI to remove them aswell.
     */
    protected void clearForm() {
        showUploadButton.setValue(false);
        formSettings.clearFormItems();
        mSetFormItemListLiveData.setValue(formSettings.getFormItems());
    }

    /**
     * Creates all of the form items according to their params. Sends each form item to the UI.
     *
     * @param formSettings holder of the current form params.
     */
    private void showFields(FormSettings formSettings) {
        List<FormFieldsByWorkflowType> fields = formSettings.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FormFieldsByWorkflowType field = fields.get(i);
            if (!field.isShowForm()) {
                continue;
            }

            FieldConfig fieldConfig = field.getFieldConfigObject();
            if (fieldConfig.isPrecalculated()) {
                continue;
            }

            buildField(field);
        }

        mSetFormItemListLiveData.setValue(formSettings.getFormItems());

        if (mWorkflowListItem == null) {
            showLoading.setValue(false);
            mSetFormItemListLiveData.setValue(formSettings.getFormItems());
            return;
        }

        // edit mode
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
    }

    /**
     * Create a specific form item according to its params.
     *
     * @param field the form item params.
     */
    private void buildField(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

//        if (field.getFieldConfigObject().getMultiple()) return;

        boolean isMultiple = field.getFieldConfigObject().getMultiple();

        switch (typeInfo.getType()) {

            case FormSettings.TYPE_TEXT:
            case FormSettings.TYPE_TEXT_AREA:
                createTextInputFormItem(field);
                break;

            case FormSettings.TYPE_LINK:
                //value type is "text", does not work for us, so we change it to "link"
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
                if (isMultiple) {
                    createCustomMultipleListFormItem(field);
                } else {
                    createCustomListFormItem(field);
                }
                break;
            /*
            case FormSettings.TYPE_PHONE:
                handleBuildPhone(field);
                break;
            case FormSettings.TYPE_CURRENCY:
                handleCurrencyType(field);
                break;
            case FormSettings.TYPE_FILE:
                handleFile(field);
                break;*/
            default:
                Log.d(TAG, "buildField: Not a generic type: " + typeInfo
                        .getType() + " value: " + typeInfo.getValueType());
                break;
        }
    }

    //region Create Form Items

    /**
     * Creates the WorkflowType form item. Performs a request to the repo to retrieve the options
     * and then send the form item to the UI.
     */
    private void createWorkflowTypeItem() {
        //used to be setWorkflowTypes

        Disposable disposable = Observable.fromCallable(() -> {
            List<WorkflowTypeItemMenu> types = mRepository.getWorklowTypeNames();
            if (types == null || types.size() < 1) {
                return false;
            }

            Option selectedOption = null; //used only in edit mode
            List<Option> options = new ArrayList<>();
            for (int i = 0; i < types.size(); i++) {
                String name = types.get(i).getName();
                Integer id = types.get(i).getId();
                formSettings.setId(id);
                formSettings.setName(name);

                Option option = new Option(id, name);
                options.add(option);

                if (mWorkflowListItem != null && mWorkflowListItem.getWorkflowTypeId() == id) {
                    selectedOption = option;
                }
            }

            SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                    .setTitleRes(R.string.type)
                    .setRequired(true)
                    .setTag(TAG_WORKFLOW_TYPE)
                    .setOptions(options)
                    .setValue(selectedOption)
                    .setEnabled(selectedOption == null) //if we are in edit mode, disable it
                    .setVisible(selectedOption == null) //if we are in edit mode, hide it
                    .setMachineName(MACHINE_NAME_TYPE)
                    .build();

            formSettings.getFormItems().add(singleChoiceFormItem);

            return singleChoiceFormItem;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleChoiceFormItem -> {
                    showLoading.setValue(false);
                    mAddWorkflowTypeItemLiveData
                            .setValue((SingleChoiceFormItem) singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG, "setWorkflowTypes: error " + throwable.getMessage());
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
                    showLoading.setValue(false);

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

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "createProductsFormItem: " + throwable.getMessage());
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
                    showLoading.setValue(false);

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

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "createRolesFormItem: " + throwable.getMessage());
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
                    showLoading.setValue(false);

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

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    Log.d(TAG,
                            "createServicesFormItem: can't get service: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        mDisposables.add(disposable);
    }

    /**
     * Creates the Projects form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createProjectsFormItem(FormFieldsByWorkflowType field) {
//        // TODO endpoint at this point returns an empty array.
//        Disposable disposable = mRepository
//                .getProjects(mToken)
//                .subscribe(projectResponse -> {
//                    showLoading.setValue(false);
//
//                    if (projectResponse.getCode() != 200) {
//                        return;
//                    }
//                    List<Project> list = projectResponse.getProjects();
//
//                    List<Option> options = new ArrayList<>();
//                    for (int i = 0; i < list.size(); i++) {
//                        String name = list.get(i).getName();
//                        Integer id = list.get(i).getId();
//
//                        Option option = new Option(id, name);
//                        options.add(option);
//                    }
//
//                    if (options.isEmpty()) return;
//
//                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
//                            .setTitle(field.getFieldName())
//                            .setRequired(field.isRequired())
//                            .setTag(field.getId())
//                            .setOptions(options)
//                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
//                            .setMachineName(field.getFieldConfigObject().getMachineName())
//                            .build();
//
//                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
//                }, throwable -> {
//                    showLoading.setValue(false);
//                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
//                });
//
//        mDisposables.add(disposable);
    }

    /**
     * Creates the SystemUsers form item. Performs a request to the repo to retrieve the options and
     * then send the form item to the UI.
     */
    private void createSystemUsersFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();

        Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> list = formSettings.getProfiles();

            if (list == null || list.size() < 1) {
                return false;
            }

            List<Option> options = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i).getFullName();
                Integer id = list.get(i).getId();

                Option option = new Option(id, name);
                options.add(option);
            }

            return new SingleChoiceFormItem.Builder()
                    .setTitle(field.getFieldName())
                    .setRequired(field.isRequired())
                    .setTag(field.getId())
                    .setOptions(options)
                    .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                    .setMachineName(field.getFieldConfigObject().getMachineName())
                    .build();

        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(singleChoiceFormItem -> {
                    showLoading.setValue(false);

                    mAddFormItemLiveData.setValue((SingleChoiceFormItem) singleChoiceFormItem);
                }, throwable -> {
                    showLoading.setValue(false);
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
                    showLoading.setValue(false);

                    List<ListItem> list = listsResponse.getItems();

                    if (list == null || list.size() < 1) {
                        return;
                    }

                    List<Option> options = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        String name = list.get(i).getName();
                        Integer id = list.get(i).getId();

                        Option option = new Option(id, name);
                        options.add(option);
                    }

                    SingleChoiceFormItem singleChoiceFormItem = new SingleChoiceFormItem.Builder()
                            .setTitle(field.getFieldName())
                            .setRequired(field.isRequired())
                            .setTag(field.getId())
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(singleChoiceFormItem);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
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
                    showLoading.setValue(false);

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
                            .setOptions(options)
                            .setTypeInfo(field.getFieldConfigObject().getTypeInfo())
                            .setMachineName(field.getFieldConfigObject().getMachineName())
                            .build();

                    mAddFormItemLiveData.setValue(multipleChoiceFormItem);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
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
    }

    /**
     * Creates a custom Date item with the specified params and sends the item to the UI.
     *
     * @param field item params.
     */
    private void createDateFormItem(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        String valueType = typeInfo.getValueType();

        if (!valueType.equals(FormSettings.VALUE_DATE)) {
            Log.d(TAG, "createDateFormItem: Value not recognized " + valueType);
            return;
        }

        DateFormItem item = new DateFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
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

        if (!valueType.equals(FormSettings.VALUE_BOOLEAN)) {
            Log.d(TAG, "createBooleanFormItem: Value not recognized: " + valueType);
            return;
        }

        BooleanFormItem item = new BooleanFormItem.Builder()
                .setTitle(field.getFieldName())
                .setRequired(field.isRequired())
                .setTag(field.getId())
                .setEscaped(escape(field.getFieldConfigObject()))
                .setMachineName(field.getFieldConfigObject().getMachineName())
                .setTypeInfo(typeInfo)
                .build();

        formSettings.getFormItems().add(item);
    }
    //endregion

    //region Fill Data

    /**
     * Performs a request to the remote repository to fetch the workflow information in order to
     * fill the form items.
     *
     * @param auth       token
     * @param workflowId ID of the workflow that is being edited.
     */
    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(auth, workflowId)
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
        titleItem.setValue(workflow.getTitle());

        TextInputFormItem descriptionItem = (TextInputFormItem) formSettings
                .findItem(FormSettings.MACHINE_NAME_DESCRIPTION);
        descriptionItem.setValue(workflow.getDescription());

        DateFormItem startDateItem = (DateFormItem) formSettings
                .findItem(FormSettings.MACHINE_NAME_START_DATE);
        Date startDate = Utils.getDateFromString(workflow.getStart(), Utils.SERVER_DATE_FORMAT);
        startDateItem.setValue(startDate);
//        String endDate = Utils.serverFormatToFormat(workflow.getEnd(), FORMAT);

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
                        fillTextInputFormItem(meta, fieldConfig);
                        break;

                    case FormSettings.TYPE_CHECKBOX:
                        fillBooleanFormItem(meta, fieldConfig);
                        break;

                    case FormSettings.TYPE_DATE:
                        fillDateFormItem(meta, fieldConfig);
                        break;

                    /*case FormSettings.VALUE_EMAIL:
                        if (fieldConfig.getMultiple()) {
                            return null;
                        }
                        if (!(meta.getDisplayValue() instanceof String)) {
                            information.setDisplayValue("");
                            return information;
                        }

                        information.setDisplayValue((String) meta.getDisplayValue());
                        return information;
                    case FormSettings.VALUE_INTEGER:

                        if (fieldConfig.getMultiple()) {
                            return null;
                        }

                        if (typeInfo.getType().equals(TYPE_TEXT)) {
                            if (!(meta.getDisplayValue() instanceof String)) {
                                information.setDisplayValue("");
                                return information;
                            }
                            information.setDisplayValue((String) meta.getDisplayValue());
                            return information;
                        }

                        if (typeInfo.getType().equals(TYPE_CURRENCY)) {
                            PostCountryCodeAndValue currency;
                            JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                                    .adapter(PostCountryCodeAndValue.class);
                            try {
                                currency = jsonAdapter.fromJson(meta.getValue());
                                information.setDisplayValue(String.valueOf(currency.value));
                                return information;
                            } catch (IOException e) {
                                e.printStackTrace();
                                information.setDisplayValue("");
                                return information;
                            } catch (JsonDataException e) {
                                e.printStackTrace();
                                information.setDisplayValue("");
                                return information;
                            }
                        }

                        if (typeInfo.getType().equals(TYPE_FILE)) {
                            // TODO handle file.
                            return null;
                        }

                        return null;
                    case FormSettings.VALUE_ENTITY:
                        if (typeInfo.getType().equals(TYPE_ROLE)) {
                            String displayValue = getLabelFrom(meta);
                            information.setDisplayValue(displayValue);
                            return information;
                        }

                        return null;
                    case FormSettings.VALUE_LIST:
                        if (typeInfo.getType().equals(TYPE_SYSTEM_USERS)) {
                            // TODO implement system user field
//                    if (fieldConfig.getMultiple()) {
//                        // {"id":50,"username":"jhonny Garzon","status":true,"email":"jgarzon600@gmail.com"}
//                    } else {
//
//                    }

                            Moshi moshi = new Moshi.Builder().build();
                            JsonAdapter<PostSystemUser> jsonAdapter = moshi.adapter(PostSystemUser.class);
                            try {
                                PostSystemUser systemUser = jsonAdapter.fromJson(meta.getValue());
                                information.setDisplayValue(systemUser.username);
                                return information;
                            } catch (IOException e) {
                                e.printStackTrace();
                                information.setDisplayValue("");
                                return information;
                            }
                        }

                        String displayValue = getLabelFrom(meta);
                        information.setDisplayValue(displayValue);
                        return information;
                    case FormSettings.VALUE_STRING:
                        // Until now phone type can only be single and not multiple
                        if (typeInfo.getType().equals(TYPE_PHONE)) {
                            PostCountryCodeAndValue phone;
                            JsonAdapter<PostCountryCodeAndValue> jsonAdapter = moshi
                                    .adapter(PostCountryCodeAndValue.class);
                            try {
                                phone = jsonAdapter.fromJson(meta.getValue());
                                information.setDisplayValue(String.valueOf(phone.value));
                                return information;
                            } catch (IOException e) {
                                e.printStackTrace();
                                information.setDisplayValue("");
                                return information;
                            } catch (JsonDataException e) {
                                e.printStackTrace();
                                information.setDisplayValue("");
                                return information;
                            }
                        }

                        if (!(meta.getDisplayValue() instanceof String)) {
                            information.setDisplayValue("");
                            return information;
                        }

                        information.setDisplayValue((String) meta.getDisplayValue());
                        return information;*/

                    default:
                        Log.d(TAG, "format: invalid type. Not Known.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "updateWorkflowInformation: " + e.getMessage());
            }
        }

        showLoading.setValue(false);
        mSetFormItemListLiveData.setValue(formSettings.getFormItems());
    }

    private void fillTextInputFormItem(Meta meta, FieldConfig fieldConfig) {
        if (fieldConfig.getMultiple()) {
            return;
        }

        String value = String.valueOf(meta.getDisplayValue());

        TextInputFormItem textInputFormItem = (TextInputFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());

        textInputFormItem.setValue(value);
    }

    private void fillBooleanFormItem(Meta meta, FieldConfig fieldConfig) {
        if (fieldConfig.getMultiple()) {
            return;
        }

        String value = meta.getValue();

        BooleanFormItem booleanFormItem = (BooleanFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());
        booleanFormItem.setValue(Boolean.valueOf(value));
    }

    private void fillDateFormItem(Meta meta, FieldConfig fieldConfig) {
        if (fieldConfig.getMultiple()) {
            return;
        }

        String value = String.valueOf(meta.getDisplayValue()); // now returns "10/25/2018"

        DateFormItem startDateItem = (DateFormItem) formSettings
                .findItem(meta.getWorkflowTypeFieldId());
        Date startDate = Utils.getDateFromString(value, "dd/MM/yyyy");
        startDateItem.setValue(startDate);
    }
    //endregion

    /**
     * This is called by the View when the user submits the form.
     */
    protected void handleCreateWorkflowAction() {
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
        boolean isValid = true;

        for (BaseFormItem item : formSettings.getFormItems()) {
            if (!item.isValid()) {
                isValid = false;
            }
        }

        mValidationUiLiveData.setValue(formSettings.findFirstInvalidItem());

        return isValid;
    }

    private void handleFile(FormFieldsByWorkflowType field) {
        showUploadButton.setValue(true);

        FieldConfig fieldConfig = field.getFieldConfigObject();
//        int listId = fieldConfig.getTypeInfo().getId();
        int customFieldId = field.getId();
        String customLabel = field.getFieldName();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        ListField listField = new ListField();
//        listField.listId = listId;
        listField.customFieldId = customFieldId;
        listField.customLabel = customLabel;
        listField.associatedWorkflowTypeId = associatedWorkflowTypeId;
        listField.isMultipleSelection = false;

        FieldData fieldData = new FieldData();
        fieldData.label = customLabel;
        fieldData.isMultipleSelection = false;
        fieldData.tag = customFieldId;
        fieldData.escape = false;

        PendingFileUpload pendingFileUpload = new PendingFileUpload();
        pendingFileUpload.fieldData = fieldData;

        formSettings.setPendingFileUpload(pendingFileUpload);

//        setListWithData.setValue(fieldData);
//        buildForm.setValue(true);
//        showLoading.setValue(false);

    }

    public void checkForContent(FormBuilder formBuilder) {
        //todo check (validation)
        /*List<FieldData> list = formSettings.getFieldItems();
        FieldData field;
        BaseFormElement baseFormElement;
        ArrayList<BaseFormElement> emptyRequiredElements = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            field = list.get(i);
            baseFormElement = formBuilder.getFormElement(field.tag);
            if (baseFormElement.isRequired() && TextUtils.isEmpty(baseFormElement.getValue())) {
                if (baseFormElement.getTag() != TAG_WORKFLOW_TYPE) {
                    emptyRequiredElements.add(baseFormElement);
                }
            }
        }

        if (emptyRequiredElements.size() == 0) {
            return;
        }

        int size = emptyRequiredElements.size();
        String[] fieldNames = new String[size];
        for (int i = 0; i < emptyRequiredElements.size(); i++) {
            fieldNames[i] = emptyRequiredElements.get(i).getTitle();
        }

        int title = R.string.required_fields;
        int message = R.string.complete_form;
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.list = fieldNames;
        dialogMessage.title = title;
        dialogMessage.message = message;

        showDialogMessage.setValue(dialogMessage);*/

    }

    private static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void handleCurrencyType(FormFieldsByWorkflowType field) {
        //todo implement
        FieldConfig fieldConfig = field.getFieldConfigObject();
        Disposable disposable = mRepository
                .getCurrencyCodes()
                .subscribe(currencyFieldData -> {
                    //list
                    /*ListField listField = new ListField();
                    listField.customFieldId = FormSettings.FIELD_CURRENCY_ID;
                    listField.listType = FormSettings.TYPE_CURRENCY;
                    listField.resStringId = R.string.currency_type;
                    ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
                    CurrencyFieldData currencyData;
                    String currencyLabel;
                    for (int i = 0; i < currencyFieldData.size(); i++) {
                        currencyData = currencyFieldData.get(i);
                        currencyLabel = currencyData.description + " - " + currencyData.currency;
                        ListFieldItemMeta item = new ListFieldItemMeta(
                                currencyData.countryId,
                                currencyLabel
                        );
                        tempList.add(item);
                    }
                    listField.children = tempList;
                    listField.isMultipleSelection = false;
                    listField.associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

                    if (listField.children.size() < 1) {
                        return;
                    }

                    showListField(listField, fieldConfig);

                    FieldData currencyNumberFieldData = new FieldData();
                    currencyNumberFieldData.label = field.getFieldName();
                    currencyNumberFieldData.required = field.isRequired();
                    currencyNumberFieldData.tag = field.getId();
                    currencyNumberFieldData.escape = escape(field.getFieldConfigObject());
//                    formSettings.addFieldDataItem(currencyNumberFieldData);
                    setFieldPhoneWithData.setValue(currencyNumberFieldData);*/
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
                });

        mDisposables.add(disposable);
    }

    private void handleBuildPhone(FormFieldsByWorkflowType field) {
        //todo implement
        FieldConfig fieldConfig = field.getFieldConfigObject();
        Disposable disposable = mRepository
                .getCountryCodes()
                .subscribe(phoneFieldData -> {
                    //list
                    /*ListField listField = new ListField();
                    listField.customFieldId = FormSettings.FIELD_CODE_ID;
                    listField.listType = FormSettings.TYPE_PHONE;
                    listField.resStringId = R.string.country_code;
                    ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
                    PhoneFieldData phoneCode;
                    String codeLabel;
                    for (int i = 0; i < phoneFieldData.size(); i++) {
                        phoneCode = phoneFieldData.get(i);
                        codeLabel = phoneCode.phoneCode + " - " + phoneCode.description;
                        ListFieldItemMeta item = new ListFieldItemMeta(
                                phoneCode.countryId,
                                codeLabel
                        );
                        tempList.add(item);
                    }
                    listField.children = tempList;
                    listField.isMultipleSelection = false;
                    listField.associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

                    if (listField.children.size() < 1) {
                        return;
                    }

                    showListField(listField, fieldConfig);

                    // Normal phone numeric field
                    FieldData phoneNumberFieldData = new FieldData();
                    phoneNumberFieldData.label = field.getFieldName();
                    phoneNumberFieldData.required = field.isRequired();
                    phoneNumberFieldData.tag = field.getId();
                    phoneNumberFieldData.escape = escape(field.getFieldConfigObject());
//                    formSettings.addFieldDataItem(phoneNumberFieldData);
                    setFieldPhoneWithData.setValue(phoneNumberFieldData);*/
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
                });

        mDisposables.add(disposable);
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
                || value.equals(FormSettings.VALUE_COORD)) {
            return true;
        }
        return value.equals(FormSettings.VALUE_LIST) && type.equals(FormSettings.TYPE_SYSTEM_USERS);
    }

    private void handleBuildEntity(FormFieldsByWorkflowType field) {
        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        switch (valueType) {
            case FormSettings.VALUE_ENTITY:
                // TODO list
                break;
            default:
                Log.d(TAG, "handleBuildProject: Not recognized " + valueType);
        }
    }

    @Deprecated
    private void showListField(ListField listField, FieldConfig fieldConfig) {
        //todo check
       /* FieldData fieldData = new FieldData();
        fieldData.label = listField.customLabel;
        fieldData.list = listField.children;
        fieldData.resLabel = listField.resStringId;
        fieldData.isMultipleSelection = listField.isMultipleSelection;
        fieldData.tag = listField.customFieldId;
        fieldData.escape = escape(fieldConfig);
        setListWithData.setValue(fieldData);
        buildForm.setValue(true);
        showLoading.setValue(false);
        formSettings.addFieldDataItem(fieldData);*/
    }

    @Deprecated
    private void handleList(FormFieldsByWorkflowType field, String fieldType) {
        //todo check
        /*FieldConfig fieldConfig = field.getFieldConfigObject();
        String valueType = fieldConfig.getTypeInfo().getValueType();
        if (!valueType.equals(FormSettings.VALUE_LIST)) {
            return;
        }

        boolean isMultipleSelection;
        if (fieldType.equals(FormSettings.TYPE_SYSTEM_USERS)) {
            isMultipleSelection = false;
        } else {
            isMultipleSelection = fieldConfig.getMultiple();
        }

        ListInfo listInfo = fieldConfig.getListInfo();
        if (listInfo == null) {
            if (fieldConfig.getTypeInfo().getType().equals(FormSettings.TYPE_SYSTEM_USERS)) {
                setTeamList(field);
            }
            return;
        }

        // It is not a base list of type system user and we have other custom fields at this point.

        int listId = fieldConfig.getListInfo().getId();
        int customFieldId = field.getId();
        String customLabel = field.getFieldName();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        if (fieldType.equals(FormSettings.TYPE_SYSTEM_USERS)) {
            createSystemUserFieldasCustomField(field, listId, customFieldId, customLabel,
                    associatedWorkflowTypeId);
            return;
        }

        Disposable disposable = mRepository
                .getList(mToken, listId)
                .subscribe(listsResponse -> {
                    List<ListItem> listItems = listsResponse.getItems();
                    ListItem listItem;
                    ListField listField = null;
                    int id;
                    for (int i = 0; i < listItems.size(); i++) {
                        listItem = listItems.get(i);
                        id = listItem.getListId();
                        if (id != listId) {
                            continue;
                        }
                        listField = formSettings.addListToForm(
                                listItem,
                                customLabel,
                                customFieldId,
                                FormSettings.TYPE_LIST
                        );
                        listField.isMultipleSelection = isMultipleSelection;
                        listField.associatedWorkflowTypeId = associatedWorkflowTypeId;
                        break;
                    }

                    if (listField == null || listField.children.size() < 1) {
                        return;
                    }
                    showListField(listField, fieldConfig);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
                });

        mDisposables.add(disposable);*/
    }

    @Deprecated
    private void saveTypeToFormSettings(FieldListSettings fieldListSettings) {
        //todo check
        /*int customLabel = fieldListSettings.labelRes;

        ArrayList<String> stringListItems = fieldListSettings.items;
        ArrayList<ListFieldItemMeta> listData = new ArrayList<>();
        String name;
        ListFieldItemMeta itemMeta;

        for (int i = 0; i < stringListItems.size(); i++) {
            name = stringListItems.get(i);
            itemMeta = new ListFieldItemMeta(
                    TAG_WORKFLOW_TYPE,
                    name
            );
            listData.add(itemMeta);
        }
        FieldData fieldData = new FieldData();
        fieldData.resLabel = customLabel;
        fieldData.list = listData;
        fieldData.tag = TAG_WORKFLOW_TYPE;
        fieldData.isMultipleSelection = false;
        fieldData.escape = false; // workflow types are not send in meta data.
        formSettings.addFieldDataItem(fieldData);*/
    }

    private void setBaseFields() {
        //todo check
        /*int[] settingsDataTitle = new int[2];
        settingsDataTitle[INDEX_RES_STRING] = R.string.title;
        settingsDataTitle[INDEX_REQUIRED] = REQUIRED;
        setTextField.postValue(settingsDataTitle);
        int[] settingDataMulti = new int[2];
        settingDataMulti[INDEX_RES_STRING] = R.string.description;
        settingDataMulti[INDEX_REQUIRED] = REQUIRED;
        setTextFieldMultiLine.postValue(settingDataMulti);
        int[] settingDatePicker = new int[2];
        settingDatePicker[INDEX_RES_STRING] = R.string.start_date;
        settingDatePicker[INDEX_REQUIRED] = REQUIRED;
        setDatePicker.postValue(settingDatePicker);
        setFormHeader.postValue(R.string.workflow_team);
        //setTeamList();*/
    }

    @Deprecated
    private void setTeamList(FormFieldsByWorkflowType field) {
        //todo check
        /*Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> profiles = mRepository.getProfiles();
            if (profiles == null || profiles.size() < 1) {
                return false;
            }
            for (int i = 0; i < profiles.size(); i++) {
                formSettings.setProfile(profiles.get(i));
            }
            formSettings.getProfileNames();
            ArrayList<String> stringListItems = formSettings.getProfileNames();
            ArrayList<Integer> idList = formSettings.getProfileIds();
            FieldListSettings fieldListSettings = new FieldListSettings();
            fieldListSettings.items = stringListItems;
            fieldListSettings.labelRes = R.string.owner;
            fieldListSettings.required = true;
            fieldListSettings.tag = field.getId();

            FieldConfig fieldConfig = field.getFieldConfigObject();
            boolean isMultipleSelection = fieldConfig.getMultiple();
            String customLabel = field.getFieldName();
            int customFieldId = field.getId();

            ArrayList<ListFieldItemMeta> listData = new ArrayList<>();
            String name;
            int id;
            for (int i = 0; i < stringListItems.size(); i++) {
                name = stringListItems.get(i);
                id = idList.get(i);
                ListFieldItemMeta itemMeta = new ListFieldItemMeta(
                        id,
                        name
                );
                listData.add(itemMeta);
            }
            FieldData fieldData = new FieldData();
            fieldData.label = customLabel;
            fieldData.list = listData;
            fieldData.tag = field.getId();
            fieldData.isMultipleSelection = isMultipleSelection;
            fieldData.escape = escape(fieldConfig);
            formSettings.addFieldDataItem(fieldData);
            //setFieldList.postValue(fieldListSettings);
            return fieldListSettings;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fieldListSettings -> {
                    FieldListSettings settings = (FieldListSettings) fieldListSettings;
                    setFieldList.setValue(settings);
                    showLoading.setValue(false);
                    buildForm.setValue(true);
                }, throwable -> {
                    showLoading.setValue(false);
                    buildForm.setValue(true);
                });
        mDisposables.add(disposable);*/
    }

    private String formatUiDateToPostDate(String uiDateFormat) {
        //todo check
        /*SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault());
        String metaDateString = "";
        try {
            Date convertedDate = dateFormat.parse(uiDateFormat);
            SimpleDateFormat serverFormat = new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault());
            metaDateString = serverFormat.format(convertedDate);
        } catch (ParseException e) {
            Log.d(TAG, "StringDateToTimestamp: e = " + e.getMessage());
        }
        return metaDateString;*/
        return null;
    }

    public void getWorkflowTypes(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        Log.d("test", "getWorkflowTypes: ");
        mRepository.getWorkflowTypes(auth)
                .subscribe(this::onTypesSuccess, this::onFailure);
    }

    public void getList(String auth, int id) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.getList(auth, id).subscribe(this::onListSuccess, this::onFailure);
    }

    public void getProducts(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.getProducts(auth)
                .subscribe(this::onProductsSuccess, this::onFailure);
    }

    public void getServices(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.getServices(auth)
                .subscribe(this::onServicesSuccess, this::onFailure);
    }

    public void getUsers(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.getUsers(auth).subscribe(this::onUsersSuccess, this::onFailure);
    }

    public void getCountries(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.getCountries(auth)
                .subscribe(this::onCountriesSuccess, this::onFailure);
    }

    public void createWorkflow(String auth, int workflowTypeId, String title, String workflowMetas,
                               String start, String description) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        mRepository.createWorkflow(auth, workflowTypeId, title, workflowMetas,
                start, description).subscribe(this::onCreateSuccess, this::onCreateFailure);
    }

    private void onTypesSuccess(WorkflowTypesResponse workflowTypesResponse) {
        for (WorkflowType type : workflowTypesResponse.getList()) {
            Log.d("test", "onTypesSuccess: " + type.getName());
        }
        mWorkflowsLiveData.setValue(workflowTypesResponse);
    }

    private void onListSuccess(ListsResponse listsResponse) {
        mListLiveData.setValue(listsResponse);
    }

    private void onProductsSuccess(ProductsResponse productsResponse) {
        mProductLiveData.setValue(productsResponse);
    }

    private void onServicesSuccess(ServicesResponse servicesResponse) {
        mServiceLiveData.setValue(servicesResponse);
    }

    private void onUsersSuccess(WorkflowUserResponse workflowUserResponse) {
        mUserLiveData.setValue(workflowUserResponse);
    }

    private void onCountriesSuccess(CountriesResponse countriesResponse) {
        mCountriesLiveData.setValue(countriesResponse);
    }

    private void onCreateSuccess(CreateWorkflowResponse createWorkflowResponse) {
        //mCreateLiveData.setValue(createWorkflowResponse);
        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.created;
        dialogMessage.message = R.string.workflow_created;
        dialogMessage.messageAggregate = " " + createWorkflowResponse.getWorkflow()
                .getWorkflowTypeKey();
        showDialogMessage.setValue(dialogMessage);
        goBack.setValue(true);
    }

    private void onEditSuccess(CreateWorkflowResponse createWorkflowResponse) {
        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.edited;
        dialogMessage.message = R.string.workflow_edit;
        dialogMessage.messageAggregate = " " + createWorkflowResponse.getWorkflow()
                .getWorkflowTypeKey();
        showDialogMessage.setValue(dialogMessage);
        goBack.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: " + throwable.getMessage());
        showLoading.setValue(false);
    }

    private void onCreateFailure(Throwable throwable) {
        showLoading.setValue(false);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.title = R.string.error;
        dialogMessage.message = R.string.error_create_workflow;
        showDialogMessage.setValue(dialogMessage);
        Log.d(TAG, "onFailure: " + throwable.getMessage());
    }

    protected LiveData<WorkflowTypesResponse> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    public LiveData<ListsResponse> getObservableList() {
        if (mListLiveData == null) {
            mListLiveData = new MutableLiveData<>();
        }
        return mListLiveData;
    }

    public LiveData<ProductsResponse> getObservableProduct() {
        if (mProductLiveData == null) {
            mProductLiveData = new MutableLiveData<>();
        }
        return mProductLiveData;
    }

    public LiveData<ServicesResponse> getObservableService() {
        if (mServiceLiveData == null) {
            mServiceLiveData = new MutableLiveData<>();
        }
        return mServiceLiveData;
    }

    public LiveData<WorkflowUserResponse> getObservableWorkflowUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    public LiveData<CountriesResponse> getObservableCountries() {
        if (mCountriesLiveData == null) {
            mCountriesLiveData = new MutableLiveData<>();
        }
        return mCountriesLiveData;
    }

    public LiveData<CreateWorkflowResponse> getObservableCreate() {
        if (mCreateLiveData == null) {
            mCreateLiveData = new MutableLiveData<>();
        }
        return mCreateLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Integer> getObservableCreateError() {
        if (mCreateErrorLiveData == null) {
            mCreateErrorLiveData = new MutableLiveData<>();
        }
        return mCreateErrorLiveData;
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

    protected LiveData<BaseFormItem> getObservableValidationUi() {
        if (mValidationUiLiveData == null) {
            mValidationUiLiveData = new MutableLiveData<>();
        }
        return mValidationUiLiveData;
    }
}
