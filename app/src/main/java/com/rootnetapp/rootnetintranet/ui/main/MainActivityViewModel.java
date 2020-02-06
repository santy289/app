package com.rootnetapp.rootnetintranet.ui.main;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.auth0.android.jwt.JWT;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.AutocompleteFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseOption;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BooleanFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.MultipleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.TextInputFormItem;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerSortSwitchAction;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.services.websocket.RestartWebsocketReceiver;
import com.rootnetapp.rootnetintranet.ui.workflowlist.DynamicFilter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.Sort;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_COMMENT_CRUD_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_COMMENT_CRUD_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_STATUS_UPDATE_ALL;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.UNCHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.IS_CHECKED_INDEX;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.VIEW_ID_INDEX;

public class MainActivityViewModel extends ViewModel {

    private MainActivityRepository repository;
    private MutableLiveData<Cursor> mWorkflowsLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mWorkfErrorLiveData;
    private MutableLiveData<String[]> setImgInView;
    private MutableLiveData<Boolean> collapseMenu;
    private MutableLiveData<Boolean> hideKeyboard;
    private MutableLiveData<Workflow> goToWorkflowDetail;
    private MutableLiveData<Boolean> attemptTokenRefresh;
    private MutableLiveData<String> saveToPreference;
    private MutableLiveData<Boolean> goToDomain;
    private MutableLiveData<Boolean> startService;
    private MutableLiveData<Boolean> stopService;
    private MutableLiveData<QuickActionsVisibility> quickActionsVisibilityLiveData;
    protected MutableLiveData<Integer> setSearchMenuLayout;
    protected MutableLiveData<Integer> setUploadMenuLayout;
    protected MutableLiveData<List<WorkflowTypeMenu>> setRightDrawerFilterList;
    protected MutableLiveData<OptionsList> setRightDrawerOptionList;
    protected MutableLiveData<Boolean> openRightDrawer;

    // Receive message from Workflow List
    protected MutableLiveData<int[]> receiveMessageToggleRadioButton;
    protected MutableLiveData<int[]> receiveMessageToggleSwitch;
    protected MutableLiveData<Integer> receiveMessageUpdateSortSelected;
    protected MutableLiveData<OptionsList> receiveMessageCreateWorkflowTypeFiltersAdapter;
    protected MutableLiveData<OptionsList> receiveMessageCreateBaseFiltersAdapter;
    protected MutableLiveData<OptionsList> receiveMessageCreateStatusFiltersAdapter;
    protected MutableLiveData<OptionsList> receiveMessageCreateSystemStatusFiltersAdapter;
    protected MutableLiveData<Integer> receiveMessageWorkflowTypeIdFilterSelected;
    protected MutableLiveData<String> receiveMessageWorkflowTypeFilterSelected;
    protected MutableLiveData<Integer> receiveMessageBaseFilterSelected;
    protected MutableLiveData<Integer> receiveMessageStatusFilterSelected;
    protected MutableLiveData<Integer> receiveMessageSystemStatusFilterSelected;

    // send message to WorkflowList
    public MutableLiveData<Integer> messageContainerToWorkflowList;
    public MutableLiveData<Integer> messageOptionSelectedToWorkflowList;
    public MutableLiveData<DynamicFilter> messageDynamicFilterSelectedToWorkflowList;
    public MutableLiveData<List<DynamicFilter>> messageDynamicFilterListSelectedToWorkflowList;
    public MutableLiveData<Boolean> messageBackActionToWorkflowList;
    public MutableLiveData<Boolean> messageInitSortByToWorkflowList;
    public MutableLiveData<int[]> messageRadioButtonClickedToWorkflowList;
    public MutableLiveData<RightDrawerSortSwitchAction> messageSortSwitchActionToWorkflowList;
    public MutableLiveData<Boolean> messageBaseFiltersClickedToWorkflowList;
    public MutableLiveData<Boolean> messageWorkflowTypeFilterClickedToWorkflowList;
    public MutableLiveData<Boolean> messageStatusFiltersClickedToWorkflowList;
    public MutableLiveData<Boolean> messageSystemStatusFiltersClickedToWorkflowList;
    public MutableLiveData<Boolean> messageClearFiltersClickedToWorkflowList;
    public MutableLiveData<Integer> messageWorkflowTypeFilterPositionSelectedToWorkflowList;
    public MutableLiveData<Integer> messageBaseFilterPositionSelectedToWorkflowList;
    public MutableLiveData<Integer> messageStatusFilterPositionSelectedToWorkflowList;
    public MutableLiveData<Integer> messageSystemStatusFilterPositionSelectedToWorkflowList;
    public MutableLiveData<Boolean> invalidateOptionsList;

