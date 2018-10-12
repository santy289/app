package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FieldData;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FieldListSettings;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FormSettings;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowTypeSpinnerAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowRepository;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import okio.Utf8;

public class WorkflowViewModel extends ViewModel {
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<PagedList<WorkflowListItem>> updateWithSortedList;
    private MutableLiveData<int[]> toggleRadioButton;
    private MutableLiveData<int[]> toggleSwitch;
    private MutableLiveData<int[]> toggleFilterSwitch;
    private MutableLiveData<Integer> setSelectType;
    private MutableLiveData<Boolean> showList;
    private MutableLiveData<Boolean> addWorkflowObserver;
    private MutableLiveData<Boolean> setAllCheckboxesList;
    public MutableLiveData<Boolean> showBottomSheetLoading;
    protected MutableLiveData<Boolean> clearFilters;
    private LiveData<PagedList<WorkflowListItem>> liveWorkflows, liveUnordered;


    //
    private MutableLiveData<List<WorkflowTypeMenu>> workflowTypeMenuItems;
    private List<WorkflowTypeItemMenu> workflowTypeForMenu;


    // TODO en development

    protected MutableLiveData<List<WorkflowTypeMenu>> rightDrawerFilterMenus;
    protected MutableLiveData<OptionsList> rightDrawerOptionMenus;
    protected MutableLiveData<Boolean> invalidateDrawerOptionsList;

    // Sort By
    protected MutableLiveData<int[]> messageMainToggleRadioButton;
    protected MutableLiveData<int[]> messageMainToggleSwitch;
//    protected MutableLiveData<int[]>

    // MOVED TO FILTERSETTINGS
    //protected List<WorkflowTypeMenu> rightDrawerFilters;

    // TODO en development
//

    private WorkflowRepository workflowRepository;
    private List<WorkflowDb> workflows, unordered;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private Sort sort;
    private FilterSettings filterSettings;
    private String token;
    private String userId;
    private int categoryId;
    private List<ListItem> categoryList;
    private static final String TAG = "WorkflowViewModel";

    public static final int NO_TYPE_SELECTED = 0;

    public static final int WORKFLOW_TYPE_FIELD = -98;

    public WorkflowViewModel(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
        sort = new Sort();
        getWorkflowTypesFromDb();
        filterSettings = new FilterSettings();
        showBottomSheetLoading = new MutableLiveData<>();
        clearFilters = new MutableLiveData<>();
        workflowTypeMenuItems = new MutableLiveData<>();
        rightDrawerFilterMenus = new MutableLiveData<>();
        rightDrawerOptionMenus = new MutableLiveData<>();
        invalidateDrawerOptionsList = new MutableLiveData<>();
        formSettings = new FormSettings();
        messageMainToggleRadioButton = new MutableLiveData<>();
        messageMainToggleSwitch = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        workflowRepository.clearDisposables();
    }

