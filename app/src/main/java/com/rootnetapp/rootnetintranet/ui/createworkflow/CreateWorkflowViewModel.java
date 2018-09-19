package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private CreateWorkflowRepository createWorkflowRepository;

    private static final String TAG = "CreateViewModel";

    private ArrayList<Integer> formFieldList;

    private FormSettings formSettings;

    private int[] fieldSettings;

    protected static final int REQUIRED = 10;
    protected static final int NOT_REQUIRED = 11;

    protected static final int INDEX_RES_STRING = 0;
    protected static final int INDEX_REQUIRED = 1;

    protected static final int TAG_WORKFLOW_TYPE = 80;

    //todo REMOVE, solo testing
    //private String auth2 = "Bearer " + Utils.testToken;

    public CreateWorkflowViewModel(CreateWorkflowRepository createWorkflowRepository) {
        this.createWorkflowRepository = createWorkflowRepository;
        setTypeList = new MutableLiveData<>();
        buildForm = new MutableLiveData<>();
        setTextField = new MutableLiveData<>();
        setTextFieldMultiLine = new MutableLiveData<>();
        setDatePicker = new MutableLiveData<>();
        setFormHeader = new MutableLiveData<>();
        setFieldList = new MutableLiveData<>();
        setFieldTextWithData = new MutableLiveData<>();
    }

    protected void onCleared() {
        disposables.clear();
    }

    protected void generateFieldsByType(String typeName) {
        showLoading.postValue(true);
        int id = formSettings.findIdByTypeName(typeName);
        if (id == 0) {
            showLoading.postValue(false);
            return;
        }

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
                    showLoading.setValue(false);
                    FormSettings settings = (FormSettings) formSettings;
                    showFields(settings);
                }, throwable -> {
                    showLoading.postValue(false);
                });
        disposables.add(disposable);

    }

    private void showFields(FormSettings formSettings) {
        Log.d(TAG, "showFields: ");
        List<FormFieldsByWorkflowType> fields = formSettings.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FormFieldsByWorkflowType field = fields.get(i);
            if (!field.isRequired()) {
                continue;
            }
            FieldConfig fieldConfig = field.getFieldConfigObject();
            if (fieldConfig.isPrecalculated()) {
                continue;
            }
            buildField(field);
        }
        buildForm.postValue(true);
    }

    private void buildField(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        switch (typeInfo.getType()) {
            case FormSettings.TYPE_TEXT:
                handleBuildText(field);
                break;
            case FormSettings.TYPE_DATE:
                break;
            case FormSettings.TYPE_SYSTEM_USERS:
                break;
            case FormSettings.TYPE_TEXT_AREA:
                break;
            case FormSettings.TYPE_CHECKBOX:
                break;
            default:
                Log.d(TAG, "buildField: Type not recognized: " + typeInfo.getType() + " value: " + typeInfo.getValueType());
                break;
        }
    }

    private void handleBuildText(FormFieldsByWorkflowType field) {
        String valueType = field.getFieldConfigObject().getTypeInfo().getValueType();
        switch (valueType) {
            case FormSettings.VALUE_STRING:
                // text field single line
                FieldData fieldData = new FieldData();
                fieldData.label = field.getFieldName();
                fieldData.required = field.isRequired();
                setFieldTextWithData.postValue(fieldData);
                break;
            case FormSettings.VALUE_INTEGER:
                
                break;
            case FormSettings.VALUE_EMAIL:
                break;
        }
    }

    public void initForm(LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        if (formSettings == null) {
            formSettings = new FormSettings();
        }
        subscribe(lifecycleOwner);
        setWorkflowTypes();
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
            setFieldList.postValue(fieldListSettings);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( success -> {
                    setBaseFields();
                }, throwable -> {
                    showLoading.postValue(false);
                });
        disposables.add(disposable);
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
        setTeamList();
    }

    private void setTeamList() {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> profiles = createWorkflowRepository.getProfiles();
            if (profiles == null || profiles.size() < 1) {
                return false;
            }
            for (int i = 0; i < profiles.size(); i++) {
                formSettings.setProfile(profiles.get(i));
            }
            formSettings.getProfileNames();
            FieldListSettings fieldListSettings = new FieldListSettings();
            fieldListSettings.items = formSettings.getProfileNames();
            fieldListSettings.labelRes = R.string.owner;
            fieldListSettings.required = true;
            setFieldList.postValue(fieldListSettings);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( success -> {
                    showLoading.setValue(false);
                    buildForm.postValue(true);
                }, throwable -> {
                    showLoading.postValue(false);
                    buildForm.postValue(true);
                });
        disposables.add(disposable);
    }




    private void subscribe(LifecycleOwner lifecycleOwner) {

        //createWorkflowRepository.getWorkflowTypeMenuItems().observe(lifecycleOwner, getWorkflowTypes);
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