    List<WorkflowTypeMenu> filtersList;
    List<WorkflowTypeMenu> optionsList;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected final static String IMG_LOGO = "imgLogo";
    protected final static String IMG_BAR_LOGO = "imgBarLogo";
    protected final static String IMG_TOOLBAR = "imgToolbar";
    private final static String TAG = "MainActivityViewModel";

    public MainActivityViewModel(MainActivityRepository repository) {
        this.repository = repository;
        this.setSearchMenuLayout = new MutableLiveData<>();
        this.setRightDrawerFilterList = new MutableLiveData<>();
        this.setRightDrawerOptionList = new MutableLiveData<>();
        this.messageContainerToWorkflowList = new MutableLiveData<>();
        this.messageBackActionToWorkflowList = new MutableLiveData<>();
        this.messageOptionSelectedToWorkflowList = new MutableLiveData<>();
        this.messageDynamicFilterSelectedToWorkflowList = new MutableLiveData<>();
        this.messageDynamicFilterListSelectedToWorkflowList = new MutableLiveData<>();
        this.invalidateOptionsList = new MutableLiveData<>();
        this.messageInitSortByToWorkflowList = new MutableLiveData<>();
        this.receiveMessageToggleRadioButton = new MutableLiveData<>();
        this.receiveMessageToggleSwitch = new MutableLiveData<>();
        this.receiveMessageUpdateSortSelected = new MutableLiveData<>();
        this.messageRadioButtonClickedToWorkflowList = new MutableLiveData<>();
        this.messageSortSwitchActionToWorkflowList = new MutableLiveData<>();
        this.messageBaseFiltersClickedToWorkflowList = new MutableLiveData<>();
        this.messageWorkflowTypeFilterClickedToWorkflowList = new MutableLiveData<>();
        this.messageStatusFiltersClickedToWorkflowList = new MutableLiveData<>();
        this.messageSystemStatusFiltersClickedToWorkflowList = new MutableLiveData<>();
        this.messageClearFiltersClickedToWorkflowList = new MutableLiveData<>();
        this.receiveMessageCreateWorkflowTypeFiltersAdapter = new MutableLiveData<>();
        this.receiveMessageCreateBaseFiltersAdapter = new MutableLiveData<>();
        this.receiveMessageCreateStatusFiltersAdapter = new MutableLiveData<>();
        this.receiveMessageCreateSystemStatusFiltersAdapter = new MutableLiveData<>();
        this.messageWorkflowTypeFilterPositionSelectedToWorkflowList = new MutableLiveData<>();
        this.messageBaseFilterPositionSelectedToWorkflowList = new MutableLiveData<>();
        this.messageStatusFilterPositionSelectedToWorkflowList = new MutableLiveData<>();
        this.messageSystemStatusFilterPositionSelectedToWorkflowList = new MutableLiveData<>();
        this.receiveMessageWorkflowTypeIdFilterSelected = new MutableLiveData<>();
        this.receiveMessageWorkflowTypeFilterSelected = new MutableLiveData<>();
        this.receiveMessageBaseFilterSelected = new MutableLiveData<>();
        this.receiveMessageStatusFilterSelected = new MutableLiveData<>();
        this.receiveMessageSystemStatusFilterSelected = new MutableLiveData<>();
        this.openRightDrawer = new MutableLiveData<>();
        this.startService = new MutableLiveData<>();
        this.stopService = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void initMainViewModel(SharedPreferences sharedPreferences) {
        if (!RestartWebsocketReceiver.getRunningIndicator()) {
            startService.setValue(true);
        }

        String json = sharedPreferences.getString(PreferenceKeys.PREF_DOMAIN, "");
        if (json.isEmpty()) {
            Log.d("test", "onCreate: ALGO PASO");//todo mejorar esta validacion
            return;
        }

        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
            ClientResponse domain;
            domain = jsonAdapter.fromJson(json);
            Utils.domain = Utils.getWebProtocol(domain.getClient().getApiUrl()) + domain.getClient()
                    .getApiUrl();
            Utils.setImgDomain(domain.getClient().getApiUrl());
            String[] content = new String[2];
            content[0] = MainActivityViewModel.IMG_LOGO;
            content[1] = Utils.URL + domain.getClient().getLogoUrl();
            setImgInView.setValue(content);
            RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
        } catch (IOException e) {
            Log.d(TAG, "initMainViewModel: error: " + e.getMessage());
        }

        String token = sharedPreferences.getString(PreferenceKeys.PREF_TOKEN, "");
        JWT jwt = new JWT(token);
        int id = Integer.parseInt(jwt.getClaim(PreferenceKeys.PREF_PROFILE_ID).asString());
        getUser(id);

        String permissionsString = sharedPreferences
                .getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");
        checkPermissions(permissionsString);
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link MainActivity}. Hide
     * the UI related to the unauthorized actions.
     *
     * @param permissionsString users permissions.
     */
    private void checkPermissions(String permissionsString) {
        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        String[] permissionsToCheck = {
                WORKFLOW_COMMENT_CRUD_OWN,
                WORKFLOW_COMMENT_CRUD_ALL,
                WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED
        };
        boolean hasCommentPermissions = permissionsUtils.hasPermissions(permissionsToCheck);
        boolean hasChangeStatusPermissions = permissionsUtils
                .hasPermission(WORKFLOW_STATUS_UPDATE_ALL);
        boolean hasEditPermissions = permissionsUtils.hasPermission(WORKFLOW_EDIT_OWN);

        QuickActionsVisibility quickActionsVisibility = new QuickActionsVisibility();
        quickActionsVisibility.setShowComment(hasCommentPermissions);
        quickActionsVisibility.setShowChangeStatus(hasChangeStatusPermissions);
        quickActionsVisibility.setShowEdit(hasEditPermissions);
        quickActionsVisibility.setShowApprove(true);

        quickActionsVisibilityLiveData.setValue(quickActionsVisibility);
    }

