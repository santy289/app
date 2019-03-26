package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListField;
import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.activation.WorkflowActivationResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.DeleteWorkflowResponse;
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
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_CREATE;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_VIEW;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_TYPE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CLEAR_ALL;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_UPDATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_UPDATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.UNCHECK;

public class WorkflowViewModel extends ViewModel {

    public static final int NO_TYPE_SELECTED = 0;
    public static final int WORKFLOW_TYPE_FIELD = -98;

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
    private MutableLiveData<Boolean> showAddButtonLiveData;
    private MutableLiveData<Boolean> showViewWorkflowButtonLiveData;
    private MutableLiveData<Boolean> completeMassAction;
    public MutableLiveData<Boolean> showBottomSheetLoading;
    protected MutableLiveData<Boolean> clearFilters;
    private LiveData<PagedList<WorkflowListItem>> liveWorkflows;

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
    protected MutableLiveData<Integer> messageMainUpdateSortSelection;

    protected MutableLiveData<OptionsList> messageMainBaseFilters;
    protected MutableLiveData<Integer> messageMainBaseFilterSelectionToFilterList;

    // MOVED TO FILTERSETTINGS
    //protected List<WorkflowTypeMenu> rightDrawerFilters;

    // TODO en development
//

    private WorkflowRepository workflowRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private Sort sort;
    private FilterSettings filterSettings;
    private String token;
    private String userId;
    private int categoryId;
    private List<ListItem> categoryList;
    private static final String TAG = "WorkflowViewModel";
    private boolean hasViewDetailsPermissions;

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
        messageMainUpdateSortSelection = new MutableLiveData<>();
        messageMainBaseFilters = new MutableLiveData<>();
        messageMainBaseFilterSelectionToFilterList = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        workflowRepository.clearDisposables();
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link WorkflowFragment}.
     * Hide the UI related to the unauthorized actions.
     *
     * @param prefs shared preferences
     */
    protected void checkPermissions(SharedPreferences prefs) {
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        boolean hasCreatePermissions = permissionsUtils.hasPermission(WORKFLOW_CREATE);
        hasViewDetailsPermissions = permissionsUtils.hasPermission(WORKFLOW_VIEW);

        showAddButtonLiveData.setValue(hasCreatePermissions);
        showViewWorkflowButtonLiveData.setValue(hasViewDetailsPermissions);
    }

    protected boolean hasViewDetailsPermissions() {
        return hasViewDetailsPermissions;
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
            workflowRepository.getWorkflowsByType(token, id, true);
        } else {
            workflowRepository.getAllWorkflowsNoFilters(token);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void insert(WorkflowDb workflow) {
        workflowRepository.insertWorkflow(workflow);
    }

    protected void initWorkflowList(SharedPreferences sharedPreferences,
                                    LifecycleOwner lifecycleOwner) {
        if (!TextUtils.isEmpty(token)) {
            showLoading.setValue(true);
            int baseFilterId = filterSettings.getBaseFilterSelectedId();
            loadWorkflowsByBaseFilters(baseFilterId, filterSettings, lifecycleOwner);
            return;
        }
        token = "Bearer " + sharedPreferences.getString("token", "");
        userId = sharedPreferences.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        categoryId = sharedPreferences.getInt("category_id", 0);
        setWorkflowListNoFilters(token);
        subscribe(lifecycleOwner);
    }

    protected void iniRightDrawerFilters() {
        if (filterSettings.getSizeOfRightDrawerOptionsListMap() > 0) {
            return;
        }

        // Get categories from network & init FilterSettings with generated Workflow Types.
        getCategories(categoryId);
        initRightDrawerFilterList();
        initBaseFilters();
        initSortBy();
    }

    /**
     * This function will check if we already have a Filter List populated, and if not it will go to
     * create a new Workflow Type Menu item for the filter list and send a message back to the Main
     * Activity to render the content on the UI.
     */
    private void initRightDrawerFilterList() {
        if (filterSettings
                .hasIdinFilterDrawerList(FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID)) {
            // Update UI
            rightDrawerFilterMenus.setValue(filterSettings.getFilterDrawerList());
            invalidateDrawerOptionsList.setValue(true);
            return;
        }
        WorkflowTypeMenu menuItem = new WorkflowTypeMenu(
                FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID,
                "",
                "",
                RightDrawerFiltersAdapter.TYPE,
                WORKFLOW_TYPE_FIELD
        );
        filterSettings.addFilterListMenu(menuItem);
        // Update UI
        rightDrawerFilterMenus.setValue(filterSettings.getFilterDrawerList());
        invalidateDrawerOptionsList.setValue(true);
    }

    public static final int BASE_FILTER_TYPE = -78;
    public static final int BASE_FILTER_ALL_ID = 44;
    public static final int BASE_FILTER_OUT_OF_TIME_ID = 45;
    public static final int BASE_FILTER_ON_TIME_ID = 46;
    public static final int BASE_FILTER_PENDING_BY_ME_ID = 47;
    public static final int BASE_FILTER_LATEST_ID = 48;

    /**
     * Creates a new list of base filter options only if FilterSettings.baseFilterOptionsList is
     * empty.
     */
    private void initBaseFilters() {
        if (filterSettings.getSizeBseFilterOptionList() > 0) {
            // It already exists.
            return;
        }

        List<WorkflowTypeMenu> baseFilterOptionsList = new ArrayList<>();
        WorkflowTypeMenu baseMenu = new WorkflowTypeMenu(
                BASE_FILTER_ALL_ID,
                R.string.all,
                WorkflowTypeSpinnerAdapter.TYPE,
                BASE_FILTER_TYPE
        );
        baseMenu.setSelected(true);
        baseFilterOptionsList.add(baseMenu);

        baseMenu = new WorkflowTypeMenu(
                BASE_FILTER_OUT_OF_TIME_ID,
                R.string.out_of_time,
                WorkflowTypeSpinnerAdapter.TYPE,
                BASE_FILTER_TYPE
        );
        baseFilterOptionsList.add(baseMenu);

        baseMenu = new WorkflowTypeMenu(
                BASE_FILTER_ON_TIME_ID,
                R.string.on_time,
                WorkflowTypeSpinnerAdapter.TYPE,
                BASE_FILTER_TYPE
        );
        baseFilterOptionsList.add(baseMenu);

        baseMenu = new WorkflowTypeMenu(
                BASE_FILTER_PENDING_BY_ME_ID,
                R.string.pending_approval,
                WorkflowTypeSpinnerAdapter.TYPE,
                BASE_FILTER_TYPE
        );
        baseFilterOptionsList.add(baseMenu);

        baseMenu = new WorkflowTypeMenu(
                BASE_FILTER_LATEST_ID,
                R.string.latest,
                WorkflowTypeSpinnerAdapter.TYPE,
                BASE_FILTER_TYPE
        );
        baseFilterOptionsList.add(baseMenu);

        filterSettings.setBaseFilterOptionsList(baseFilterOptionsList);
    }

    private void getCategories(int id) {
        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .getCategoryList(token, id)
                .subscribe(listsResponse -> {
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

    protected void resetFilterSettings() {
        filterSettings.resetFilterSettings();
    }

    /**
     * Groups workflow types in their respective categories and saves results in FilterSettings.
     */
    private void initWorkflowTypeMenus() {
        //init workflow type filter
        ListItem category;
        WorkflowTypeItemMenu typeMenu;

        // Organize all workflows in a result ArrayMap where the key is the Category name, and the
        // values are lists of our workflow types.

        ArrayMap<String, List<WorkflowTypeMenu>> result = new ArrayMap<>();

        String categoryName;
        List<WorkflowTypeMenu> tempMenus = new ArrayList<>();
        List<WorkflowTypeMenu> noCategory = new ArrayList<>();
        int idCat;
        int catId;
        for (int i = 0; i < categoryList.size(); i++) {
            category = categoryList.get(i);
            categoryName = category.getName();
            catId = category.getId();
            for (int j = 0; j < workflowTypeForMenu.size(); j++) {
                typeMenu = workflowTypeForMenu.get(j);
                idCat = typeMenu.getCategory();
                if (idCat == catId && !isOriginalIdInList(typeMenu.getOriginalId(), tempMenus)) {
                    String menuLabel = typeMenu.getName();
                    WorkflowTypeMenu menu = new WorkflowTypeMenu(
                            typeMenu.id,
                            menuLabel,
                            WorkflowTypeSpinnerAdapter.TYPE,
                            typeMenu.getId(),
                            typeMenu.getOriginalId()
                    );
                    menu.setWorkflowCount(typeMenu.getWorkflowCount());
                    tempMenus.add(menu);
                }
            }
            if (tempMenus.isEmpty()) {
                continue;
            }
            result.put(categoryName, tempMenus);
            tempMenus = new ArrayList<>();
        }

        // Find workflow types that have no category related to them.
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
                menu.setWorkflowCount(typeMenu.getWorkflowCount());
                noCategory.add(menu);
            }
        }

        if (!noCategory.isEmpty()) {
            result.put(WorkflowTypeSpinnerAdapter.NO_CATEGORY_LABEL, noCategory);
        }

        // Create one single ArrayList that will keep all the workflow types and categories in order.
        // This spinnerMenuArray is the reference of how the UI will display the list of worklfow types.
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
        filterSettings.saveOptionsListFor(FilterSettings.RIGHT_DRAWER_FILTER_TYPE_ITEM_ID,
                spinnerMenuArray);
    }

    /**
     * Finds out if the originalId is already saved on the WorkflowTypeMenu list.
     *
     * @param originalId
     * @param types
     *
     * @return
     */
    private boolean isOriginalIdInList(int originalId, List<WorkflowTypeMenu> types) {
        if (types == null || types.size() < 1) {
            return false;
        }

        WorkflowTypeMenu targetType;
        for (int i = 0; i < types.size(); i++) {
            targetType = types.get(i);
            if (targetType.getOriginalId() == originalId) {
                return true;
            }

        }

        return false;
    }

    @Deprecated
    protected void loadWorkflowsByType(int position, LifecycleOwner lifecycleOwner) {
        showLoading.postValue(true);
        WorkflowTypeMenu menu = spinnerMenuArray.get(position);
        int typeId = menu.getWorkflowTypeId();
        if (typeId == NO_TYPE_SELECTED) {
            workflowRepository.getAllWorkflowsNoFilters(token);
        } else {
            workflowRepository.getWorkflowsByType(token, typeId, false);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void loadWorkflowsByType(WorkflowTypeMenu menu, LifecycleOwner lifecycleOwner) {
//        showLoading.postValue(true);
        int typeId = menu.getWorkflowTypeId();
        if (typeId == NO_TYPE_SELECTED) {
            workflowRepository.getAllWorkflowsNoFilters(token);
        } else {
            workflowRepository.getWorkflowsByType(token, typeId, false);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    /**
     * Request for filtered data to workflow repository. This function handles all the different
     * filtering scenarios. It handles cases with base filters, meta data, workflow type filters.
     *
     * @param selectedBaseFilterId Id of selected base filter.
     * @param filterSettings       Instance of FilterSettings.
     * @param lifecycleOwner       Fragment with observer that we need to remove.
     */
    private void loadWorkflowsByBaseFilters(
            int selectedBaseFilterId,
            FilterSettings filterSettings,
            LifecycleOwner lifecycleOwner) {
        String metaDataString = filterSettings.getAllItemIdsSelectedAsString();
        int workflowTypeSelected = filterSettings.getWorkflowTypeId();

        Map<String, Object> options = new ArrayMap<>();
        if (workflowTypeSelected != NO_TYPE_SELECTED) {
            options.put("workflow_type_id", workflowTypeSelected);
        }
        if (!TextUtils.isEmpty(metaDataString)) {
            options.put("workflow_metadata", metaDataString);
        }

        switch (selectedBaseFilterId) {
            case BASE_FILTER_ALL_ID:
                // Nothing to do add to options.
                break;
            case BASE_FILTER_OUT_OF_TIME_ID:
                options.put("out_of_time", true);
                break;
            case BASE_FILTER_ON_TIME_ID:
                options.put("out_of_time", false);
                break;
            case BASE_FILTER_PENDING_BY_ME_ID:
                options.put("responsible_id", userId);
                break;
            case BASE_FILTER_LATEST_ID:
                options.put("latest", true);
                break;
        }

        workflowRepository.getWorkflowsByBaseFilters(token, options);
        liveWorkflows.removeObservers(lifecycleOwner);
    }

    protected void handleBaseFieldClick() {
        OptionsList optionsList = filterSettings.handleOptionListForBaseFilters();
        messageMainBaseFilters.setValue(optionsList);
    }

    /**
     * Handles a position tapped on the List of BaseFilters. This function will update
     * FilterSettings with the latest position selected and make a network call to update the
     * database with the selected base filter.
     *
     * @param position       Selection done by the user on the list of base filters.
     * @param lifecycleOwner Fragment has an observer that we need to remove and use a new one.
     */
    protected void handleBaseFieldPositionSelected(int position, LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        WorkflowTypeMenu filterSelected;
        filterSelected = filterSettings.handleBaseFilterPositionSelected(position);
        int baseFilterId = filterSelected.getId();
        if (!filterSelected.isSelected()) {
            // Unselected, no items selected. Filter by All.
            filterSettings.resetBaseFilterSelectionToAll();
            baseFilterId = BASE_FILTER_ALL_ID;
            messageMainBaseFilterSelectionToFilterList.setValue(R.string.all);
        } else {
            messageMainBaseFilterSelectionToFilterList.setValue(filterSelected.getResLabel());
        }
        invalidateDrawerOptionsList.setValue(true);
        loadWorkflowsByBaseFilters(baseFilterId, filterSettings, lifecycleOwner);
    }

    /**
     * When a filter is selected use this handler to populate the options list for the selected
     * filter.
     *
     * @param position Index of the position selected in the UI ListView.
     */
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
        if (menuList.isEmpty()) return;
        WorkflowTypeMenu menu = menuList.get(position);

        // Selecting workflow type no need to update other items.
        // Clear all fields if we are selecting a new workflow type.
        if (filterSettings.getFilterListIndexSelected() == DRAWER_FILTER_LIST_INDEX_TYPE) {
            handleFilterByWorkflowType(menu, lifecycleOwner);
            return;
        }

        filterSettings.updateRightDrawerOptionListWithSelected(menu, true);
        updateSelectedMenuItem(menu);
        filterSettings.updateFilterListItemSelected(menu);

        int baseFilterId = filterSettings.getBaseFilterSelectedId();
        loadWorkflowsByBaseFilters(baseFilterId, filterSettings, lifecycleOwner);
    }

    private void handleFilterByWorkflowType(WorkflowTypeMenu menu, LifecycleOwner lifecycleOwner) {
        filterSettings.clearDynamicFields();

        if (filterSettings.isTypeAlreadySelected(menu.getWorkflowTypeId())) {
            // Update FilterSettings and Right Drawer UI.
            filterSettings.setWorkflowTypeId(NO_TYPE_SELECTED);
            sendMessageUpdateSelectionToWorkflowList(Sort.sortType.NONE);

            filterSettings.updateWorkflowTypeListFilterItem(null);
            filterSettings.updateRightDrawerOptionListWithSelected(menu, false); //TEST
            updateSelectedMenuItem(menu);
//                filterSettings.updateFilterListItemSelected(menu);
            showLoading.setValue(false);
            applyFilters(filterSettings);
            return;
        }

        // Choosing some workflow type from the filter Workflow Type.
        // Update Filter Settings with new workflow type id.
        filterSettings.setWorkflowTypeId(menu.getOriginalId());
        loadWorkflowsByType(menu, lifecycleOwner);

        // clear any selected items if any
        filterSettings.clearworklowTypeSelection();

        // Filter List Update with new selection
        filterSettings.updateWorkflowTypeListFilterItem(menu);

        // Update Drawer Options List
        filterSettings.updateRightDrawerOptionListWithSelected(menu, false);

        updateSelectedMenuItem(menu);
//            filterSettings.updateFilterListItemSelected(menu);

        // Allowing single selection on the UI for this list.
        invalidateDrawerOptionsList.setValue(true);

        // TODO double check if we need this removeObservers() we call already in loadWorkflowByType() something similar.
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterSettings);

        int workflowTypeId = menu.getWorkflowTypeId();
        findDynamicFieldsBy(workflowTypeId);
    }

    private FormSettings formSettings;

    // TODO USE TO CREATE GET REQUEST FOR FILTERING
    private WorkflowMetas createMetaData(FieldData fieldData, String valueSelected,
                                         int workflowTypeFieldId, FieldConfig fieldConfig) {
        WorkflowMetas workflowMeta = new WorkflowMetas();
        workflowMeta.setUnformattedValue(valueSelected);
        workflowMeta.setWorkflowTypeFieldId(workflowTypeFieldId);
//        formSettings.formatMetaData(workflowMeta, fieldData, fieldConfig); //todo check
        return workflowMeta;
    }

    private void updateSelectedMenuItem(WorkflowTypeMenu menu) {
        menu.setSelected(!menu.isSelected());
    }

    private void findDynamicFieldsBy(int workflowTypeId) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<FormFieldsByWorkflowType> fields = workflowRepository
                    .getFiedsByWorkflowType(workflowTypeId);
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
                .subscribe(dynamicFieldsSettings -> {
                    Log.d(TAG, "findDynamicFieldsBy: ");
                    FormSettings settings = formSettings;
                    showFields(settings);
                }, throwable -> {
                    showLoading.postValue(false);
                    Log.d(TAG,
                            "findDynamicFieldsBy: Something went wrong getting fields" + throwable
                                    .getMessage());
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
                Log.d(TAG, "buildField: Not a generic type: " + typeInfo
                        .getType() + " value: " + typeInfo.getValueType());
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
            createSystemUserFieldasCustomField(field, listId, customFieldId, customLabel,
                    associatedWorkflowTypeId);
            return;
        }

        Disposable disposable = workflowRepository
                .getList(token, listId)
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
                .subscribe(listField -> {
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
//        formSettings.addFieldDataItem(fieldData); //todo check
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
            fieldData.list = listData;
            fieldData.tag = field.getId();
            fieldData.isMultipleSelection = isMultipleSelection;
            fieldData.escape = escape(fieldConfig);
//            formSettings.addFieldDataItem(fieldData); //todo check
            //setFieldList.postValue(fieldListSettings);
            return fieldListSettings;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fieldListSettings -> {
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
                || value.equals(FormSettings.VALUE_COORDS)) {
            return true;
        }
        if (value.equals(FormSettings.VALUE_LIST) && type.equals(FormSettings.TYPE_SYSTEM_USERS)) {
            return true;
        }
        return false;
    }

    /**
     * Sets search text value to FilterSettings object. Restart live data and apply rest of the if
     * any.
     *
     * @param searchText
     * @param lifecycleOwner
     */
    protected void filterBySearchText(String searchText, LifecycleOwner lifecycleOwner) {
        filterSettings.setSearchText(searchText);
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterSettings);
    }

    protected void clearFilters() {
        clearFilters.setValue(true);
    }

    private void subscribe(LifecycleOwner lifecycleOwner) {
        final Observer<Boolean> handleRepoErrorObserver = (error -> {
            showLoading.postValue(false);
        });
        final Observer<Boolean> handleRepoSuccessObserver = (success -> {
            showLoading.postValue(false);
            // TODO change data source to new query pointing ONLY to my profile id.
            applyFilters(filterSettings, userId);
        });
        final Observer<Boolean> handleRepoSuccessNoFilterObserver = (success -> {
            showLoading.postValue(false);
            applyFilters(filterSettings);
        });

        final Observer<Boolean> handleRestSuccessWithNoApplyFilter = (success -> {
            showLoading.postValue(false);
        });

        workflowRepository.getObservableHandleRepoError().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoError()
                .observe(lifecycleOwner, handleRepoErrorObserver);

        workflowRepository.getObservableHandleRepoSuccess().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoSuccess()
                .observe(lifecycleOwner, handleRepoSuccessObserver);

        workflowRepository.getObservableHandleRepoSuccessNoFilter().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoSuccessNoFilter()
                .observe(lifecycleOwner, handleRepoSuccessNoFilterObserver);

        workflowRepository.getObservableHandleRestSuccessWithNoApplyFilter().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRestSuccessWithNoApplyFilter()
                .observe(lifecycleOwner, handleRestSuccessWithNoApplyFilter);
    }

    protected LiveData<Boolean> getObservableLoadMore() {
        return workflowRepository.showLoadMore;
    }

    private void updateFilterBoxSettings(int workflowTypeId, int typeIdPositionInArray,
                                         boolean isCheckedMyPendings, boolean isCheckedStatus) {
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
        updateFilterBoxSettings(menu.getWorkflowTypeId(), typeIdPositionInArray,
                isCheckedMyPendings, isCheckedStatus);
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterSettings);
    }

    protected void applyFilters() {
        if (filterSettings == null) {
            return;
        }
        applyFilters(filterSettings);
    }

    private void applyFilters(FilterSettings filterSettings) {
        applyFilters(filterSettings, "");
    }

    /**
     * Checks the FilterSettings object and according the parameters selected by the user it will
     * sort, filter queries to the local database. At the same time it will initalize a new
     * BoundaryCallback to enable paging for the list. Finally it will remove any observers for the
     * LiveData observing the previous local query, and update the LiveData to observe to the new
     * local database query.
     *
     * @param filterSettings
     * @param id
     */
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
                toggleRadioButton(RADIO_NUMBER, CHECK);
                break;
            }
            case BYCREATE: {
                toggleRadioButton(RADIO_CREATED_DATE, CHECK);
                break;
            }
            case BYUPDATE: {
                toggleRadioButton(RADIO_UPDATED_DATE, CHECK);
                break;
            }
        }
        if (sort.getNumberSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(SWITCH_NUMBER, CHECK);
        } else {
            toggleSwitch(SWITCH_NUMBER, UNCHECK);
        }
        if (sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(SWITCH_CREATED_DATE, CHECK);
        } else {
            toggleSwitch(SWITCH_CREATED_DATE, UNCHECK);
        }
        if (sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC)) {
            toggleSwitch(SWITCH_UPDATED_DATE, CHECK);
        } else {
            toggleSwitch(SWITCH_UPDATED_DATE, UNCHECK);
        }
    }

    public static final int IS_CHECKED_INDEX = 0;
    public static final int VIEW_ID_INDEX = 1;

    protected void receiveMessageRadioButtonClicked(int[] message) {
        int checked = message[IS_CHECKED_INDEX];
        int viewId = message[VIEW_ID_INDEX];
        boolean isChecked = false;
        if (checked == CHECK) {
            isChecked = true;
        }
        handleRadioButtonClicked(isChecked, viewId);
    }

    protected void handleRadioButtonClicked(boolean isChecked, @IdRes int viewId) {
        switch (viewId) {
            case R.id.chbx_workflownumber: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYNUMBER)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.NONE);
                    } else {
                        sort.setSortingType(Sort.sortType.BYNUMBER);
                        clearOtherSwitchesBut(Sort.sortType.BYNUMBER);
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.BYNUMBER);
                    }
                }
                break;
            }
            case R.id.chbx_createdate: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYCREATE)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.NONE);
                    } else {
                        sort.setSortingType(Sort.sortType.BYCREATE);
                        clearOtherSwitchesBut(Sort.sortType.BYCREATE);
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.BYCREATE);
                    }
                }
                break;
            }
            case R.id.chbx_updatedate: {
                if (isChecked) {
                    if (sort.getSortingType().equals(Sort.sortType.BYUPDATE)) {
                        sort.setSortingType(Sort.sortType.NONE);
                        clearRadioButtonGroup();
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.NONE);
                    } else {
                        sort.setSortingType(Sort.sortType.BYUPDATE);
                        clearOtherSwitchesBut(Sort.sortType.BYUPDATE);
                        sendMessageUpdateSelectionToWorkflowList(Sort.sortType.BYUPDATE);
                    }
                }
                break;
            }
        }
    }

    private void sendMessageUpdateSelectionToWorkflowList(Sort.sortType sortType) {
        switch (sortType) {
            case NONE:
                messageMainUpdateSortSelection.setValue(R.string.no_selection);
                break;
            case BYNUMBER:
                messageMainUpdateSortSelection.setValue(R.string.workflow_number);
                break;
            case BYCREATE:
                messageMainUpdateSortSelection.setValue(R.string.created_date);
                break;
            case BYUPDATE:
                messageMainUpdateSortSelection.setValue(R.string.updated_date);
                break;
            default:
                break;
        }
    }

    protected void handleSwitchOnClick(
            int viewRadioType,
            Sort.sortType sortType,
            boolean isChecked
    ) {
        if (isChecked) {
            toggleRadioButton(viewRadioType, CHECK);
            sort.setSortingType(sortType);
            sendMessageUpdateSelectionToWorkflowList(sortType);
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
        if (listWorkflows.size() < 1) {
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
                toggleSwitch(SWITCH_CREATED_DATE, UNCHECK);
                toggleSwitch(SWITCH_UPDATED_DATE, UNCHECK);
                break;
            case BYCREATE:
                toggleSwitch(SWITCH_NUMBER, UNCHECK);
                toggleSwitch(SWITCH_UPDATED_DATE, UNCHECK);
                break;
            case BYUPDATE:
                toggleSwitch(SWITCH_CREATED_DATE, UNCHECK);
                toggleSwitch(SWITCH_NUMBER, UNCHECK);
                break;
            default:
                Log.d(TAG, "clearOtherSwitchesBut: Using wrong Sorty type which is uknown.");
        }
    }

    private void clearRadioButtonGroup() {
        toggleRadioButton(RADIO_CLEAR_ALL, UNCHECK);
    }

    private void toggleRadioButton(int viewRadioType, int viewIsCheckType) {
        int[] toggleRadio = new int[2];
        toggleRadio[INDEX_TYPE] = viewRadioType;
        toggleRadio[INDEX_CHECK] = viewIsCheckType;
        messageMainToggleRadioButton.setValue(toggleRadio);
    }

    private void toggleSwitch(int viewSwitchType, int viewIsCheckType) {
        int[] toggleSwitch = new int[2];
        toggleSwitch[INDEX_TYPE] = viewSwitchType;
        toggleSwitch[INDEX_CHECK] = viewIsCheckType;
        messageMainToggleSwitch.setValue(toggleSwitch);
    }

    protected void openCloseWorkflows(List<Integer> workflowIds, boolean open) {
        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .postOpenCloseActivation(token, workflowIds, open)
                .subscribe(this::onOpenCloseWorkflowsSuccess, this::onFailure);

        disposables.add(disposable);
    }

    private void onOpenCloseWorkflowsSuccess(WorkflowActivationResponse workflowActivationResponse) {
        completeMassAction.setValue(true);
    }

    protected void enableDisableWorkflows(List<Integer> workflowIds, boolean enable) {
        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .postEnableDisableActivation(token, workflowIds, enable)
                .subscribe(this::onEnableDisableWorkflowsSuccess, this::onFailure);

        disposables.add(disposable);
    }

    private void onEnableDisableWorkflowsSuccess(WorkflowActivationResponse workflowActivationResponse) {
        completeMassAction.setValue(true);
    }

    protected void deleteWorkflows(List<Integer> workflowIds) {
        if (workflowIds.isEmpty()) return;

        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .postDeleteWorkflows(token, workflowIds.get(0), workflowIds)
                .subscribe(this::onDeleteWorkflowsSuccess, this::onFailure);

        disposables.add(disposable);
    }

    private void onDeleteWorkflowsSuccess(DeleteWorkflowResponse deleteWorkflowResponse) {
        completeMassAction.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: " + throwable.getMessage());
        showLoading.setValue(false);
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
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

    protected LiveData<Boolean> getObservableShowAddButton() {
        if (showAddButtonLiveData == null) {
            showAddButtonLiveData = new MutableLiveData<>();
        }
        return showAddButtonLiveData;
    }

    protected LiveData<Boolean> getObservableShowViewWorkflowButton() {
        if (showViewWorkflowButtonLiveData == null) {
            showViewWorkflowButtonLiveData = new MutableLiveData<>();
        }
        return showViewWorkflowButtonLiveData;
    }

    protected LiveData<Integer> getObservableSetSelectType() {
        if (setSelectType == null) {
            setSelectType = new MutableLiveData<>();
        }
        return setSelectType;
    }

    protected LiveData<Boolean> getObservableCompleteMassAction() {
        if (completeMassAction == null) {
            completeMassAction = new MutableLiveData<>();
        }
        return completeMassAction;
    }

}