    private void getWorkflowTypesFromDb() {
        Disposable disposable = Observable.fromCallable(() -> {
            workflowTypeForMenu = this.workflowRepository.getWorkflowTypesForMenu();
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                }, throwable -> {
                    Log.d(TAG, "failure: Can't access to DB: " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    // First to be called
    private void setWorkflowListNoFilters(String token) {
        workflowRepository.setWorkflowList(token);
        liveWorkflows = workflowRepository.getAllWorkflows();
    }

    protected void swipeToRefresh(LifecycleOwner lifecycleOwner) {
        int id = filterSettings.getWorkflowTypeId();
        if (id > 0) {
            workflowRepository.getWorkflowsByType(token, id);
        } else {
            workflowRepository.getAllWorkflowsNoFilters(token);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void insert(WorkflowDb workflow) {
        workflowRepository.insertWorkflow(workflow);
    }

    protected void initWorkflowList(SharedPreferences sharedPreferences, LifecycleOwner lifecycleOwner) {
        token = "Bearer "+ sharedPreferences.getString("token","");
        userId = sharedPreferences.getString(PreferenceKeys.PREFERENCE_PROFILE_ID, "");
        categoryId = sharedPreferences.getInt("category_id", 0);
        setWorkflowListNoFilters(token);
        subscribe(lifecycleOwner);
    }

    protected void iniRightDrawerFilters() {
        getCategories(categoryId);
        initRightDrawerFilterList();
        initSortBy();
    }

    private void initRightDrawerFilterList() {
        WorkflowTypeMenu menuItem = new WorkflowTypeMenu(
                FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID,
                "",
                "",
                RightDrawerFiltersAdapter.TYPE,
                WORKFLOW_TYPE_FIELD
        );
        filterSettings.addFilterListMenu(menuItem);
        rightDrawerFilterMenus.setValue(filterSettings.getFilterDrawerList());
    }

    private void getCategories(int id) {
        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .getCategoryList(token, id)
                .subscribe( listsResponse -> {
                    showLoading.setValue(false);
                    List<ListItem> list = listsResponse.getItems();
                    if (list.size() < 1) {
                        return;
                    }
                    categoryList = list.get(0).getChildren();
                    initWorkflowTypeMenus();
                }, throwable -> {
                    showLoading.setValue(false);
                    Log.d(TAG, "getCategories: Can't get categories for all workflows");
                });
        disposables.add(disposable);
    }

    private void initWorkflowTypeMenus() {
        //init workflow type filter
        ListItem category;
        WorkflowTypeItemMenu typeMenu;

        ArrayMap<String, List<WorkflowTypeMenu>> result = new ArrayMap<>();

        String categoryName;
        List<WorkflowTypeMenu> tempMenus = new ArrayList<>();
        List<WorkflowTypeMenu> noCategory = new ArrayList<>();
        int idCat;
        int catId;
        for (int i = 0; i < categoryList.size(); i++) {
            category = categoryList.get(i);
            categoryName = category.getName();
            for (int j = 0; j < workflowTypeForMenu.size(); j++) {
                typeMenu = workflowTypeForMenu.get(j);
                idCat = typeMenu.getCategory();
                catId = category.getId();
                if (idCat == catId) {
                    String menuLabel = typeMenu.getName();
                    WorkflowTypeMenu menu = new WorkflowTypeMenu(
                            typeMenu.id,
                            menuLabel,
                            WorkflowTypeSpinnerAdapter.TYPE,
                            typeMenu.getId()
                    );
                    tempMenus.add(menu);
                }
            }
            if (tempMenus.isEmpty()) {
                continue;
            }
            result.put(categoryName, tempMenus);
            tempMenus = new ArrayList<>();
        }

        for (int i = 0; i < workflowTypeForMenu.size(); i++) {
            typeMenu = workflowTypeForMenu.get(i);
            idCat = typeMenu.getCategory();
            if (idCat == 0) {
                String menuLabel = typeMenu.getName();
                WorkflowTypeMenu menu = new WorkflowTypeMenu(
                        typeMenu.id,
                        menuLabel,
                        WorkflowTypeSpinnerAdapter.TYPE,
                        typeMenu.getId()
                );
                noCategory.add(menu);
            }
        }


        if (!noCategory.isEmpty()) {
            result.put(WorkflowTypeSpinnerAdapter.NO_CATEGORY_LABEL, noCategory);
        }

        spinnerMenuArray = new ArrayList<>();

        String key;
        WorkflowTypeMenu menu;
        for (int i = 0; i < result.size(); i++) {
            key = result.keyAt(i);
            menu = new WorkflowTypeMenu(
                    0, // TODO we need category id here MAYBE ?
                    key,
                    WorkflowTypeSpinnerAdapter.CATEGORY
            );
            spinnerMenuArray.add(menu);
            List<WorkflowTypeMenu> list = result.get(key);
            for (int j = 0; j < list.size(); j++) {
                menu = list.get(j);
                spinnerMenuArray.add(menu);
            }
        }

        WorkflowTypeMenu noSelection = new WorkflowTypeMenu(
                0,
                "NO SELECTION",
                WorkflowTypeSpinnerAdapter.NO_SELECTION
        );
        spinnerMenuArray.add(0, noSelection);
        filterSettings.saveOptionsListFor(FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID, spinnerMenuArray);
    }

    @Deprecated
    protected void loadWorkflowsByType(int position, LifecycleOwner lifecycleOwner) {
        showLoading.postValue(true);
        WorkflowTypeMenu menu = spinnerMenuArray.get(position);
        int typeId = menu.getWorkflowTypeId();
        if (typeId == NO_TYPE_SELECTED) {
            workflowRepository.getAllWorkflowsNoFilters(token);
        } else {
            workflowRepository.getWorkflowsByType(token, typeId);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void loadWorkflowsByType(WorkflowTypeMenu menu, LifecycleOwner lifecycleOwner) {
//        showLoading.postValue(true);
        int typeId = menu.getWorkflowTypeId();
        if (typeId == NO_TYPE_SELECTED) {
            workflowRepository.getAllWorkflowsNoFilters(token);
        } else {
            workflowRepository.getWorkflowsByType(token, typeId);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void handleSelectedItemInFilters(int position) {
        OptionsList optionsList = filterSettings
                .handleFilterListPositionSelected(position);
        rightDrawerOptionMenus.setValue(optionsList);
    }

    protected void handleRightDrawerBackAction() {
        List<WorkflowTypeMenu> list = filterSettings.getFilterDrawerList();
        if (list == null) {
            return;
        }
        rightDrawerFilterMenus.setValue(list);
    }

    private final int DRAWER_FILTER_LIST_INDEX_TYPE = 0;
    protected void handleOptionSelected(int position, LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        List<WorkflowTypeMenu> menuList = filterSettings.getOptionsListAtSelectedFilterIndex();
        WorkflowTypeMenu menu = menuList.get(position);

        // Selecting workflow type no need to update other items.
        // Clear all fields if we are selecting a new workflow type.
        if (filterSettings.getFilterListIndexSelected() == DRAWER_FILTER_LIST_INDEX_TYPE) {

            filterSettings.clearDynamicFields();

            if (filterSettings.isTypeAlreadySelected(menu.getWorkflowTypeId())) {
                filterSettings.updateWorkflowTypeListFilterItem(null);
                filterSettings.updateRightDrawerOptionListWithSelected(menu, false); //TEST
                updateSelectedMenuItem(menu);
//                filterSettings.updateFilterListItemSelected(menu);
                showLoading.setValue(false);
                return;
            }
            loadWorkflowsByType(menu, lifecycleOwner);

            // clear any selected items if any
            filterSettings.clearworklowTypeSelection();

            // Filter List Update
            filterSettings.updateWorkflowTypeListFilterItem(menu);
            // Update Drawer Options List
            filterSettings.updateRightDrawerOptionListWithSelected(menu, false);
            updateSelectedMenuItem(menu);
//            filterSettings.updateFilterListItemSelected(menu);


            // Allowing single selection on the UI for this list.
            invalidateDrawerOptionsList.setValue(true);

            liveWorkflows.removeObservers(lifecycleOwner);
            applyFilters(filterSettings);
            int workflowTypeId = menu.getWorkflowTypeId();
            findDynamicFieldsBy(workflowTypeId);

            return;
        }

        filterSettings.updateRightDrawerOptionListWithSelected(menu, true);
        updateSelectedMenuItem(menu);
        filterSettings.updateFilterListItemSelected(menu);

        FieldData fieldData = filterSettings.getFieldDataFromSelectedOptionList();
        FieldConfig fieldConfig = filterSettings.getFieldConfigFromDrawerOptionList();
        String values = filterSettings.getAllValuesSelectedInOptionList();

        String metaString = filterSettings.getAllItemIdsSelectedAsString();
        //int[] idValuesArray = filterSettings.arrayOfIdsSelected();

        if (TextUtils.isEmpty(metaString)) {
            loadWorkflowsByType(menu, lifecycleOwner);
            liveWorkflows.removeObservers(lifecycleOwner);
            applyFilters(filterSettings);
        }

//        WorkflowMetas meta = createMetaData(
//                fieldData,
//                values,
//                menu.getWorkflowTypeId(),
//                fieldConfig);


//            String test2 = "{\"254\":163}"; // works
//            String test3 = "{\"254\":[163,164]}";
//            String test3v = "{\"254\":164,\"255\":149}";
//            String test4 = "{\"256\":[148,149]}";

            Disposable disposable = workflowRepository
                    .getWorkflowsByFieldFilters(
                            token,
                            menu.getWorkflowTypeId(),
                            metaString)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseDb -> workflowRepository.workflowDbSuccess(responseDb),
                            throwable -> {
                                Log.d(TAG, "handleOptionSelected: " + throwable.getMessage());
                    });

            disposables.add(disposable);
    }

    private FormSettings formSettings;
    // TODO USE TO CREATE GET REQUEST FOR FILTERING
    private WorkflowMetas createMetaData(FieldData fieldData, String valueSelected, int workflowTypeFieldId, FieldConfig fieldConfig) {
        WorkflowMetas workflowMeta = new WorkflowMetas();
        workflowMeta.setUnformattedValue(valueSelected);
        workflowMeta.setWorkflowTypeFieldId(workflowTypeFieldId);
        formSettings.formatMetaData(workflowMeta, fieldData, fieldConfig);
        return workflowMeta;
    }

    private void updateSelectedMenuItem(WorkflowTypeMenu menu) {
        menu.setSelected(!menu.isSelected());
    }


    // TODO update filter list and option list with this information.
    // TODO save everything in our FitlerSettings.
    // TODO use createMetaData to generate the metadata to send in GET request
    private void findDynamicFieldsBy(int workflowTypeId) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = workflowRepository.getFiedsByWorkflowType(workflowTypeId);
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
                .subscribe( dynamicFieldsSettings -> {
                    Log.d(TAG, "findDynamicFieldsBy: ");
                    FormSettings settings = formSettings;
                    showFields(settings);
                }, throwable -> {
                    showLoading.postValue(false);
                    Log.d(TAG, "findDynamicFieldsBy: Something went wrong getting fields" + throwable.getMessage());
                });
        disposables.add(disposable);
    }


    private void showFields(FormSettings formSettings) {
        List<FormFieldsByWorkflowType> fields = formSettings.getFields();
        for (int i = 0; i < fields.size(); i++) {
            FormFieldsByWorkflowType field = fields.get(i);
            if (!field.isShowForm()) {
                showLoading.setValue(false);
                continue;
            }

            FieldConfig fieldConfig = field.getFieldConfigObject();
            if (fieldConfig.isPrecalculated()) {
                showLoading.setValue(false);
                continue;
            }

            buildField(field);
        }
        showLoading.setValue(false);
    }

    private void buildField(FormFieldsByWorkflowType field) {
        TypeInfo typeInfo = field.getFieldConfigObject().getTypeInfo();
        switch (typeInfo.getType()) {
            case FormSettings.TYPE_SYSTEM_USERS:
                //handleList(field, FormSettings.TYPE_SYSTEM_USERS);
                break;
            case FormSettings.TYPE_CHECKBOX:
                //handleCheckBox(field);
                break;
            case FormSettings.TYPE_PROJECT:
                //handleProject(field);
                break;
            case FormSettings.TYPE_ROLE:
                //handeBuildRoles(field);
                break;
            case FormSettings.TYPE_LIST:
                handleList(field, FormSettings.TYPE_LIST);
                break;
            case FormSettings.TYPE_PRODUCT:
                //handleBuildProduct(field);
                break;
            case FormSettings.TYPE_SERVICE:
                //handleBuildService(field);
                break;
//            case FormSettings.TYPE_BIRTH_DATE:
//                handleBuildDate(field);
//                break;
//            case FormSettings.TYPE_PHONE:
//                handleBuildPhone(field);
//                break;
//            case FormSettings.TYPE_CURRENCY:
//                handleCurrencyType(field);
//                break;
//            case FormSettings.TYPE_LINK:
//                handleBuildText(field);
//                break;
//            case FormSettings.TYPE_TEXT:
//                handleBuildText(field);
//                break;
//            case FormSettings.TYPE_DATE:
//                handleBuildDate(field);
//                break;
//            case FormSettings.TYPE_FILE:
//                handleFile(field);
//                break;
//            case FormSettings.TYPE_TEXT_AREA:
//                handleBuildTextArea(field);
//                break;
            default:
                Log.d(TAG, "buildField: Not a generic type: " + typeInfo.getType() + " value: " + typeInfo.getValueType());
                break;
        }
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
        int customFieldId = field.getFieldId();
        String customLabel = field.getFieldName();
        int associatedWorkflowTypeId = fieldConfig.getAssociatedWorkflowTypedId();

        if (fieldType.equals(FormSettings.TYPE_SYSTEM_USERS)) {
            createSystemUserFieldasCustomField(field, listId, customFieldId, customLabel, associatedWorkflowTypeId);
            return;
        }

        Disposable disposable = workflowRepository
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

                    // TODO update filter list with new fields
//                    buildForm.setValue(true);


                }, throwable -> {
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

        // TODO save customFieldId in order to make the GET request

        // filter field info
        int id = listField.customFieldId; // id to identify which field in the content type we are talking about.
        String label = listField.customLabel;
        boolean isMultiple = listField.isMultipleSelection;

        // option fields info list to display in options
        List<ListFieldItemMeta> children = listField.children;
        int itemId = children.get(0).id; // value to send to get as value selected
        String optionLabel = children.get(0).name;

        //setListWithData.setValue(fieldData);
        // TODO UPDATE OPTIONS LIST WITH FIELD DATA FROM THE INTERNET
        // TODO update FILTERSETTINGS FILTER LIST WITH FIELDS

        filterSettings.updateFilterListWithDynamicField(listField, fieldData, fieldConfig);
        formSettings.addFieldDataItem(fieldData);
    }

    private void setTeamList(FormFieldsByWorkflowType field) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormCreateProfile> profiles = workflowRepository.getProfiles();
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

                    // TODO update FILTERSETTINGS
                    //setFieldList.setValue(settings);
                    showLoading.setValue(false);
                    //buildForm.setValue(true);
                }, throwable -> {
                    showLoading.setValue(false);
                    //buildForm.setValue(true);
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

    // TODO END OF DYNAMIC FILTER IMPLEMENTATION


    protected void loadMyPendingWorkflows(boolean isChecked, LifecycleOwner lifecycleOwner) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        showLoading.postValue(true);
        if (isChecked) {
            filterSettings.setCheckedMyPending(true);
            int id = Integer.valueOf(userId);
            workflowRepository.getMyPendingWorkflows(id, token);

        } else {
            filterSettings.setCheckedMyPending(false);
            workflowRepository.getAllWorkflowsNoFilters(token);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
        // liveWorkflows' Observer that was removed will be put back in one of the observers in subscribe.
    }

    protected void filterBySearchText(String searchText, LifecycleOwner lifecycleOwner) {
        filterSettings.setSearchText(searchText);
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterSettings);
    }

    protected void clearFilters() {
        clearFilters.setValue(true);
    }

    private void subscribe(LifecycleOwner lifecycleOwner) {
        final Observer<Boolean> handleRepoErrorObserver = ( error -> {
            showLoading.postValue(false);
        });
        final Observer<Boolean> handleRepoSuccessObserver = ( success -> {
            showLoading.postValue(false);
            // TODO change data source to new query pointing ONLY to my profile id.
            applyFilters(filterSettings, userId);
        });
        final Observer<Boolean> handleRepoSuccessNoFilterObserver = ( success -> {
           showLoading.postValue(false);
           applyFilters(filterSettings);
        });

        workflowRepository.getObservableHandleRepoError().observe(lifecycleOwner, handleRepoErrorObserver);
        workflowRepository.getObservableHandleRepoSuccess().observe(lifecycleOwner, handleRepoSuccessObserver);
        workflowRepository.getObservableHandleRepoSuccessNoFilter().observe(lifecycleOwner, handleRepoSuccessNoFilterObserver);
    }

    protected LiveData<Boolean> getObservableLoadMore() {
        return workflowRepository.showLoadMore;
    }

    private void updateFilterBoxSettings(int workflowTypeId, int typeIdPositionInArray, boolean isCheckedMyPendings, boolean isCheckedStatus) {
        filterSettings.setCheckedMyPending(isCheckedMyPendings);
        filterSettings.setWorkflowTypeId(workflowTypeId);
        filterSettings.setCheckedStatus(isCheckedStatus);
        filterSettings.setTypeIdPositionInArray(typeIdPositionInArray);
    }

    protected void handleWorkflowTypeFilters(
            LifecycleOwner lifecycleOwner,
            //int workflowTypeId,
            int typeIdPositionInArray,
            boolean isCheckedMyPendings,
            boolean isCheckedStatus) {
        WorkflowTypeMenu menu = spinnerMenuArray.get(typeIdPositionInArray);
        updateFilterBoxSettings(menu.getWorkflowTypeId(), typeIdPositionInArray, isCheckedMyPendings, isCheckedStatus);
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterSettings);
    }

    private void applyFilters(FilterSettings filterSettings) {
        applyFilters(filterSettings, "");
    }

    private void applyFilters(FilterSettings filterSettings, String id) {
        switch (sort.getSortingType()) {
            case NONE: {
                if (filterSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            token,
                            id,
                            filterSettings.getSearchText());
                } else {
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            filterSettings.getWorkflowTypeId(),
                            token,
                            id,
                            filterSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYNUMBER: {
                if (filterSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getNumberSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOWID,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getNumberSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            filterSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOWID,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYCREATE: {
                if (filterSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOW_CREATED,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            filterSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOW_CREATED,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYUPDATE: {
                if (filterSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOW_UPDATED,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterSettings.isCheckedStatus(),
                            filterSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOW_UPDATED,
                            isDescending,
                            token,
                            id,
                            filterSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
        }
    }

    private void reloadWorkflowsList() {
//        liveWorkflows.removeObservers(lifecycleOwner); // TODO try putting this back and maybe it works and delete from handleWorkflowTypeFilters first line.
        liveWorkflows = workflowRepository.getAllWorkflows();
        addWorkflowObserver.postValue(true);
    }

    protected void handleCheckboxAllOnClick(boolean isChecked) {
        setAllCheckboxesList.postValue(isChecked);
    }

    protected LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return liveWorkflows;
    }

    protected void initFilters() {

        initWorkflowtypeMenu();

        if (filterSettings.isCheckedMyPending()) {
            toggleFilterSwitch(WorkflowFragment.SWITCH_PENDING, WorkflowFragment.CHECK);
        } else {
            toggleFilterSwitch(WorkflowFragment.SWITCH_PENDING, WorkflowFragment.UNCHECK);
        }
        if (filterSettings.isCheckedStatus()) {
            toggleFilterSwitch(WorkflowFragment.SWITCH_STATUS, WorkflowFragment.CHECK);
        } else {
            toggleFilterSwitch(WorkflowFragment.SWITCH_STATUS, WorkflowFragment.UNCHECK);
        }
        setSelectType.postValue(filterSettings.getTypeIdPositionInArray());
    }

    ArrayList<WorkflowTypeMenu> spinnerMenuArray;
    private void initWorkflowtypeMenu() {
        //init workflow type filter
        ListItem category;
        WorkflowTypeItemMenu typeMenu;

        ArrayMap<String, List<WorkflowTypeMenu>> result = new ArrayMap<>();

        String categoryName;
        List<WorkflowTypeMenu> tempMenus = new ArrayList<>();
        List<WorkflowTypeMenu> noCategory = new ArrayList<>();
        int idCat;
        int catId;
        for (int i = 0; i < categoryList.size(); i++) {
            category = categoryList.get(i);
            categoryName = category.getName();
            for (int j = 0; j < workflowTypeForMenu.size(); j++) {
                typeMenu = workflowTypeForMenu.get(j);
                idCat = typeMenu.getCategory();
                catId = category.getId();
                if (idCat == catId) {
                    String menuLabel = typeMenu.getName();
                    WorkflowTypeMenu menu = new WorkflowTypeMenu(
                            typeMenu.id,
                            menuLabel,
                            WorkflowTypeSpinnerAdapter.TYPE,
                            typeMenu.getId()
                    );
                    tempMenus.add(menu);
                }
            }
            if (tempMenus.isEmpty()) {
                continue;
            }
            result.put(categoryName, tempMenus);
            tempMenus = new ArrayList<>();
        }

        for (int i = 0; i < workflowTypeForMenu.size(); i++) {
            typeMenu = workflowTypeForMenu.get(i);
            idCat = typeMenu.getCategory();
            if (idCat == 0) {
                String menuLabel = typeMenu.getName();
                WorkflowTypeMenu menu = new WorkflowTypeMenu(
                        typeMenu.id,
                        menuLabel,
                        WorkflowTypeSpinnerAdapter.TYPE,
                        typeMenu.getId()
                );
                noCategory.add(menu);
            }
        }


        if (!noCategory.isEmpty()) {
            result.put(WorkflowTypeSpinnerAdapter.NO_CATEGORY_LABEL, noCategory);
        }

        spinnerMenuArray = new ArrayList<>();

        String key;
        WorkflowTypeMenu menu;
        for (int i = 0; i < result.size(); i++) {
            key = result.keyAt(i);
            menu = new WorkflowTypeMenu(
                    0,
                    key,
                    WorkflowTypeSpinnerAdapter.CATEGORY
            );
            spinnerMenuArray.add(menu);
            List<WorkflowTypeMenu> list = result.get(key);
            for (int j = 0; j < list.size(); j++) {
                menu = list.get(j);
                spinnerMenuArray.add(menu);
            }
        }

        WorkflowTypeMenu noSelection = new WorkflowTypeMenu(
                0,
                "NO SELECTION",
                WorkflowTypeSpinnerAdapter.NO_SELECTION
        );
        spinnerMenuArray.add(0, noSelection);

        workflowTypeMenuItems.setValue(spinnerMenuArray);
    }

    protected void initSortBy() {
        switch (sort.getSortingType()) {
            case BYNUMBER: {
                toggleRadioButton(WorkflowFragment.RADIO_NUMBER, WorkflowFragment.CHECK);
                break;
            }
            case BYCREATE: {
                toggleRadioButton(WorkflowFragment.RADIO_CREATED_DATE, WorkflowFragment.CHECK);
                break;
            }
            case BYUPDATE: {
                toggleRadioButton(WorkflowFragment.RADIO_UPDATED_DATE, WorkflowFragment.CHECK);
                break;
            }
        }
        if (sort.getNumberSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(WorkflowFragment.SWITCH_NUMBER, WorkflowFragment.CHECK);
        } else {
            toggleSwitch(WorkflowFragment.SWITCH_NUMBER, WorkflowFragment.UNCHECK);
        }
        if (sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(WorkflowFragment.SWITCH_CREATED_DATE, WorkflowFragment.CHECK);
        } else {
            toggleSwitch(WorkflowFragment.SWITCH_CREATED_DATE, WorkflowFragment.UNCHECK);
        }
        if (sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(WorkflowFragment.SWITCH_UPDATED_DATE, WorkflowFragment.CHECK);
        } else {
            toggleSwitch(WorkflowFragment.SWITCH_UPDATED_DATE, WorkflowFragment.UNCHECK);
        }
    }

    protected void handleRadioButtonClicked(boolean isChecked, @IdRes int viewId) {
        switch (viewId) {
            case R.id.chbx_workflownumber: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYNUMBER)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                    } else {
                        sort.setSortingType(Sort.sortType.BYNUMBER);
                        clearOtherSwitchesBut(Sort.sortType.BYNUMBER);
                    }
                }
                break;
            }
            case R.id.chbx_createdate: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYCREATE)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                    } else {
                        sort.setSortingType(Sort.sortType.BYCREATE);
                        clearOtherSwitchesBut(Sort.sortType.BYCREATE);
                    }
                }
                break;
            }
            case R.id.chbx_updatedate: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYUPDATE)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                    } else {
                        sort.setSortingType(Sort.sortType.BYUPDATE);
                        clearOtherSwitchesBut(Sort.sortType.BYUPDATE);
                    }
                }
                break;
            }
        }
    }