    protected void sendFilterClickToWorflowList(int position) {
        messageContainerToWorkflowList.setValue(position);
    }

    protected void sendOptionSelectedToWorkflowList(int position) {
        messageOptionSelectedToWorkflowList.setValue(position);
    }

    protected void sendDynamicFilterSelectedToWorkflowList(DynamicFilter dynamicFilter) {
        messageDynamicFilterSelectedToWorkflowList.setValue(dynamicFilter);
    }

    protected void sendDynamicFilterListSelectedToWorkflowList(
            List<DynamicFilter> dynamicFilterList) {
        messageDynamicFilterListSelectedToWorkflowList.setValue(dynamicFilterList);
    }

    protected void sendRightDrawerBackButtonClick() {
        messageBackActionToWorkflowList.setValue(true);
    }

    protected void sendBaseFiltersClicked() {
        messageBaseFiltersClickedToWorkflowList.setValue(true);
    }

    protected void sendWorkflowTypeFilterClicked() {
        messageWorkflowTypeFilterClickedToWorkflowList.setValue(true);
    }

    protected void sendStatusFiltersClicked() {
        messageStatusFiltersClickedToWorkflowList.setValue(true);
    }

    protected void sendSystemStatusFiltersClicked() {
        messageSystemStatusFiltersClickedToWorkflowList.setValue(true);
    }

    protected void sendClearFiltersClicked() {
        messageClearFiltersClickedToWorkflowList.setValue(true);
    }

    protected void sendWorkflowTypeFilterPositionClicked(int position) {
        messageWorkflowTypeFilterPositionSelectedToWorkflowList.setValue(position);
    }

    protected void sendBaseFilterPositionClicked(int position) {
        messageBaseFilterPositionSelectedToWorkflowList.setValue(position);
    }

    protected void sendStatusFilterPositionClicked(int position) {
        messageStatusFilterPositionSelectedToWorkflowList.setValue(position);
    }

    protected void sendSystemStatusFilterPositionClicked(int position) {
        messageSystemStatusFilterPositionSelectedToWorkflowList.setValue(position);
    }

    protected void handleRadioButtonClicked(boolean isChecked, @IdRes int viewId) {
        int[] message = new int[2];
        int checked = UNCHECK;
        if (isChecked) {
            checked = CHECK;
        }
        message[IS_CHECKED_INDEX] = checked;
        message[VIEW_ID_INDEX] = viewId;
        messageRadioButtonClickedToWorkflowList.setValue(message);
    }

    protected void handleSwitchOnClick(
            int viewRadioType,
            Sort.sortType sortType,
            boolean isChecked
    ) {
        RightDrawerSortSwitchAction actionMessage = new RightDrawerSortSwitchAction();
        actionMessage.viewRadioType = viewRadioType;
        actionMessage.sortType = sortType;
        actionMessage.isChecked = isChecked;
        messageSortSwitchActionToWorkflowList.setValue(actionMessage);
    }

    public void openRightDrawer() {
        openRightDrawer.setValue(true);
    }

