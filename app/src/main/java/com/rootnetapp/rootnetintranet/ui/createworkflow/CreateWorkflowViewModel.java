package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.Workflow;
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
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

    protected void postWorkflow(FormBuilder formBuilder) {
        ArrayList<FieldData> fieldItems = formSettings.getFieldItems();
        ArrayList<BaseFormElement> formElements = new ArrayList<>();
        FieldData fieldData;
        BaseFormElement baseFormElement;
        for (int i = 0; i < fieldItems.size(); i++) {
            fieldData = fieldItems.get(i);
            baseFormElement = formBuilder.getFormElement(fieldData.tag);

            formElements.add(baseFormElement);
        }

        int workflowTypeId = formSettings.getWorkflowTypeIdSelected();
        String title = "app test";
        String start = "";

        List<WorkflowMetas> metas = new ArrayList<>();
        WorkflowMetas workflowMetas;
        String value;
        for (int i = 0; i < formElements.size(); i++) {
            baseFormElement = formElements.get(i);
            workflowMetas = new WorkflowMetas();
            workflowMetas.setWorkflowTypeFieldId(baseFormElement.getTag());
            value = baseFormElement.getValue();
            workflowMetas.setValue(value);
            metas.add(workflowMetas);
        }

        String test = "Sep 21, 2018";

        Log.d(TAG, "postWorkflow: ");
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
                handleList(field);
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
            case FormSettings.TYPE_LINK:
                handleBuildText(field);
                break;
            case FormSettings.TYPE_LIST:
                handleList(field);
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

    private void handleBuildPhone(FormFieldsByWorkflowType field) {
        FieldData fieldData = new FieldData();
        fieldData.label = field.getFieldName();
        fieldData.required = field.isRequired();
        fieldData.tag = field.getId();
        formSettings.addFieldDataItem(fieldData);
        setFieldPhoneWithData.postValue(fieldData);
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
                    showListField(listField);
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

                    showListField(listField);
                }, throwable -> {
                    Log.d(TAG, "handleBuildService: can't get service: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    private void showListField(ListField listField) {
        FieldData fieldData = new FieldData();
        fieldData.label = listField.customLabel;
        fieldData.list = listField.children;
        fieldData.isMultipleSelection = listField.isMultipleSelection;
        fieldData.tag = listField.customFieldId;
        setListWithData.setValue(fieldData);
        buildForm.setValue(true);
        showLoading.setValue(false);
        formSettings.addFieldDataItem(fieldData);
    }

    private void handleList(FormFieldsByWorkflowType field) {
        FieldConfig fieldConfig = field.getFieldConfigObject();
        String valueType = fieldConfig.getTypeInfo().getValueType();
        if (!valueType.equals(FormSettings.VALUE_LIST)) {
            return;
        }

        boolean isMultipleSelection = fieldConfig.getMultiple();

        ListInfo listInfo = fieldConfig.getListInfo();
        if (listInfo == null) {
            if (fieldConfig.getTypeInfo().getType().equals(FormSettings.TYPE_SYSTEM_USERS)) {
                setTeamList(field);
            }
            return;
        }

        int listId = fieldConfig.getListInfo().getId();
        int customFieldId = field.getId();
        String customLabel = field.getFieldName();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

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
                    showListField(listField);
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.e(TAG, "handleList: problem getting list " + throwable.getMessage());
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
            for (int i = 0; i < stringListItems.size(); i++) {
                name = stringListItems.get(i);
                ListFieldItemMeta itemMeta = new ListFieldItemMeta(
                        customFieldId,
                        name
                );
                listData.add(itemMeta);
            }
            FieldData fieldData = new FieldData();
            fieldData.label = customLabel;
            fieldData.list =  listData;
            fieldData.tag = field.getId();
            fieldData.isMultipleSelection = isMultipleSelection;
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
        mCreateLiveData.setValue(createWorkflowResponse);
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }
    private void onCreateFailure(Throwable throwable) {
        mCreateErrorLiveData.setValue(R.string.failure_connect);
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

}
