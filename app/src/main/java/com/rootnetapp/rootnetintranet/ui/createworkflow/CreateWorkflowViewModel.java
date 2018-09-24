package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.CreateRequest;
import com.rootnetapp.rootnetintranet.models.createworkflow.CurrencyFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.model.BaseFormElement;

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
    private List<WorkflowTypeItemMenu> workflowTypeMenuItems;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected MutableLiveData<List<String>> setTypeList;
    protected MutableLiveData<Boolean> buildForm;
    protected MutableLiveData<int[]> setTextField;
    protected MutableLiveData<int[]> setTextFieldMultiLine;
    protected MutableLiveData<int[]> setDatePicker;
    protected MutableLiveData<Integer> setFormHeader;
    protected MutableLiveData<FieldListSettings> setFieldList;
    protected MutableLiveData<FieldData> setFieldTextWithData;
    protected MutableLiveData<FieldData> setFieldNumericWithData;
    protected MutableLiveData<FieldData> setFieldAreaWithData;
    protected MutableLiveData<FieldData> setFieldDateWithData;
    protected MutableLiveData<FieldData> setFieldSwitchWithData;
    protected MutableLiveData<FieldData> setFieldEmailWithData;
    protected MutableLiveData<FieldData> setFieldPhoneWithData;
    protected MutableLiveData<FieldData> setListWithData;
    protected MutableLiveData<Boolean> refreshForm;
    protected MutableLiveData<Boolean> clearFormFields;
    protected MutableLiveData<DialogMessage> showDialogMessage;
    protected MutableLiveData<Boolean> goBack;

    private CreateWorkflowRepository createWorkflowRepository;

    private static final String TAG = "CreateViewModel";

    private FormSettings formSettings;

    private String token;

    protected static final int REQUIRED = 10;
    protected static final int NOT_REQUIRED = 11;

    protected static final int INDEX_RES_STRING = 0;
    protected static final int INDEX_REQUIRED = 1;

    protected static final int TAG_WORKFLOW_TYPE = 80;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
        setFieldTextWithData = new MutableLiveData<>();
        setTypeList = new MutableLiveData<>();
        buildForm = new MutableLiveData<>();
        setTextField = new MutableLiveData<>();
        setTextFieldMultiLine = new MutableLiveData<>();
        setDatePicker = new MutableLiveData<>();
        setFormHeader = new MutableLiveData<>();
        setFieldList = new MutableLiveData<>();
        clearFormFields = new MutableLiveData<>();
        setFieldNumericWithData = new MutableLiveData<>();
        setFieldAreaWithData = new MutableLiveData<>();
        setFieldDateWithData = new MutableLiveData<>();
        refreshForm = new MutableLiveData<>();
        setFieldSwitchWithData = new MutableLiveData<>();
        setFieldEmailWithData = new MutableLiveData<>();
        setFieldPhoneWithData = new MutableLiveData<>();
        setListWithData = new MutableLiveData<>();
        goBack = new MutableLiveData<>();
    }

    public void initForm(String token) {
        showLoading.setValue(true);
        if (formSettings == null) {
            formSettings = new FormSettings();
        }
        this.token = token;
        setWorkflowTypes();
    }

    public void createWorkflow() {
        ArrayList<FieldData> data = formSettings.getFieldItems();
        Log.d(TAG, "createWorkflow: here");
    }

    protected void onCleared() {
        disposables.clear();
    }
    
    private void handleInvalidEmail() {
        DialogMessage dialog = new DialogMessage();
        dialog.title = R.string.warning;
        dialog.message = R.string.form_invalid_email;
        showDialogMessage.setValue(dialog);
    }

    private WorkflowMetas createMetaData(FieldData fieldData, BaseFormElement baseFormElement) {
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
        int workflowTypeFieldId = baseFormElement.getTag();
        String value = baseFormElement.getValue();
        workflowMeta.setUnformattedValue(value);
        workflowMeta.setWorkflowTypeFieldId(workflowTypeFieldId);
        formSettings.formatMetaData(workflowMeta, fieldData);
        return workflowMeta;
    }

    private String formatUiDateToPostDate(String uiDateFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
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
        return metaDateString;
    }

    protected void postWorkflow(FormBuilder formBuilder) {
        ArrayList<FieldData> fieldItems = formSettings.getFieldItemsForPost();
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
        
        int titleTag = baseInfo.get(FormSettings.MACHINE_NAME_TITLE);
        int descriptionTag = baseInfo.get(FormSettings.MACHINE_NAME_DESCRIPTION);
        int startTag = baseInfo.get(FormSettings.MACHINE_NAME_START_DATE);

        int workflowTypeId = formSettings.getWorkflowTypeIdSelected();
        String title = formBuilder.getFormElement(titleTag).getValue();
        String description = formBuilder.getFormElement(descriptionTag).getValue();
        String start = formatUiDateToPostDate(formBuilder.getFormElement(startTag).getValue());

        ArrayList<BaseFormElement> formElements = new ArrayList<>();
        FieldData fieldData;
        BaseFormElement baseFormElement;
        List<WorkflowMetas> metas = new ArrayList<>();
        WorkflowMetas workflowMetas;
        String value;


        for (int i = 0; i < fieldItems.size(); i++) {
            fieldData = fieldItems.get(i);
            baseFormElement = formBuilder.getFormElement(fieldData.tag);

            int fieldId = fieldData.tag;
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
            }

            workflowMetas = createMetaData(fieldData, baseFormElement);
            if (workflowMetas == null) {
                showLoading.setValue(false);
                return;
            }
            metas.add(workflowMetas);
        }

        // remove empty fields
        ArrayList<Integer> removeIndex = new ArrayList<>();
        WorkflowMetas testMeta;
        for (int i = 0; i < metas.size(); i++) {
            testMeta = metas.get(i);
            if (TextUtils.isEmpty(testMeta.getValue()) || testMeta.getValue().equals("0") || testMeta.getValue().equals("[]")) {
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
        postToServer(metas, workflowTypeId, title, start, description);
    }

    private boolean hasValidFields(int fieldId, String value) {
        TypeInfo typeInfo = formSettings.findFieldDataById(fieldId);
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
            if(!TextUtils.isEmpty(value)
                    && !isValidEmail(value)) {
                handleInvalidEmail();
                return false;
            }
        }

        return true;
    }

    private void postToServer(List<WorkflowMetas> metas, int workflowTypeId, String title, String start, String description) {
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
        Disposable disposable = createWorkflowRepository
                .createWorkflow(token, createRequest)
                .subscribe(this::onCreateSuccess, this::onCreateFailure);

        disposables.add(disposable);
    }

    protected void generateFieldsByType(String typeName) {
        int id = formSettings.findIdByTypeName(typeName);
        if (id == 0) {
            showLoading.postValue(false);
            return;
        }
        showLoading.postValue(true);
        clearFormFields.setValue(true);
        formSettings.clearFormFieldData();
        formSettings.setWorkflowTypeIdSelected(id);
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = createWorkflowRepository.getFiedsByWorkflowType(id);
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
                .subscribe( formSettings -> {
                    FormSettings settings = (FormSettings) formSettings;
                    showFields(settings);
                }, throwable -> {
                    showLoading.postValue(false);
                });
        disposables.add(disposable);

    }

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
        buildForm.setValue(true);
    }

    private void buildField(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        switch (typeInfo.getType()) {
            case FormSettings.TYPE_TEXT:
                handleBuildText(field);
                break;
            case FormSettings.TYPE_DATE:
                handleBuildDate(field);
                break;
            case FormSettings.TYPE_SYSTEM_USERS:
                handleList(field, FormSettings.TYPE_SYSTEM_USERS);
                break;
            case FormSettings.TYPE_TEXT_AREA:
                handleBuildTextArea(field);
                break;
            case FormSettings.TYPE_CHECKBOX:
                handleCheckBox(field);
                break;
            case FormSettings.TYPE_PROJECT:
                handleProject(field);
                break;
            case FormSettings.TYPE_ROLE:
                handeBuildRoles(field);
                break;
            case FormSettings.TYPE_BIRTH_DATE:
                handleBuildDate(field);
                break;
            case FormSettings.TYPE_PHONE:
                handleBuildPhone(field);
                break;
            case FormSettings.TYPE_CURRENCY:
                handleCurrencyType(field);
                break;
            case FormSettings.TYPE_LINK:
                handleBuildText(field);
                break;
            case FormSettings.TYPE_LIST:
                handleList(field, FormSettings.TYPE_LIST);
                break;
            case FormSettings.TYPE_PRODUCT:
                handleBuildProduct(field);
                break;
            case FormSettings.TYPE_SERVICE:
                handleBuildService(field);
                break;
            default:
                Log.d(TAG, "buildField: Not a generic type: " + typeInfo.getType() + " value: " + typeInfo.getValueType());
                break;
        }
    }

    public void checkForContent(FormBuilder formBuilder) {
        List<FieldData> list = formSettings.getFieldItems();
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

        showDialogMessage.setValue(dialogMessage);

    }

    private final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void handleCurrencyType(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        Disposable disposable = createWorkflowRepository
                .getCurrencyCodes()
                .subscribe( currencyFieldData -> {
                    //list
                    ListField listField = new ListField();
                    listField.customFieldId = FormSettings.FIELD_CURRENCY_ID;
                    listField.listType = FormSettings.TYPE_CURRENCY;
                    listField.resStringId = R.string.currency_type;
                    ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
                    CurrencyFieldData currencyData;
                    String currencyLabel;
                    for (int i = 0; i < currencyFieldData.size(); i++) {
                        currencyData = currencyFieldData.get(i);
                        currencyLabel = currencyData.description + " - "+ currencyData.currency;
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
                    formSettings.addFieldDataItem(currencyNumberFieldData);
                    setFieldPhoneWithData.setValue(currencyNumberFieldData);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    private void handleBuildPhone(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        Disposable disposable = createWorkflowRepository
                .getCountryCodes()
                .subscribe( phoneFieldData -> {

                    //list
                    ListField listField = new ListField();
                    listField.customFieldId = FormSettings.FIELD_CODE_ID;
                    listField.listType = FormSettings.TYPE_PHONE;
                    listField.resStringId = R.string.country_code;
                    ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
                    PhoneFieldData phoneCode;
                    String codeLabel;
                    for (int i = 0; i < phoneFieldData.size(); i++) {
                        phoneCode = phoneFieldData.get(i);
                        codeLabel = phoneCode.phoneCode + " - "+ phoneCode.description;
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
                    formSettings.addFieldDataItem(phoneNumberFieldData);
                    setFieldPhoneWithData.setValue(phoneNumberFieldData);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
                });

        disposables.add(disposable);
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
        if (value.equals(FormSettings.VALUE_LIST) && type.equals(FormSettings.TYPE_SYSTEM_USERS)) {
            return true;
        }
        return false;
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

    private void handleBuildText(FormFieldsByWorkflowType field) {
        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        FieldData fieldData = new FieldData();
        fieldData.label = field.getFieldName();
        fieldData.required = field.isRequired();
        fieldData.tag = field.getId();
        fieldData.escape = escape(field.fieldConfigObject);
        if (FormSettings.VALUE_STRING.equals(valueType) || FormSettings.VALUE_TEXT.equals(valueType)) {
            formSettings.addFieldDataItem(fieldData);
            setFieldTextWithData.setValue(fieldData);
            return;
        }

        switch (valueType) {
            case FormSettings.VALUE_INTEGER:
                setFieldNumericWithData.setValue(fieldData);
                break;
            case FormSettings.VALUE_EMAIL:
                setFieldEmailWithData.setValue(fieldData);
                break;
        }
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleBuildTextArea(FormFieldsByWorkflowType field) {
        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        FieldData fieldData = new FieldData();
        fieldData.label = field.getFieldName();
        fieldData.required = field.isRequired();
        fieldData.tag = field.getId();
        fieldData.escape = escape(field.fieldConfigObject);
        switch (valueType) {
            case FormSettings.VALUE_STRING:
                setFieldAreaWithData.setValue(fieldData);
                break;
            default:
                Log.d(TAG, "handleBuildTextArea: Value not recognized " + valueType);
                break;
        }
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleBuildDate(FormFieldsByWorkflowType field) {
        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        FieldData fieldData = new FieldData();
        fieldData.label = field.getFieldName();
        fieldData.required = field.isRequired();
        fieldData.tag = field.getId();
        fieldData.escape = escape(field.fieldConfigObject);
        switch (valueType) {
            case FormSettings.VALUE_DATE:
                setFieldDateWithData.setValue(fieldData);
                break;
            default:
                Log.d(TAG, "handleBuildDate: Value not recognized " + valueType);
                break;
        }
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleCheckBox(FormFieldsByWorkflowType field) {
        String statusMachineName = field.getFieldConfigObject().getMachineName();
        if (statusMachineName != null && statusMachineName.equals("wf_status")) {
            return;
        }

        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        FieldData fieldData = new FieldData();
        fieldData.label = field.getFieldName();
        fieldData.required = field.isRequired();
        fieldData.tag = field.getId();
        fieldData.escape = escape(field.fieldConfigObject);
        switch (valueType) {
            case FormSettings.VALUE_BOOLEAN:
                setFieldSwitchWithData.setValue(fieldData);
                break;
            default:
                Log.d(TAG, "handleCheckBox: Value not recognized: " + valueType);
                break;
        }
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleBuildProduct(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        String customLabel = field.getFieldName();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();
    }

    private void handleProject(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        boolean isMultipleSelection = fieldConfig.getMultiple();
        String customLabel = field.getFieldName();
        int customFieldId = field.getId();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        // TODO endpoint at this point returns an empty array.
//        Disposable disposable = createWorkflowRepository
//                .getProjects(token)
//                .subscribe( projectResponse -> {
//
//                    if (roleResponse.getCode() != 200) {
//                        showLoading.setValue(false);
//                        return;
//                    }
//                    List<Role> list = roleResponse.getList();
//                    ListField listField = formSettings.addRolesLisToForm(
//                            list,
//                            customLabel,
//                            customFieldId,
//                            FormSettings.TYPE_ROLE
//                    );
//                    listField.isMultipleSelection = isMultipleSelection;
//                    listField.associatedWorkflowTypeId = associatedWorkflowTypeId;
//
//                    if (listField.children.size() < 1) {
//                        return;
//                    }
//                    showListField(listField);
//                }, throwable -> {
//                    showLoading.setValue(false);
//                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
//                });
//
//        disposables.add(disposable);
    }

    private void handeBuildRoles(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        boolean isMultipleSelection = fieldConfig.getMultiple();
        String customLabel = field.getFieldName();
        int customFieldId = field.getId();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        Disposable disposable = createWorkflowRepository
                .getRoles(token)
                .subscribe( roleResponse -> {
                    if (roleResponse.getCode() != 200) {
                        showLoading.setValue(false);
                        return;
                    }
                    List<Role> list = roleResponse.getList();
                    ListField listField = formSettings.addRolesLisToForm(
                            list,
                            customLabel,
                            customFieldId,
                            FormSettings.TYPE_ROLE
                    );
                    listField.isMultipleSelection = isMultipleSelection;
                    listField.associatedWorkflowTypeId = associatedWorkflowTypeId;

                    if (listField.children.size() < 1) {
                        return;
                    }
                    showListField(listField, fieldConfig);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "handeBuildRoles: " + throwable.getMessage());
                });

        disposables.add(disposable);
    }

    private void handleBuildService(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        boolean isMultipleSelection = fieldConfig.getMultiple();
        String customLabel = field.getFieldName();
        int customFieldId = field.getId();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        Disposable disposable = createWorkflowRepository
                .getServices(token)
                .subscribe( servicesResponse -> {
                    if (servicesResponse.getCode() != 200) {
                        showLoading.setValue(false);
                        return;
                    }
                    List<Service> list = servicesResponse.getList();
                    ListField listField = formSettings.addServiceListToForm(
                            list,
                            customLabel,
                            customFieldId,
                            FormSettings.TYPE_SERVICE
                    );
                    listField.isMultipleSelection = isMultipleSelection;
                    listField.associatedWorkflowTypeId = associatedWorkflowTypeId;

                    if (listField.children.size() < 1) {
                        return;
                    }

                    showListField(listField, fieldConfig);
                }, throwable -> {
                    Log.d(TAG, "handleBuildService: can't get service: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    private void showListField(ListField listField, FieldConfig fieldConfig) {
        FieldData fieldData = new FieldData();
        fieldData.label = listField.customLabel;
        fieldData.list = listField.children;
        fieldData.resLabel = listField.resStringId;
        fieldData.isMultipleSelection = listField.isMultipleSelection;
        fieldData.tag = listField.customFieldId;
        fieldData.escape = escape(fieldConfig);
        setListWithData.setValue(fieldData);
        buildForm.setValue(true);
        showLoading.setValue(false);
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleList(FormFieldsByWorkflowType field, String fieldType) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
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
            createSystemUserFieldasCustomField(field, listId, customFieldId, customLabel, associatedWorkflowTypeId);
            return;
        }

        Disposable disposable = createWorkflowRepository
                .getList(token, listId)
                .subscribe( listsResponse -> {
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

        disposables.add(disposable);
    }

    private void createSystemUserFieldasCustomField(
            FormFieldsByWorkflowType field,
            int listId,
            int customFieldId,
            String customLabel,
            int associatedWorkflowTypeId) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> profiles = formSettings.getProfiles();
            if (profiles == null || profiles.size() < 1) {
                return false;
            }
            ListField listField = new ListField();
            listField.listId = listId;
            listField.customFieldId = customFieldId;
            listField.customLabel = customLabel;
            listField.associatedWorkflowTypeId = associatedWorkflowTypeId;
            listField.isMultipleSelection = false;
            listField.listType = FormSettings.TYPE_SYSTEM_USERS;

            ArrayList<ListFieldItemMeta> tempList = new ArrayList<>();
            for (int i = 0; i < profiles.size(); i++) {
                FormCreateProfile profile = profiles.get(i);
                ListFieldItemMeta item = new ListFieldItemMeta(
                        profile.getId(),
                        profile.getUsername(),
                        customFieldId
                );
                tempList.add(item);
            }

            listField.children = tempList;

            return listField;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( listField -> {
                    ListField fieldData = (ListField) listField;
                    showListField(fieldData, field.getFieldConfigObject());

                    showLoading.setValue(false);
                    buildForm.setValue(true);
                }, throwable -> {
                    showLoading.setValue(false);
                    buildForm.setValue(true);
                });
        disposables.add(disposable);
    }


    private void setWorkflowTypes() {
        Disposable disposable = Observable.fromCallable(() -> {
            List<WorkflowTypeItemMenu> types = createWorkflowRepository.getWorklowTypeNames();
            if (types == null || types.size() < 1) {
                return false;
            }
            String name;
            Integer id;
            for (int i = 0; i < types.size(); i++) {
                name = types.get(i).getName();
                id = types.get(i).getId();
                formSettings.setId(id);
                formSettings.setName(name);
            }
            FieldListSettings fieldListSettings = new FieldListSettings();
            fieldListSettings.items = formSettings.getNames();
            fieldListSettings.labelRes = R.string.type;
            fieldListSettings.required = true;
            fieldListSettings.tag = TAG_WORKFLOW_TYPE;
            saveTypeToFormSettings(fieldListSettings);
            return fieldListSettings;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( fieldListSettings -> {
                    FieldListSettings data = (FieldListSettings) fieldListSettings;
                    setFieldList.setValue(data);
                    //setBaseFields();
                    buildForm.setValue(true);
                    showLoading.setValue(false);
                }, throwable -> {
                    Log.d(TAG, "setWorkflowTypes: error " + throwable.getMessage());
                    showLoading.postValue(false);
                });
        disposables.add(disposable);
    }

    private void saveTypeToFormSettings(FieldListSettings fieldListSettings) {
        int customLabel = fieldListSettings.labelRes;

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
        fieldData.list =  listData;
        fieldData.tag = TAG_WORKFLOW_TYPE;
        fieldData.isMultipleSelection = false;
        fieldData.escape = false; // workflow types are not send in meta data.
        formSettings.addFieldDataItem(fieldData);
    }

    private void setBaseFields() {
        int[] settingsDataTitle = new int[2];
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
        //setTeamList();
    }

    private void setTeamList(FormFieldsByWorkflowType field) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> profiles = createWorkflowRepository.getProfiles();
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
            fieldData.list =  listData;
            fieldData.tag = field.getId();
            fieldData.isMultipleSelection = isMultipleSelection;
            fieldData.escape = escape(fieldConfig);
            formSettings.addFieldDataItem(fieldData);
            //setFieldList.postValue(fieldListSettings);
            return fieldListSettings;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( fieldListSettings -> {
                    FieldListSettings settings = (FieldListSettings) fieldListSettings;
                    setFieldList.setValue(settings);
                    showLoading.setValue(false);
                    buildForm.setValue(true);
                }, throwable -> {
                    showLoading.setValue(false);
                    buildForm.setValue(true);
                });
        disposables.add(disposable);
    }

    public void getWorkflowTypes(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        Log.d("test", "getWorkflowTypes: ");
        createWorkflowRepository.getWorkflowTypes(auth).subscribe(this::onTypesSuccess, this::onFailure);
    }

    public void getList(String auth, int id) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getList(auth, id).subscribe(this::onListSuccess, this::onFailure);
    }

    public void getProducts(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getProducts(auth).subscribe(this::onProductsSuccess, this::onFailure);
    }

    public void getServices(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getServices(auth).subscribe(this::onServicesSuccess, this::onFailure);
    }

    public void getUsers(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getUsers(auth).subscribe(this::onUsersSuccess, this::onFailure);
    }

    public void getCountries(String auth) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.getCountries(auth).subscribe(this::onCountriesSuccess, this::onFailure);
    }

    public void createWorkflow(String auth, int workflowTypeId, String title, String workflowMetas,
                               String start, String description) {
        //todo auth2 SOLO TESTING mientras no esta el backend live
        createWorkflowRepository.createWorkflow(auth, workflowTypeId, title, workflowMetas,
                start, description).subscribe(this::onCreateSuccess, this::onCreateFailure);
    }

    private void onTypesSuccess(WorkflowTypesResponse workflowTypesResponse) {
        for (WorkflowType type : workflowTypesResponse.getList()) {
            Log.d("test", "onTypesSuccess: "+type.getName());
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

}