    public void receiveMessageToggleRadioButton(int[] message) {
        receiveMessageToggleRadioButton.setValue(message);
    }

    public void receiveMessageToggleSwitch(int[] message) {
        receiveMessageToggleSwitch.setValue(message);
    }

    public void receiveMessageUpdateSortSelection(int sorType) {
        receiveMessageUpdateSortSelected.setValue(sorType);
    }

    public void receiveMessageWorkflowTypeFilterSelectedToListUi(String label) {
        receiveMessageWorkflowTypeFilterSelected.setValue(label);
    }

    public void receiveMessageWorkflowTypeIdFilterSelectedToListUi(int workflowTypeId) {
        receiveMessageWorkflowTypeIdFilterSelected.setValue(workflowTypeId);
    }

    public void receiveMessageBaseFilterSelectedToListUi(int resLabel) {
        receiveMessageBaseFilterSelected.setValue(resLabel);
    }

    public void receiveMessageStatusFilterSelectedToListUi(int resLabel) {
        receiveMessageStatusFilterSelected.setValue(resLabel);
    }

    public void receiveMessageSystemStatusFilterSelectedToListUi(int resLabel) {
        receiveMessageSystemStatusFilterSelected.setValue(resLabel);
    }

    public void getUser(int id) {
        Disposable disposable = repository.getUser(id)
                .subscribe(this::onUserSuccess, this::onFailure);
        disposables.add(disposable);
    }

    public void getWorkflowsLike(String text) {
        Disposable disposable = repository.getWorkflowsLike(text)
                .subscribe(this::onWorkflowsSuccess, this::onWorflowsFailure);
        disposables.add(disposable);
    }

    public void getWorkflow(int id) {
        Disposable disposable = repository.getWorkflow(id)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        disposables.add(disposable);
    }

    protected void attemptLogin(String user, String password) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    String firebaseToken = "";
                    if (task.isSuccessful()) {
                        // Get new Instance ID token
                        firebaseToken = task.getResult().getToken();
                    }

