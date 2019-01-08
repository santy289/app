package com.rootnetapp.rootnetintranet.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import androidx.annotation.IdRes;

import android.text.TextUtils;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerSortSwitchAction;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.services.websocket.WebsocketSecureHandler;
import com.rootnetapp.rootnetintranet.ui.workflowlist.Sort;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.auth.ChallengeResponseAuth;
import io.crossbar.autobahn.wamp.interfaces.IAuthenticator;
import io.crossbar.autobahn.wamp.types.EventDetails;
import io.crossbar.autobahn.wamp.types.ExitInfo;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

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
    protected MutableLiveData<Integer> setSearchMenuLayout;
    protected MutableLiveData<Integer> setUploadMenuLayout;
    protected MutableLiveData<List<WorkflowTypeMenu>> setRightDrawerFilterList;
    protected MutableLiveData<OptionsList> setRightDrawerOptionList;
    protected MutableLiveData<Boolean> openRightDrawer;

    // Receive message from Workflow List
    protected MutableLiveData<int[]> receiveMessageToggleRadioButton;
    protected MutableLiveData<int[]> receiveMessageToggleSwitch;
    protected MutableLiveData<Integer> receiveMessageUpdateSortSelected;
    protected MutableLiveData<OptionsList> receiveMessageCreateBaseFiltersAdapter;
    protected MutableLiveData<Integer> receiveMessageBaseFilterSelected;

    // send message to WorkflowList
    public MutableLiveData<Integer> messageContainerToWorkflowList;
    public MutableLiveData<Integer> messageOptionSelectedToWorkflowList;
    public MutableLiveData<Boolean> messageBackActionToWorkflowList;
    public MutableLiveData<Boolean> messageInitSortByToWorkflowList;
    public MutableLiveData<int[]> messageRadioButtonClickedToWorkflowList;
    public MutableLiveData<RightDrawerSortSwitchAction> messageSortSwitchActionToWorkflowList;
    public MutableLiveData<Boolean> messageBaseFiltersClickedToWorkflowList;
    public MutableLiveData<Integer> messageBaseFilterPositionSelectedToWorkflowList;
    public MutableLiveData<Boolean> invalidateOptionsList;

    // receiving incoming notification from network
    private LiveData<String[]> receiveIncomingNotification;
    private NotificationManager notifyManager;

    List<WorkflowTypeMenu> filtersList;
    List<WorkflowTypeMenu> optionsList;

    private WebsocketSecureHandler webSocketHandler;
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
        this.invalidateOptionsList = new MutableLiveData<>();
        this.messageInitSortByToWorkflowList = new MutableLiveData<>();
        this.receiveMessageToggleRadioButton = new MutableLiveData<>();
        this.receiveMessageToggleSwitch = new MutableLiveData<>();
        this.receiveMessageUpdateSortSelected = new MutableLiveData<>();
        this.messageRadioButtonClickedToWorkflowList = new MutableLiveData<>();
        this.messageSortSwitchActionToWorkflowList = new MutableLiveData<>();
        this.messageBaseFiltersClickedToWorkflowList = new MutableLiveData<>();
        this.receiveMessageCreateBaseFiltersAdapter = new MutableLiveData<>();
        this.messageBaseFilterPositionSelectedToWorkflowList = new MutableLiveData<>();
        this.receiveMessageBaseFilterSelected = new MutableLiveData<>();
        this.openRightDrawer = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        webSocketHandler.completeClient();
    }

    protected void initMainViewModel(SharedPreferences sharedPreferences) {
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
            Utils.domain = "https://" + domain.getClient().getApiUrl();
            Utils.setImgDomain(domain.getClient().getApiUrl());
            String[] content = new String[2];
            content[0] = MainActivityViewModel.IMG_LOGO;
            content[1] = Utils.URL + domain.getClient().getLogoUrl();
            setImgInView.setValue(content);
            content[0] = MainActivityViewModel.IMG_BAR_LOGO;
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
    }

    void initNotifications(SharedPreferences sharedPref, NotificationManager notificationManager) {
        String protocol = sharedPref.getString(PreferenceKeys.PREF_PROTOCOL, "");
        String port = sharedPref.getString(PreferenceKeys.PREF_PORT, "");
        String token = sharedPref.getString(PreferenceKeys.PREF_TOKEN, "");

        if (TextUtils.isEmpty(protocol) || TextUtils.isEmpty(port) || TextUtils.isEmpty(token)) {
            return;
        }

        webSocketHandler = new WebsocketSecureHandler(protocol, port, token);
        webSocketHandler.initNotifications();

        receiveIncomingNotification = Transformations.map(
          webSocketHandler.getObservableIncomingNotification(),
          notificationArray -> {
              Log.d(TAG, "initNotifications: test");


              return notificationArray;
          }
        );
    }



    protected void sendFilterClickToWorflowList(int position) {
        messageContainerToWorkflowList.setValue(position);
    }

    protected void sendOptionSelectedToWorkflowList(int position) {
        messageOptionSelectedToWorkflowList.setValue(position);
    }

    protected void sendRightDrawerBackButtonClick() {
        messageBackActionToWorkflowList.setValue(true);
    }

    protected void sendBaseFiltersClicked() {
        messageBaseFiltersClickedToWorkflowList.setValue(true);
    }

    protected void sendBaseFilterPositionClicked(int position) {
        messageBaseFilterPositionSelectedToWorkflowList.setValue(position);
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

    public void receiveMessageBaseFilterSelectedToListUi(int resLabel) {
        receiveMessageBaseFilterSelected.setValue(resLabel);
    }

    public void getUser(int id) {
        Disposable disposable = repository.getUser(id).subscribe(this::onUserSuccess, this::onFailure);
        disposables.add(disposable);
    }

    public void getWorkflowsLike(String text) {
        Disposable disposable = repository.getWorkflowsLike(text).subscribe(this::onWorkflowsSuccess, this::onWorflowsFailure);
        disposables.add(disposable);
    }

    public void getWorkflow(int id) {
        Disposable disposable = repository.getWorkflow(id).subscribe(this::onWorkflowSuccess, this::onFailure);
        disposables.add(disposable);
    }

    protected void attemptLogin(String user, String password) {
        Disposable disposable = repository.login(user, password)
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
                    Log.d(TAG, "attemptToLogin: Smomething failed with network request: " + throwable.getMessage());
                    goToDomain.setValue(true);
                });
        disposables.add(disposable);
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

    public void createDrawerBaseFiltersOptionListAdapter(OptionsList optionsList) {
        receiveMessageCreateBaseFiltersAdapter.setValue(optionsList);
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
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    private void onWorflowsFailure(Throwable throwable) {
        Log.d(TAG, "onWorflowsFailure: " + throwable.getMessage());
        mWorkfErrorLiveData.setValue(R.string.failure_connect);
    }

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

    LiveData<String[]> getReceiveIncomingNotification() {
        return receiveIncomingNotification;
    }

}
