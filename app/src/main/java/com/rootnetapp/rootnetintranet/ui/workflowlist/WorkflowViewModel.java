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
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListItem;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowTypeSpinnerAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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



    protected MutableLiveData<List<WorkflowTypeMenu>> rightDrawerFilterMenus;
    protected MutableLiveData<List<WorkflowTypeMenu>> rightDrawerOptionMenus;
    protected List<WorkflowTypeMenu> rightDrawerFilters;


//

    private WorkflowRepository workflowRepository;
    private List<WorkflowDb> workflows, unordered;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private Sort sort;
    private FilterBoxSettings filterBoxSettings;
    private String token;
    private String userId;
    private int categoryId;
    private List<ListItem> categoryList;
    private static final String TAG = "WorkflowViewModel";

    public static final int NO_TYPE_SELECTED = 0;

    public WorkflowViewModel(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
        sort = new Sort();
        getWorkflowTypesFromDb();
        filterBoxSettings = new FilterBoxSettings();
        showBottomSheetLoading = new MutableLiveData<>();
        clearFilters = new MutableLiveData<>();
        workflowTypeMenuItems = new MutableLiveData<>();
        rightDrawerFilterMenus = new MutableLiveData<>();
        rightDrawerOptionMenus = new MutableLiveData<>();
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
        int id = filterBoxSettings.getWorkflowTypeId();
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
                "NO SELECTION",
                WorkflowTypeSpinnerAdapter.NO_SELECTION
        );
        spinnerMenuArray.add(0, noSelection);
    }

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

    protected void loadMyPendingWorkflows(boolean isChecked, LifecycleOwner lifecycleOwner) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        showLoading.postValue(true);
        if (isChecked) {
            filterBoxSettings.setCheckedMyPending(true);
            int id = Integer.valueOf(userId);
            workflowRepository.getMyPendingWorkflows(id, token);

        } else {
            filterBoxSettings.setCheckedMyPending(false);
            workflowRepository.getAllWorkflowsNoFilters(token);
        }
        liveWorkflows.removeObservers(lifecycleOwner);
        // liveWorkflows' Observer that was removed will be put back in one of the observers in subscribe.
    }

    protected void filterBySearchText(String searchText, LifecycleOwner lifecycleOwner) {
        filterBoxSettings.setSearchText(searchText);
        liveWorkflows.removeObservers(lifecycleOwner);
        applyFilters(filterBoxSettings);
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
            applyFilters(filterBoxSettings, userId);
        });
        final Observer<Boolean> handleRepoSuccessNoFilterObserver = ( success -> {
           showLoading.postValue(false);
           applyFilters(filterBoxSettings);
        });

        workflowRepository.getObservableHandleRepoError().observe(lifecycleOwner, handleRepoErrorObserver);
        workflowRepository.getObservableHandleRepoSuccess().observe(lifecycleOwner, handleRepoSuccessObserver);
        workflowRepository.getObservableHandleRepoSuccessNoFilter().observe(lifecycleOwner, handleRepoSuccessNoFilterObserver);
    }

    protected LiveData<Boolean> getObservableLoadMore() {
        return workflowRepository.showLoadMore;
    }

    private void updateFilterBoxSettings(int workflowTypeId, int typeIdPositionInArray, boolean isCheckedMyPendings, boolean isCheckedStatus) {
        filterBoxSettings.setCheckedMyPending(isCheckedMyPendings);
        filterBoxSettings.setWorkflowTypeId(workflowTypeId);
        filterBoxSettings.setCheckedStatus(isCheckedStatus);
        filterBoxSettings.setTypeIdPositionInArray(typeIdPositionInArray);
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
        applyFilters(filterBoxSettings);
    }

    private void applyFilters(FilterBoxSettings filterBoxSettings) {
        applyFilters(filterBoxSettings, "");
    }

    private void applyFilters(FilterBoxSettings filterBoxSettings, String id) {
        switch (sort.getSortingType()) {
            case NONE: {
                if (filterBoxSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                } else {
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            filterBoxSettings.getWorkflowTypeId(),
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYNUMBER: {
                if (filterBoxSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getNumberSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOWID,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getNumberSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            filterBoxSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOWID,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYCREATE: {
                if (filterBoxSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOW_CREATED,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getCreatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            filterBoxSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOW_CREATED,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                }
                reloadWorkflowsList();
                break;
            }
            case BYUPDATE: {
                if (filterBoxSettings.getWorkflowTypeId() == NO_TYPE_SELECTED) {
                    boolean isDescending = !sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            WorkflowRepository.WORKFLOW_UPDATED,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
                } else {
                    boolean isDescending = !sort.getUpdatedSortOrder().equals(Sort.sortOrder.ASC);
                    workflowRepository.rawQueryWorkflowListByFilters(
                            filterBoxSettings.isCheckedStatus(),
                            filterBoxSettings.getWorkflowTypeId(),
                            WorkflowRepository.WORKFLOW_UPDATED,
                            isDescending,
                            token,
                            id,
                            filterBoxSettings.getSearchText());
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

        if (filterBoxSettings.isCheckedMyPending()) {
            toggleFilterSwitch(WorkflowFragment.SWITCH_PENDING, WorkflowFragment.CHECK);
        } else {
            toggleFilterSwitch(WorkflowFragment.SWITCH_PENDING, WorkflowFragment.UNCHECK);
        }
        if (filterBoxSettings.isCheckedStatus()) {
            toggleFilterSwitch(WorkflowFragment.SWITCH_STATUS, WorkflowFragment.CHECK);
        } else {
            toggleFilterSwitch(WorkflowFragment.SWITCH_STATUS, WorkflowFragment.UNCHECK);
        }
        setSelectType.postValue(filterBoxSettings.getTypeIdPositionInArray());
    }



    private void initRightDrawerFilterList() {
        rightDrawerFilters = new ArrayList<>();

        WorkflowTypeMenu menuItem = new WorkflowTypeMenu(
                "Tipo De Workflow",
                "Todos los tipos",
                RightDrawerFiltersAdapter.TYPE,
                0
        );

        rightDrawerFilters.add(menuItem);

        menuItem = new WorkflowTypeMenu(
                "Filtros",
                "Todos",
                RightDrawerFiltersAdapter.TYPE,
                0
        );
        rightDrawerFilters.add(menuItem);


        rightDrawerFilterMenus.setValue(rightDrawerFilters);

    }

    private static final int WORKFLOW_TYPE = 0;
    protected void handleSelectedItemInFilters(int position) {

        if (position == WORKFLOW_TYPE) {
            rightDrawerOptionMenus.setValue(spinnerMenuArray);
        }
    }

    protected void handleRightDrawerBackAction() {
        if (rightDrawerFilters == null) {
            return;
        }
        rightDrawerFilterMenus.setValue(rightDrawerFilters);
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
        toggleRadioButton.setValue(toggleRadio);
    }

    private void toggleSwitch(int viewSwitchType, int viewIsCheckType) {
        int[] toggleRadio = new int[2];
        toggleRadio[WorkflowFragment.INDEX_TYPE] = viewSwitchType;
        toggleRadio[WorkflowFragment.INDEX_CHECK] = viewIsCheckType;
        toggleSwitch.setValue(toggleRadio);
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