    protected void handleSwitchOnClick(
            int viewRadioType,
            Sort.sortType sortType,
            boolean isChecked
    ) {
        if (isChecked) {
            toggleRadioButton(viewRadioType, WorkflowFragment.CHECK);
            sort.setSortingType(sortType);
            if (sortType == Sort.sortType.BYNUMBER) {
                sort.setNumberSortOrder(Sort.sortOrder.ASC);
            } else if (sortType == Sort.sortType.BYCREATE) {
                sort.setCreatedSortOrder(Sort.sortOrder.ASC);
            } else if (sortType == Sort.sortType.BYUPDATE) {
                sort.setUpdatedSortOrder(Sort.sortOrder.ASC);
            }
            clearOtherSwitchesBut(sortType);
        } else {
            if (sortType == Sort.sortType.BYNUMBER) {
                sort.setNumberSortOrder(Sort.sortOrder.DESC);
            } else if (sortType == Sort.sortType.BYCREATE) {
                sort.setCreatedSortOrder(Sort.sortOrder.DESC);
            } else if (sortType == Sort.sortType.BYUPDATE) {
                sort.setUpdatedSortOrder(Sort.sortOrder.DESC);
            }
        }
    }

    private void updateSortObject(Sort.sortType ofType) {
        switch (ofType) {
            case BYNUMBER:
                sort.setCreatedSortOrder(Sort.sortOrder.DESC);
                sort.setUpdatedSortOrder(Sort.sortOrder.DESC);
                break;
            case BYCREATE:
                sort.setNumberSortOrder(Sort.sortOrder.DESC);
                sort.setUpdatedSortOrder(Sort.sortOrder.DESC);
                break;
            case BYUPDATE:
                sort.setNumberSortOrder(Sort.sortOrder.DESC);
                sort.setCreatedSortOrder(Sort.sortOrder.DESC);
                break;
            default:
                Log.d(TAG, "updateSortObject: Can't update sort Object using this sort type.");
        }
    }

