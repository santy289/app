package com.rootnetapp.rootnetintranet.ui.sync;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDB;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDBDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.country.CountryDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.ProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.websocket.OptionsSettingsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import retrofit2.HttpException;

public class SyncHelper {

    private MutableLiveData<Boolean> mSyncLiveData;
    private MutableLiveData<Integer> mProgressLiveData;
    private MutableLiveData<Boolean> attemptTokenRefresh;
    private MutableLiveData<Boolean> goToDomain;
    private MutableLiveData<String> saveToPreference;
    MutableLiveData<String[]> saveStringToPreference;
    MutableLiveData<Integer> saveIdToPreference;

    private ApiInterface apiInterface;
    private AppDatabase database;
    private List<Workflow> workflows;
    private List<WorkflowDb> workflowDbs;
    private List<Profile> profiles;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private int queriesDoneSoFar;
    private String auth;

    public final static int INDEX_KEY_STRING = 0;
    public final static int INDEX_KEY_VALUE = 1;
    private final static String TAG = "SyncHelper";

    // IMPORTANT: Remember to increase or decrease this number when you remove or add an endpoint.
    protected static final int MAX_ENDPOINT_CALLS = 6;

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
        this.workflowDbs = new ArrayList<>();
        this.profiles = new ArrayList<>();
        this.saveIdToPreference = new MutableLiveData<>();
        this.saveStringToPreference = new MutableLiveData<>();
    }

    protected ApiInterface getApiInterface() {
        return apiInterface;
    }

    protected AppDatabase getDatabase() {
        return database;
    }

    protected void syncData(String token) {
        this.auth = token;
        queriesDoneSoFar = 0;

        getWsSettings(token);
        getGoogleMapsSettings(token);
        getUser(token);
        getWorkflowTypesDb(token);
        getLoggedProfile(token);
    }

    /**
     * It switches Retrofit domain request to BuildConfig.BASE_URL once it is done getting country
     * data it switches Retrofit domain request back to Utils.domain.
     *
     * @param token
     */
    private void getCountryData(String token) {
        String base = BuildConfig.BASE_URL + "v1/";
        RetrofitUrlManager.getInstance().putDomain("api", base);

        Disposable disposable = apiInterface
                .getCountriesDb(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveCountriesToDatabase, throwable -> {
                    Log.d(TAG, "getCountryData: " + throwable.getMessage());
                    RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
                    handleNetworkError(throwable);
                });

        disposables.add(disposable);
    }

    /**
     * RxJava implementation to change 2 network requests for obtaining webSocket settings such as
     * port number and protocol type. Finally it saves to SharedPreferences all these settings.
     *
     * @param token Token network request.
     */
    private void getWsSettings(String token) {
        Disposable disposable = apiInterface
                .getWsPort(token)
                .doOnNext(response -> saveSettingsToPreference(response,
                        PreferenceKeys.PREF_PORT))
                .flatMap(response -> apiInterface.getWsProtocol(token))
                .doOnNext(response -> saveSettingsToPreference(response,
                        PreferenceKeys.PREF_PROTOCOL))
//                .retryWhen(observable -> Observable.timer(3, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> success(true), throwable -> {
                    Log.d(TAG, "getWsSettings: " + throwable.getMessage());
                    if (throwable instanceof UnknownHostException) {
                        Log.d(TAG, "getWsSettings: network is down probably");
                    }
                    handleNetworkError(throwable);
                });

        disposables.add(disposable);
    }

    /**
     * RxJava implementation to change 2 network requests for obtaining webSocket settings such as
     * port number and protocol type. Finally it saves to SharedPreferences all these settings.
     *
     * @param token Token network request.
     */
    private void getGoogleMapsSettings(String token) {
        Disposable disposable = apiInterface
                .getGoogleMapsApiKey(token)
                .doOnNext(response -> saveSettingsToPreference(response,
                        PreferenceKeys.PREF_GOOGLE_MAPS_API_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> success(true), throwable -> {
                    Log.d(TAG, "getGoogleMapsSettings: " + throwable.getMessage());
                    if (throwable instanceof UnknownHostException) {
                        Log.d(TAG, "getGoogleMapsSettings: network is down probably");
                    }
                    handleNetworkError(throwable);
                });

        disposables.add(disposable);
    }

    /**
     * Validates if response has a correct status "success". Saves response values to
     * SharedPreferences.
     *
     * @param response      Incoming response with values.
     * @param preferenceKey Expecting static variables from class PreferenceKeys.
     */
    private void saveSettingsToPreference(OptionsSettingsResponse response,
                                          String preferenceKey) {
        String status = response.getStatus();
        if (TextUtils.isEmpty(status) || !status.equals("success")) {
            return;
        }
        String value = response.getData().getValue();
        String[] preferencesValue = new String[]{preferenceKey, value};
        saveStringToPreference.postValue(preferencesValue);
    }

    private void saveCountriesToDatabase(CountryDbResponse response) {
        RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
        Disposable disposable = Observable.fromCallable(() -> {
            List<CountryDB> list = response.getCountries();
            if (list == null) {
                return false;
            }
            CountryDBDao countryDBDao = database.countryDBDao();
            countryDBDao.deleteAllCountries();
            countryDBDao.insertCountryList(list);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success, this::handleNetworkError);
        disposables.add(disposable);
    }

    private void getWorkflowsDb(String token, int page) {
        Disposable disposable = apiInterface
                .getWorkflowsDb(
                        token,
                        100,
                        true,
                        page,
                        false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getWorkflowDbSuccess, throwable -> {
                    Log.d(TAG, "getWorkflowsDb: error: " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void getWorkflowTypesDb(String token) {
        Disposable disposable = apiInterface
                .getWorkflowTypesDb(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowTypesDbSuccess, throwable -> {
                    Log.e(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void onWorkflowTypesDbSuccess(WorkflowTypeDbResponse response) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<WorkflowTypeDb> workflowTypes = response.getList();
            if (workflowTypes == null) {
                return false;
            }

            WorkflowTypeDbDao workflowTypeDbDao = database.workflowTypeDbDao();
            workflowTypeDbDao.deleteAllWorkfloyTypes();
            workflowTypeDbDao.insertWorkflowTypes(workflowTypes);

            workflowTypeDbDao.deleteAllFields();
            for (int i = 0; i < workflowTypes.size(); i++) {
                List<Field> fields = workflowTypes.get(i).getFields();
                workflowTypeDbDao.insertAllFields(fields);
            }

            getWorkflowsDb(auth, 1);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> onDatabaseSavedWorkflowTypeDb(),
                        this::handleNetworkError
                );
        disposables.add(disposable);
    }

    private void onDatabaseSavedWorkflowTypeDb() {
        Disposable disposable = apiInterface
                .getCategoryListId(auth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryListResponse -> {
                    int id = categoryListResponse.getCategoryList();
                    saveIdToPreference.setValue(id);
                    success(true);
                }, throwable -> {
                    Log.d(TAG,
                            "onDatabaseSavedWorkflowTypeDb: Something went wrong trying to get category list id: " + throwable
                                    .getMessage());
                });
        disposables.add(disposable);
    }

    private void getWorkflowDbSuccess(WorkflowResponseDb workflowsResponse) {
        workflowDbs.addAll(workflowsResponse.getList());
        Disposable disposable = Observable.fromCallable(() -> {
            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            // TODO put in a transaction DAO function
            workflowDbDao.deleteAllWorkflows();
            workflowDbDao.insertWorkflows(workflowDbs);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> getCountryData(auth),
                        this::handleNetworkError
                );
        disposables.add(disposable);
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    private void getUser(String token) {
        Disposable disposable = apiInterface.getProfiles(token).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, throwable -> {
                    Log.e(TAG, "getData: error " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void handleNetworkError(Throwable throwable) {
        if (!(throwable instanceof HttpException)) {
            proceedWithUnhandledException();
            return;
        }

        HttpException networkError = (HttpException) throwable;

        if (networkError.code() == 500 || networkError.code() != 401) {
            proceedWithUnhandledException();
            return;
        }

        if (!disposables.isDisposed()) {
            disposables.clear();
        }
        attemptTokenRefresh.setValue(true);
    }

    private void proceedWithUnhandledException() {
        mSyncLiveData.setValue(false);
    }

    protected void attemptLogin(String username, String password) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    String firebaseToken = "";
                    if (task.isSuccessful()) {
                        // Get new Instance ID token
                        firebaseToken = task.getResult().getToken();
                    }

                    Disposable disposable = apiInterface.login(username, password, firebaseToken)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginResponse -> {
                                if (loginResponse == null) {
                                    goToDomain.setValue(true);
                                    return;
                                }
                                String token = loginResponse.getToken();
                                saveToPreference.setValue(token);
                                String authToken = "Bearer " + token;
                                syncData(authToken);
                            }, throwable -> {
                                Log.d(TAG,
                                        "attemptToLogin: Smomething failed with network request: " + throwable
                                                .getMessage());
                                goToDomain.setValue(true);
                            });
                    disposables.add(disposable);
                });
    }

    private void onUsersSuccess(ProfileResponse profileResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<Profile> profiles = profileResponse.getProfiles();
            if (profiles == null) {
                return false;
            }
            database.profileDao().deleteAllProfiles();
            database.profileDao().insertProfiles(profiles);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success,
                        this::handleNetworkError);
        disposables.add(disposable);
    }

    @Deprecated
    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        workflows.addAll(workflowsResponse.getList());
        Disposable disposable = Observable.fromCallable(() -> {
            database.workflowDao().clearWorkflows();
            database.workflowDao().insertAll(workflows);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success,
                        this::handleNetworkError);
        disposables.add(disposable);
    }

    private void getLoggedProfile(String token) {
        Disposable disposable = apiInterface.getLoggedProfile(token)
                .subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(this::onLoggedProfileSuccess, throwable -> {
                            Log.d(TAG, "getData: error " + throwable.getMessage());
                            handleNetworkError(throwable);
                        });
        disposables.add(disposable);
    }

    private void onLoggedProfileSuccess(LoggedProfileResponse loggedProfileResponse) {
        if (loggedProfileResponse.getUser() == null || loggedProfileResponse.getUser()
                .getPermissions() == null) {
            proceedWithUnhandledException();
            return;
        }

        User loggedUser = loggedProfileResponse.getUser();
        addLoggedProfileToDatabase(loggedUser);

        Object permissionsObj = loggedUser.getPermissions();

        if (!(permissionsObj instanceof Map)) {
            proceedWithUnhandledException();
            return;
        }

        String permissionsString = RootnetPermissionsUtils.getPermissionsStringFromMap(
                (Map<String, Object>) permissionsObj);

        String[] value = new String[]{PreferenceKeys.PREF_USER_PERMISSIONS,
                                      permissionsString};
        saveStringToPreference.postValue(value);
    }

    private void addLoggedProfileToDatabase(User user) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<User> list = new ArrayList<>();
            list.add(user);
            database.userDao().insertAll(list);
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success,
                        this::handleNetworkError);
        disposables.add(disposable);
    }

    private void success(Boolean o) {
        queriesDoneSoFar++;
        mProgressLiveData.setValue(queriesDoneSoFar);
        if (MAX_ENDPOINT_CALLS == queriesDoneSoFar) {
            mSyncLiveData.setValue(true);
        }
    }

    public LiveData<Boolean> getObservableSync() {
        if (mSyncLiveData == null) {
            mSyncLiveData = new MutableLiveData<>();
        }
        return mSyncLiveData;
    }

    public LiveData<Integer> getObservableProgress() {
        if (mProgressLiveData == null) {
            mProgressLiveData = new MutableLiveData<>();
        }
        return mProgressLiveData;
    }

    public LiveData<Boolean> getObservableAttemptTokenRefresh() {
        if (attemptTokenRefresh == null) {
            attemptTokenRefresh = new MutableLiveData<>();
        }
        return attemptTokenRefresh;
    }

    public LiveData<Boolean> getObservableGoToDomain() {
        if (goToDomain == null) {
            goToDomain = new MutableLiveData<>();
        }
        return goToDomain;
    }

    public LiveData<String> getObservableSavetoPreference() {
        if (saveToPreference == null) {
            saveToPreference = new MutableLiveData<>();
        }
        return saveToPreference;
    }

}