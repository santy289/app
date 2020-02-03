package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.responses.activation.WorkflowActivationResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.DeleteWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.PostDeleteWorkflows;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowTypeSpinnerAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowRepository;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_ACTIVATE_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_CREATE;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_DELETE;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_OPEN_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_VIEW;
import static com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel.TAG_STANDARD_ACTIVE;
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
    public static final int REQUEST_WORKFLOW_DETAIL = 26;

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<PagedList<WorkflowListItem>> updateWithSortedList;
    private MutableLiveData<Integer> setSelectType;
    private MutableLiveData<Boolean> showList;
    private MutableLiveData<Boolean> addWorkflowObserver;
    private MutableLiveData<Boolean> setAllCheckboxesList;
    private MutableLiveData<Boolean> showAddButtonLiveData;
    private MutableLiveData<Boolean> showViewWorkflowButtonLiveData;
    private MutableLiveData<Boolean> completeMassAction;
    private MutableLiveData<Boolean> handleScrollRecyclerToTop;
    private MutableLiveData<Boolean> showBulkActionMenuLiveData;
    private MutableLiveData<Integer> filtersCounterLiveData;
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

    protected MutableLiveData<OptionsList> messageMainWorkflowTypeFilters;
    protected MutableLiveData<OptionsList> messageMainBaseFilters;
    protected MutableLiveData<OptionsList> messageMainStatusFilters;
    protected MutableLiveData<OptionsList> messageMainSystemStatusFilters;
    protected MutableLiveData<String> messageMainWorkflowTypeFilterSelectionToFilterList;
    protected MutableLiveData<Integer> messageMainWorkflowTypeIdFilterSelectionToFilterList;
    protected MutableLiveData<Integer> messageMainBaseFilterSelectionToFilterList;
    protected MutableLiveData<Integer> messageMainStatusFilterSelectionToFilterList;
    protected MutableLiveData<Integer> messageMainSystemStatusFilterSelectionToFilterList;

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
    private boolean hasBulkDeletePermissions;
    private boolean hasBulkOpenClosePermissions;
    private boolean hasBulkActivationPermissions;
    private List<Integer> mWorkflowIdsToDelete;
    private Boolean isSwipe;
    private boolean isShowOpenActionMenu;
    private boolean isShowCloseActionMenu;
    private boolean isShowEnableActionMenu;
    private boolean isShowDisableActionMenu;
    private boolean isSortFilterApplied;
    private boolean isBaseFilterApplied;
    private boolean isStatusFilterApplied;
    private boolean isSystemStatusFilterApplied;
    private boolean isWorkflowTypeFilterApplied;
    private boolean isScrollToTop;

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
        messageMainToggleRadioButton = new MutableLiveData<>();
        messageMainToggleSwitch = new MutableLiveData<>();
        messageMainUpdateSortSelection = new MutableLiveData<>();
        messageMainWorkflowTypeFilters = new MutableLiveData<>();
        messageMainBaseFilters = new MutableLiveData<>();
        messageMainStatusFilters = new MutableLiveData<>();
        messageMainSystemStatusFilters = new MutableLiveData<>();
        messageMainWorkflowTypeFilterSelectionToFilterList = new MutableLiveData<>();
        messageMainWorkflowTypeIdFilterSelectionToFilterList = new MutableLiveData<>();
        messageMainBaseFilterSelectionToFilterList = new MutableLiveData<>();
        messageMainStatusFilterSelectionToFilterList = new MutableLiveData<>();
        messageMainSystemStatusFilterSelectionToFilterList = new MutableLiveData<>();

        //Open filter is selected by default
        isShowOpenActionMenu = false;
        isShowCloseActionMenu = true;

        //Active filter is selected by default
        isShowEnableActionMenu = false;
        isShowDisableActionMenu = true;
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
        hasBulkDeletePermissions = permissionsUtils.hasPermission(WORKFLOW_DELETE);
        hasBulkOpenClosePermissions = permissionsUtils.hasPermission(WORKFLOW_OPEN_ALL);
        hasBulkActivationPermissions = permissionsUtils.hasPermission(WORKFLOW_ACTIVATE_ALL);

        showBulkActionMenuLiveData.setValue(
                hasBulkDeletePermissions || hasBulkOpenClosePermissions || hasBulkActivationPermissions);

        showAddButtonLiveData.setValue(hasCreatePermissions);
        showViewWorkflowButtonLiveData.setValue(hasViewDetailsPermissions);
    }

    protected boolean hasViewDetailsPermissions() {
        return hasViewDetailsPermissions;
    }

    protected boolean hasBulkDeletePermissions() {
        return hasBulkDeletePermissions;
    }

    protected boolean hasBulkOpenClosePermissions() {
        return hasBulkOpenClosePermissions;
    }

    protected boolean hasBulkActivationPermissions() {
        return hasBulkActivationPermissions;
    }

    protected boolean isShowOpenActionMenu() {
        return isShowOpenActionMenu;
    }

    protected boolean isShowCloseActionMenu() {
        return isShowCloseActionMenu;
    }

    protected boolean isShowEnableActionMenu() {
        return isShowEnableActionMenu;
    }

    protected boolean isShowDisableActionMenu() {
        return isShowDisableActionMenu;
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

    protected void insert(WorkflowDb workflow) {
        workflowRepository.insertWorkflow(workflow);
    }

    private void sendFiltersCounterToUi() {
        int count = filterSettings.getDynamicFilters().size();

        if (isSortFilterApplied) count++;
        if (isBaseFilterApplied) count++;
        if (isStatusFilterApplied) count++;
        if (isSystemStatusFilterApplied) count++;
        if (isWorkflowTypeFilterApplied) count++;

        filtersCounterLiveData.setValue(count);
    }

    protected void initWorkflowList(SharedPreferences sharedPreferences,
                                    LifecycleOwner lifecycleOwner) {
        if (!TextUtils.isEmpty(token)) {
//            showLoading.setValue(true);
            int baseFilterId = filterSettings.getBaseFilterSelectedId();
            loadWorkflowsByFilters(filterSettings, lifecycleOwner);
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
        initStatusFilters();
        initSystemStatusFilters();
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

    public static final int STATUS_FILTER_TYPE = -99;
    public static final int STATUS_FILTER_ALL_ID = 95;
    public static final int STATUS_FILTER_OPEN_ID = 96;
    public static final int STATUS_FILTER_CLOSED_ID = 97;

    /**
     * Creates a new list of status filter options only if FilterSettings.statusFilterOptionList is
     * empty.
     */
    private void initStatusFilters() {
        if (filterSettings.getSizeStatusFilterOptionList() > 0) {
            // It already exists.
            return;
        }

        List<WorkflowTypeMenu> statusFilterOptionList = new ArrayList<>();
        WorkflowTypeMenu menu = new WorkflowTypeMenu(
                STATUS_FILTER_ALL_ID,
                R.string.all,
                WorkflowTypeSpinnerAdapter.TYPE,
                STATUS_FILTER_TYPE
        );
        statusFilterOptionList.add(menu);

        menu = new WorkflowTypeMenu(
                STATUS_FILTER_OPEN_ID,
                R.string.open,
                WorkflowTypeSpinnerAdapter.TYPE,
                STATUS_FILTER_TYPE
        );
        menu.setSelected(true);
        statusFilterOptionList.add(menu);

        menu = new WorkflowTypeMenu(
                STATUS_FILTER_CLOSED_ID,
                R.string.closed,
                WorkflowTypeSpinnerAdapter.TYPE,
                STATUS_FILTER_TYPE
        );
        statusFilterOptionList.add(menu);

        filterSettings.setStatusFilterOptionList(statusFilterOptionList);
    }

    public static final int SYSTEM_STATUS_FILTER_TYPE = -110;
    public static final int SYSTEM_STATUS_FILTER_ALL_ID = 111;
    public static final int SYSTEM_STATUS_FILTER_ACTIVE_ID = 112;
    public static final int SYSTEM_STATUS_FILTER_INACTIVE__ID = 113;

    /**
     * Creates a new list of system status filter options only if FilterSettings.systemStatusFilterOptionList
     * is empty.
     */
    private void initSystemStatusFilters() {
        if (filterSettings.getSizeSystemStatusFilterOptionList() > 0) {
            // It already exists.
            return;
        }

        List<WorkflowTypeMenu> systemStatusFilterOptionList = new ArrayList<>();
        WorkflowTypeMenu menu = new WorkflowTypeMenu(
                SYSTEM_STATUS_FILTER_ALL_ID,
                R.string.all,
                WorkflowTypeSpinnerAdapter.TYPE,
                SYSTEM_STATUS_FILTER_TYPE
        );
        systemStatusFilterOptionList.add(menu);

        menu = new WorkflowTypeMenu(
                SYSTEM_STATUS_FILTER_ACTIVE_ID,
                R.string.active,
                WorkflowTypeSpinnerAdapter.TYPE,
                SYSTEM_STATUS_FILTER_TYPE
        );
        menu.setSelected(true);
        systemStatusFilterOptionList.add(menu);

        menu = new WorkflowTypeMenu(
                SYSTEM_STATUS_FILTER_INACTIVE__ID,
                R.string.inactive,
                WorkflowTypeSpinnerAdapter.TYPE,
                SYSTEM_STATUS_FILTER_TYPE
        );
        systemStatusFilterOptionList.add(menu);

        filterSettings.setSystemStatusFilterOptionList(systemStatusFilterOptionList);
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
        if (filterSettings.getSizeWorkflowTypeOptionList() > 0) {
            //already exists
            return;
        }

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

        filterSettings.setWorkflowTypeOptionsList(spinnerMenuArray);
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

    /**
     * Request for filtered data to workflow repository. This function handles all the different
     * filtering scenarios. It handles cases with base filters, meta data, workflow type filters.
     *
     * @param filterSettings Instance of FilterSettings.
     * @param lifecycleOwner Fragment with observer that we need to remove.
     */
    private void loadWorkflowsByFilters(FilterSettings filterSettings,
                                        LifecycleOwner lifecycleOwner) {
        int workflowTypeSelected = filterSettings.getWorkflowTypeId();

        Map<String, Object> options = new ArrayMap<>();
        isWorkflowTypeFilterApplied = workflowTypeSelected != NO_TYPE_SELECTED;
        if (isWorkflowTypeFilterApplied) {
            options.put("workflow_type_id", workflowTypeSelected);
        }

        switch (filterSettings.getBaseFilterSelectedId()) {
            case BASE_FILTER_ALL_ID:
                // Nothing to do add to options.
                isBaseFilterApplied = false;
                break;
            case BASE_FILTER_OUT_OF_TIME_ID:
                isBaseFilterApplied = true;
                options.put("out_of_time", true);
                break;
            case BASE_FILTER_ON_TIME_ID:
                isBaseFilterApplied = true;
                options.put("out_of_time", false);
                break;
            case BASE_FILTER_PENDING_BY_ME_ID:
                isBaseFilterApplied = true;
                options.put("responsible_id", userId);
                break;
            case BASE_FILTER_LATEST_ID:
                isBaseFilterApplied = true;
                options.put("latest", true);
                break;
        }

        switch (filterSettings.getStatusFilterSelectedId()) {
            case STATUS_FILTER_ALL_ID:
                // Nothing to do add to options.
                isShowOpenActionMenu = true;
                isShowCloseActionMenu = true;
                isStatusFilterApplied = true;
                break;
            case STATUS_FILTER_OPEN_ID:
                options.put("open", true);
                isShowOpenActionMenu = false;
                isShowCloseActionMenu = true;
                isStatusFilterApplied = false;
                break;
            case STATUS_FILTER_CLOSED_ID:
                options.put("open", false);
                isShowOpenActionMenu = true;
                isShowCloseActionMenu = false;
                isStatusFilterApplied = true;
                break;
        }

        Boolean systemStatusFilterValue = null;
        String systemStatusFilterKey = String.valueOf(TAG_STANDARD_ACTIVE);
        switch (filterSettings.getSystemStatusFilterSelectedId()) {
            case SYSTEM_STATUS_FILTER_ALL_ID:
                systemStatusFilterValue = null;
                filterSettings.setCheckedStatus(true);
                isShowEnableActionMenu = true;
                isShowDisableActionMenu = true;
                isSystemStatusFilterApplied = true;
                break;
            case SYSTEM_STATUS_FILTER_ACTIVE_ID:
                systemStatusFilterValue = true;
                filterSettings.setCheckedStatus(true);
                isShowEnableActionMenu = false;
                isShowDisableActionMenu = true;
                isSystemStatusFilterApplied = false;
                break;
            case SYSTEM_STATUS_FILTER_INACTIVE__ID:
                systemStatusFilterValue = false;
                filterSettings.setCheckedStatus(false);
                isShowEnableActionMenu = true;
                isShowDisableActionMenu = false;
                isSystemStatusFilterApplied = true;
                break;
        }

        Map<String, Object> dynamicFiltersMap = new HashMap<>(filterSettings.getDynamicFilters());

        if (systemStatusFilterValue != null) {
            dynamicFiltersMap.put(systemStatusFilterKey, systemStatusFilterValue);
        } else {
            dynamicFiltersMap.remove(systemStatusFilterKey);
        }

        if (!dynamicFiltersMap.isEmpty()) {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Map<String, Object>> jsonAdapter = moshi
                    .adapter(Types.newParameterizedType(Map.class, String.class, Object.class));
            String dynamicFiltersString = jsonAdapter.toJson(dynamicFiltersMap);
            if (!TextUtils.isEmpty(dynamicFiltersString)) {
                options.put("workflow_metadata", dynamicFiltersString);
            }
        }

        workflowRepository.getWorkflowsByBaseFilters(token, options);
        liveWorkflows.removeObservers(lifecycleOwner);
        sendFiltersCounterToUi();
    }

    protected void handleWorkflowTypeFieldClick() {
        OptionsList optionsList = filterSettings.handleOptionListForWorkflowTypeFilters();
        messageMainWorkflowTypeFilters.setValue(optionsList);
    }

    protected void handleBaseFieldClick() {
        OptionsList optionsList = filterSettings.handleOptionListForBaseFilters();
        messageMainBaseFilters.setValue(optionsList);
    }

    protected void handleStatusFieldClick() {
        OptionsList optionsList = filterSettings.handleOptionListForStatusFilters();
        messageMainStatusFilters.setValue(optionsList);
    }

    protected void handleSystemStatusFieldClick() {
        OptionsList optionsList = filterSettings.handleOptionListForSystemStatusFilters();
        messageMainSystemStatusFilters.setValue(optionsList);
    }

    /**
     * Handles a position tapped on the List of WorkflowTypeFilters. This function will update
     * FilterSettings with the latest position selected and make a network call to update the
     * database with the selected base filter.
     *
     * @param position       Selection done by the user on the list of workflow type filters.
     * @param lifecycleOwner Fragment has an observer that we need to remove and use a new one.
     */
    protected void handleWorkflowTypeFieldPositionSelected(int position,
                                                           LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        WorkflowTypeMenu filterSelected;
        filterSelected = filterSettings.handleWorkflowTypeFilterPositionSelected(position);
        if (!filterSelected.isSelected()) {
            // Unselected, no items selected. Filter by All.
            filterSettings.resetWorkflowTypeFilterSelectionToAll();
            filterSelected = filterSettings.getWorkflowTypeOptionsList().get(0);
            filterSettings.setWorkflowTypeId(filterSelected.getWorkflowTypeId());
        }
        messageMainWorkflowTypeFilterSelectionToFilterList.setValue(filterSelected.getLabel());
        messageMainWorkflowTypeIdFilterSelectionToFilterList
                .setValue(filterSelected.getWorkflowTypeId());
        invalidateDrawerOptionsList.setValue(true);
        handleFilterByWorkflowType(filterSelected, lifecycleOwner);
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
        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
    }

    /**
     * Handles a position tapped on the List of StatusFilters. This function will update
     * FilterSettings with the latest position selected and make a network call to update the
     * database with the selected base filter.
     *
     * @param position       Selection done by the user on the list of status filters.
     * @param lifecycleOwner Fragment has an observer that we need to remove and use a new one.
     */
    protected void handleStatusFieldPositionSelected(int position, LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        WorkflowTypeMenu filterSelected;
        filterSelected = filterSettings.handleStatusFilterPositionSelected(position);
        int filterId = filterSelected.getId();
        if (!filterSelected.isSelected()) {
            // Unselected, no items selected. Filter by Open.
            filterSettings.resetStatusFilterSelectionToOpen();
            filterId = STATUS_FILTER_OPEN_ID;
            messageMainStatusFilterSelectionToFilterList.setValue(R.string.open);
        } else {
            messageMainStatusFilterSelectionToFilterList.setValue(filterSelected.getResLabel());
        }
        invalidateDrawerOptionsList.setValue(true);
        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
    }

    /**
     * Handles a position tapped on the List of SystemStatusFilters. This function will update
     * FilterSettings with the latest position selected and make a network call to update the
     * database with the selected base filter.
     *
     * @param position       Selection done by the user on the list of system status filters.
     * @param lifecycleOwner Fragment has an observer that we need to remove and use a new one.
     */
    protected void handleSystemStatusFieldPositionSelected(int position,
                                                           LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        WorkflowTypeMenu filterSelected;
        filterSelected = filterSettings.handleSystemStatusFilterPositionSelected(position);
        int filterId = filterSelected.getId();
        if (!filterSelected.isSelected()) {
            // Unselected, no items selected. Filter by Active.
            filterSettings.resetSystemStatusFilterSelectionToActive();
            filterId = STATUS_FILTER_OPEN_ID;
            messageMainSystemStatusFilterSelectionToFilterList.setValue(R.string.active);
        } else {
            messageMainSystemStatusFilterSelectionToFilterList
                    .setValue(filterSelected.getResLabel());
        }
        invalidateDrawerOptionsList.setValue(true);
        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
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
//        updateSelectedMenuItem(menu);
        filterSettings.updateFilterListItemSelected(menu);

        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
    }

    protected void handleDynamicFilterSelected(DynamicFilter dynamicFilter,
                                               LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);
        filterSettings.addDynamicFilter(dynamicFilter.getKey(), dynamicFilter.getValue());
        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
    }

    protected void handleDynamicFilterListSelected(List<DynamicFilter> dynamicFilters,
                                                   LifecycleOwner lifecycleOwner) {
        showLoading.setValue(true);

        dynamicFilters.forEach(dynamicFilter -> filterSettings
                .addDynamicFilter(dynamicFilter.getKey(), dynamicFilter.getValue()));

        loadWorkflowsByFilters(filterSettings, lifecycleOwner);
    }

    private void handleFilterByWorkflowType(WorkflowTypeMenu menu, LifecycleOwner lifecycleOwner) {
        int workflowTypeId = menu.getWorkflowTypeId();
        if (filterSettings.isTypeAlreadySelected(workflowTypeId)) {
            // Update FilterSettings and Right Drawer UI.
            filterSettings.setWorkflowTypeId(NO_TYPE_SELECTED);
            sendMessageUpdateSelectionToWorkflowList(Sort.sortType.NONE);

            filterSettings.updateWorkflowTypeListFilterItem(null);

            showLoading.setValue(false);
            applyFilters(lifecycleOwner, filterSettings);
            return;
        }

        // Choosing some workflow type from the filter Workflow Type.
        // Update Filter Settings with new workflow type id.
        filterSettings.setWorkflowTypeId(workflowTypeId);
        loadWorkflowsByFilters(filterSettings, lifecycleOwner);

        // Filter List Update with new selection
        filterSettings.updateWorkflowTypeListFilterItem(menu);

        filterSettings.updateFilterListItemSelected(menu);

        // Allowing single selection on the UI for this list.
        invalidateDrawerOptionsList.setValue(true);

        // TODO double check if we need this removeObservers() we call already in loadWorkflowByType() something similar.
        liveWorkflows.removeObservers(lifecycleOwner);
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
        applyFilters(lifecycleOwner, filterSettings);
    }

    private void subscribe(LifecycleOwner lifecycleOwner) {
        final Observer<Boolean> handleRepoErrorObserver = (error -> {
            showLoading.postValue(false);
        });
        final Observer<Boolean> handleRepoSuccessObserver = (success -> {
            showLoading.postValue(false);
            // TODO change data source to new query pointing ONLY to my profile id.
            applyFilters(lifecycleOwner, filterSettings, userId);
        });
        final Observer<Boolean> handleRepoSuccessNoFilterObserver = (success -> {
            showLoading.postValue(false);
            applyFilters(lifecycleOwner, filterSettings);
        });

        final Observer<Boolean> handleRestSuccessWithNoApplyFilter = (success -> showLoading
                .postValue(false));

        final Observer<Boolean> handleDeleteWorkflows = (success -> completeMassAction
                .setValue(success));

        final Observer<Boolean> handleRefreshWorkflows = (success -> completeMassAction
                .setValue(success));

        workflowRepository.getObservableHandleRepoError().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoError()
                .observe(lifecycleOwner, handleRepoErrorObserver);

        workflowRepository.getObservableHandleRepoSuccess().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoSuccess()
                .observe(lifecycleOwner, handleRepoSuccessObserver);

        workflowRepository.getObservableHandleRepoSuccessNoFilter().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRepoSuccessNoFilter()
                .observe(lifecycleOwner, handleRepoSuccessNoFilterObserver);

        workflowRepository.getObservableHandleRestSuccessWithNoApplyFilter()
                .removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleRestSuccessWithNoApplyFilter()
                .observe(lifecycleOwner, handleRestSuccessWithNoApplyFilter);

        workflowRepository.getObservableHandleDeleteWorkflows().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleDeleteWorkflows()
                .observe(lifecycleOwner, handleDeleteWorkflows);

        workflowRepository.getObservableHandleGetAllWorkflows().removeObservers(lifecycleOwner);
        workflowRepository.getObservableHandleGetAllWorkflows()
                .observe(lifecycleOwner, handleRefreshWorkflows);
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

    protected void applyFilters(LifecycleOwner lifecycleOwner) {
        if (filterSettings == null) {
            return;
        }
        applyFilters(lifecycleOwner, filterSettings);
    }

    private void applyFilters(LifecycleOwner lifecycleOwner, FilterSettings filterSettings) {
        applyFilters(lifecycleOwner, filterSettings, "");
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
    private void applyFilters(LifecycleOwner lifecycleOwner, FilterSettings filterSettings,
                              String id) {
        switch (sort.getSortingType()) {
            case NONE: {
                isSortFilterApplied = false;
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
                isScrollToTop = true;
                reloadWorkflowsList(lifecycleOwner);
                break;
            }
            case BYNUMBER: {
                isSortFilterApplied = true;
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
                isScrollToTop = true;
                reloadWorkflowsList(lifecycleOwner);
                break;
            }
            case BYCREATE: {
                isSortFilterApplied = true;
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
                isScrollToTop = true;
                reloadWorkflowsList(lifecycleOwner);
                break;
            }
            case BYUPDATE: {
                isSortFilterApplied = true;
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
                isScrollToTop = true;
                reloadWorkflowsList(lifecycleOwner);
                break;
            }
        }

        sendFiltersCounterToUi();
    }

    public void reloadWorkflowsList(LifecycleOwner lifecycleOwner) {
        reloadWorkflowsList(lifecycleOwner, false);
    }

    public void reloadWorkflowsList(LifecycleOwner lifecycleOwner, boolean isSwipe) {
        this.isSwipe = isSwipe;
        if (!isSwipe) showLoading.setValue(true);

        liveWorkflows.removeObservers(
                lifecycleOwner); // TODO try putting this back and maybe it works and delete from handleWorkflowTypeFilters first line.
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
        showLoading.setValue(false);
        updateWithSortedList.setValue(listWorkflows);
        if (isSwipe != null && isSwipe) {
            isSwipe = null; //clear value
        }

        if (isScrollToTop) {
            isScrollToTop = false;
            handleScrollRecyclerToTop.setValue(true);
        }

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

    private void onOpenCloseWorkflowsSuccess(
            WorkflowActivationResponse workflowActivationResponse) {
        if (workflowActivationResponse.getData() == null
                || workflowActivationResponse.getData().isEmpty()) {
            completeMassAction.setValue(true);
            return;
        }

        workflowRepository.deleteWorkflowsLocal(workflowActivationResponse.getData().get(0));
    }

    protected void enableDisableWorkflows(List<Integer> workflowIds, boolean enable) {
        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .postEnableDisableActivation(token, workflowIds, enable)
                .subscribe(this::onEnableDisableWorkflowsSuccess, this::onFailure);

        disposables.add(disposable);
    }

    private void onEnableDisableWorkflowsSuccess(
            WorkflowActivationResponse workflowActivationResponse) {
        if (workflowActivationResponse.getData() == null
                || workflowActivationResponse.getData().isEmpty()) {
            completeMassAction.setValue(true);
            return;
        }

        workflowRepository.deleteWorkflowsLocal(workflowActivationResponse.getData().get(0));
    }

    protected void deleteWorkflows(List<Integer> workflowIds) {
        if (workflowIds.isEmpty()) return;

        mWorkflowIdsToDelete = workflowIds;

        PostDeleteWorkflows postDeleteWorkflows = new PostDeleteWorkflows();
        postDeleteWorkflows.setWorkflowsArray(workflowIds);

        showLoading.setValue(true);
        Disposable disposable = workflowRepository
                .postDeleteWorkflows(token, workflowIds.get(0), postDeleteWorkflows)
                .subscribe(this::onDeleteWorkflowsSuccess, this::onFailure);

        disposables.add(disposable);
    }

    private void onDeleteWorkflowsSuccess(DeleteWorkflowResponse deleteWorkflowResponse) {
        if (deleteWorkflowResponse.getCode() != 200 || mWorkflowIdsToDelete == null
                || mWorkflowIdsToDelete.isEmpty()) {
            completeMassAction.setValue(true);
            return;
        }

        workflowRepository.deleteWorkflowsLocalByIds(mWorkflowIdsToDelete);
        mWorkflowIdsToDelete = null;
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

    public LiveData<Boolean> getObservableShowList() {
        if (showList == null) {
            showList = new MutableLiveData<>();
        }
        return showList;
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

    protected LiveData<Boolean> getObservableHandleScrollRecyclerToTop() {
        if (handleScrollRecyclerToTop == null) {
            handleScrollRecyclerToTop = new MutableLiveData<>();
        }
        return handleScrollRecyclerToTop;
    }

    protected LiveData<Boolean> getObservableShowBulkActionMenu() {
        if (showBulkActionMenuLiveData == null) {
            showBulkActionMenuLiveData = new MutableLiveData<>();
        }
        return showBulkActionMenuLiveData;
    }

    protected LiveData<Integer> getObservableFiltersCounter() {
        if (filtersCounterLiveData == null) {
            filtersCounterLiveData = new MutableLiveData<>();
        }
        return filtersCounterLiveData;
    }
}