    protected void handleUiAndIncomingList(PagedList<WorkflowListItem> listWorkflows) {
        if (listWorkflows == null) {
            showList.setValue(false);
            return;
        }
        if(listWorkflows.size() < 1){
            showList.setValue(false);
            return;
        }
        updateWithSortedList.setValue(listWorkflows);
        showList.setValue(true);
    }

    private void clearOtherSwitchesBut(Sort.sortType ofType) {
        updateSortObject(ofType);

        // Update UI
        switch (ofType) {
            case BYNUMBER:
                toggleSwitch(WorkflowFragment.SWITCH_CREATED_DATE, WorkflowFragment.UNCHECK);
                toggleSwitch(WorkflowFragment.SWITCH_UPDATED_DATE, WorkflowFragment.UNCHECK);
                break;
            case BYCREATE:
                toggleSwitch(WorkflowFragment.SWITCH_NUMBER, WorkflowFragment.UNCHECK);
                toggleSwitch(WorkflowFragment.SWITCH_UPDATED_DATE, WorkflowFragment.UNCHECK);
                break;
            case BYUPDATE:
                toggleSwitch(WorkflowFragment.SWITCH_CREATED_DATE, WorkflowFragment.UNCHECK);
                toggleSwitch(WorkflowFragment.SWITCH_NUMBER, WorkflowFragment.UNCHECK);
                break;
            default:
                Log.d(TAG, "clearOtherSwitchesBut: Using wrong Sorty type which is uknown.");
        }
    }