                    Disposable disposable = repository.login(user, password, firebaseToken)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginResponse -> {
                                if (loginResponse == null) {
                                    goToDomain.setValue(true);
                                    return;
                                }
                                String token = loginResponse.getToken();
                                saveToPreference.setValue(token);
                                String authToken = "Bearer " + token;

                            }, throwable -> {
                                Log.d(TAG,
                                        "attemptToLogin: Smomething failed with network request: " + throwable
                                                .getMessage());
                                goToDomain.setValue(true);
                            });
                    disposables.add(disposable);
                });
    }

    protected void onCreateOptionsMenu() {
        int defaultMenu = R.menu.menu_search;

    }

    // Called from workflow list.
    public void invalidateOptionListDrawer() {
        invalidateOptionsList.setValue(true);
    }

    public void createRightDrawerListAdapter(List<WorkflowTypeMenu> menus) {
        filtersList = menus;
        setRightDrawerFilterList.setValue(filtersList);
    }

    public void createRightDrawerOptionListAdapter(OptionsList rightOptionsList) {
//        optionsList = menus;
        setRightDrawerOptionList.setValue(rightOptionsList);
    }

    public void createDrawerWorkflowTypeFiltersOptionListAdapter(OptionsList optionsList) {
        receiveMessageCreateWorkflowTypeFiltersAdapter.setValue(optionsList);
    }

    public void createDrawerBaseFiltersOptionListAdapter(OptionsList optionsList) {
        receiveMessageCreateBaseFiltersAdapter.setValue(optionsList);
    }

    public void createDrawerStatusFiltersOptionListAdapter(OptionsList optionsList) {
        receiveMessageCreateStatusFiltersAdapter.setValue(optionsList);
    }

    public void createDrawerSystemStatusFiltersOptionListAdapter(OptionsList optionsList) {
        receiveMessageCreateSystemStatusFiltersAdapter.setValue(optionsList);
    }

    private void onWorkflowSuccess(Workflow workflow) {
        if (workflow == null) {
            return;
        }
        collapseMenu.setValue(true);
        hideKeyboard.setValue(true);
        goToWorkflowDetail.setValue(workflow);
    }

    private void onUserSuccess(User user) {
        String path = Utils.imgDomain + user.getPicture().trim();
        String[] content = new String[2];
        content[0] = IMG_TOOLBAR;
        content[1] = path;
        setImgInView.setValue(content);
    }

    private void onWorkflowsSuccess(Cursor cursor) {
        mWorkflowsLiveData.setValue(cursor);
    }

    private void onFailure(Throwable throwable) {
        Log.d(TAG, "onFailure: " + throwable.getMessage());
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
    }

    private void onWorflowsFailure(Throwable throwable) {
        Log.d(TAG, "onWorflowsFailure: " + throwable.getMessage());
        mWorkfErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
        mWorkfErrorLiveData.setValue(R.string.failure_connect);
    }

    //region Dynamic Filters

    protected void onValuesSelected(List<BaseFormItem> baseFormItems) {
        List<DynamicFilter> dynamicFilters = baseFormItems
                .stream()
                .map(this::getDynamicFilterFromFormItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        sendDynamicFilterListSelectedToWorkflowList(dynamicFilters);
    }

    private DynamicFilter getDynamicFilterFromFormItem(BaseFormItem baseFormItem) {
        String key = String.valueOf(baseFormItem.getFieldId());
        DynamicFilter dynamicFilter = null;

        if (baseFormItem instanceof SingleChoiceFormItem) {
            Option value = ((SingleChoiceFormItem) baseFormItem).getValue();
            Integer idValue = null;
            if (value != null) idValue = value.getId();

            dynamicFilter = new DynamicFilter(key, idValue);

        } else if (baseFormItem instanceof TextInputFormItem) {
            dynamicFilter = new DynamicFilter(key, ((TextInputFormItem) baseFormItem).getValue());

        } else if (baseFormItem instanceof AutocompleteFormItem) {
            Option value = ((AutocompleteFormItem) baseFormItem).getValue();
            if (value == null) return null;

            dynamicFilter = new DynamicFilter(key, value.getId());

        } else if (baseFormItem instanceof BooleanFormItem) {
            dynamicFilter = new DynamicFilter(key, ((BooleanFormItem) baseFormItem).getValue());

        } else if (baseFormItem instanceof MultipleChoiceFormItem) {
            List<BaseOption> values = ((MultipleChoiceFormItem) baseFormItem).getValues();

            List<Integer> intValues = values
                    .stream()
                    .map(option -> ((Option) option).getId())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            dynamicFilter = new DynamicFilter(key, intValues);

        }

        return dynamicFilter;
    }
    //endregion

    public LiveData<Cursor> getObservableWorkflows() {
        if (mWorkflowsLiveData == null) {
            mWorkflowsLiveData = new MutableLiveData<>();
        }
        return mWorkflowsLiveData;
    }

    public LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public LiveData<Integer> getObservableWorkflowError() {
        if (mWorkfErrorLiveData == null) {
            mWorkfErrorLiveData = new MutableLiveData<>();
        }
        return mWorkfErrorLiveData;
    }

    public LiveData<String[]> getObservableSetImgInView() {
        if (setImgInView == null) {
            setImgInView = new MutableLiveData<>();
        }
        return setImgInView;
    }

    protected LiveData<Boolean> getObservableCollapseMenu() {
        if (collapseMenu == null) {
            collapseMenu = new MutableLiveData<>();
        }
        return collapseMenu;
    }

    protected LiveData<Boolean> getObservableHideKeyboard() {
        if (hideKeyboard == null) {
            hideKeyboard = new MutableLiveData<>();
        }
        return hideKeyboard;
    }

    LiveData<Workflow> getObservableGoToWorkflowDetail() {
        if (goToWorkflowDetail == null) {
            goToWorkflowDetail = new MutableLiveData<>();
        }
        return goToWorkflowDetail;
    }

    LiveData<String> getObservableSaveToPreference() {
        if (saveToPreference == null) {
            saveToPreference = new MutableLiveData<>();
        }
        return saveToPreference;
    }

    LiveData<Boolean> getObservableAttemptTokenRefresh() {
        if (attemptTokenRefresh == null) {
            attemptTokenRefresh = new MutableLiveData<>();
        }
        return attemptTokenRefresh;
    }

    LiveData<Boolean> getObservableGoToDomain() {
        if (goToDomain == null) {
            goToDomain = new MutableLiveData<>();
        }
        return goToDomain;
    }

    LiveData<Boolean> getObservableStartService() {
        if (startService == null) {
            startService = new MutableLiveData<>();
        }
        return startService;
    }

    LiveData<Boolean> getObservableStopService() {
        if (stopService == null) {
            stopService = new MutableLiveData<>();
        }
        return stopService;
    }

    LiveData<QuickActionsVisibility> getObservableQuickActionsVisibility() {
        if (quickActionsVisibilityLiveData == null) {
            quickActionsVisibilityLiveData = new MutableLiveData<>();
        }
        return quickActionsVisibilityLiveData;
    }
}