    private void clearRadioButtonGroup() {
        toggleRadioButton(WorkflowFragment.RADIO_CLEAR_ALL, WorkflowFragment.UNCHECK);
    }

    private void toggleRadioButton(int viewRadioType, int viewIsCheckType) {
        int[] toggleRadio = new int[2];
        toggleRadio[WorkflowFragment.INDEX_TYPE] = viewRadioType;
        toggleRadio[WorkflowFragment.INDEX_CHECK] = viewIsCheckType;


//        toggleRadioButton.setValue(toggleRadio);
        messageMainToggleRadioButton.setValue(toggleRadio);
    }

    private void toggleSwitch(int viewSwitchType, int viewIsCheckType) {
        int[] toggleSwitch = new int[2];
        toggleSwitch[WorkflowFragment.INDEX_TYPE] = viewSwitchType;
        toggleSwitch[WorkflowFragment.INDEX_CHECK] = viewIsCheckType;
        
        messageMainToggleSwitch.setValue(toggleSwitch);
//        this.toggleSwitch.setValue(toggleSwitch);

    }

    private void toggleFilterSwitch(int viewSwitchType, int viewIsCheckType) {
        int[] toggleSwitch = new int[2];
        toggleSwitch[WorkflowFragment.INDEX_TYPE] = viewSwitchType;
        toggleSwitch[WorkflowFragment.INDEX_CHECK] = viewIsCheckType;
        toggleFilterSwitch.setValue(toggleSwitch);
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

    protected LiveData<PagedList<WorkflowListItem>> getObservableUpdateWithSortedList() {
        if (updateWithSortedList == null) {
            updateWithSortedList = new MutableLiveData<>();
        }
        return updateWithSortedList;
    }

    protected LiveData<int[]> getObservableToggleRadioButton() {
        if (toggleRadioButton == null) {
            toggleRadioButton = new MutableLiveData<>();
        }
        return toggleRadioButton;
    }

    protected LiveData<int[]> getObservableToggleSwitch() {
        if (toggleSwitch == null) {
            toggleSwitch = new MutableLiveData<>();
        }
        return toggleSwitch;
    }

    protected LiveData<int[]> getObservableToggleFilterSwitch() {
        if (toggleFilterSwitch == null) {
            toggleFilterSwitch = new MutableLiveData<>();
        }
        return toggleFilterSwitch;
    }

    public LiveData<Boolean> getObservableShowList() {
        if (showList == null) {
            showList = new MutableLiveData<>();
        }
        return showList;
    }

    protected LiveData<List<WorkflowTypeMenu>> getObservableTypeItemMenu() {
        return workflowTypeMenuItems;
    }

    protected LiveData<Boolean> getObservableAddWorkflowObserver() {
        if (addWorkflowObserver == null) {
            addWorkflowObserver = new MutableLiveData<>();
        }
        return addWorkflowObserver;
    }

    protected LiveData<Boolean> getObservableSetAllCheckboxesList() {
        if (setAllCheckboxesList == null) {
            setAllCheckboxesList = new MutableLiveData<>();
        }
        return setAllCheckboxesList;
    }

    protected LiveData<Integer> getObservableSetSelectType() {
        if (setSelectType == null) {
            setSelectType = new MutableLiveData<>();
        }
        return setSelectType;
    }

}
