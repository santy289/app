package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDB;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDBDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.country.CountryDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.ProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowRepository;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
    protected MutableLiveData<Integer> saveIdToPreference;

    private ApiInterface apiInterface;
    private AppDatabase database;
    private List<Workflow> workflows;
    private List<WorkflowDb> workflowDbs;
    private List<Profile> profiles;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private int queriesDoneSoFar = 0;
    private String auth;

    private final static String TAG = "SyncHelper";
    protected static final int MAX_ENDPOINT_CALLS = 4; // TODO put this back to 2 when done testing.

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
        this.workflowDbs = new ArrayList<>();
        this.profiles = new ArrayList<>();
        this.saveIdToPreference = new MutableLiveData<>();
    }

    protected void syncData(String token) {
        this.auth = token;
        getUser(token);
        getAllWorkflows(token, 1);
        getWorkflowTypesDb(token);

        //getProfiles(token);
    }

    private void getCountryData(String token) {
        String base = BuildConfig.BASE_URL + "v1/";
        RetrofitUrlManager.getInstance().putDomain("api", base);

        Disposable disposable = apiInterface
                .getCountriesDb(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveCountriesToDatabase, throwable -> {
                    Log.d(TAG, "getCountryData: " + throwable.getMessage());
                    RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
                });

        disposables.add(disposable);
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
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success, this::onWorkflowTypesDbFailure);
        disposables.add(disposable);
    }

    private void getWorkflowsDb(String token, int page) {
        Disposable disposable = apiInterface
                .getWorkflowsDb(
                        token,
                        WorkflowRepository.ENDPOINT_PAGE_SIZE,
                        true,
                        page,
                        false)
                .subscribeOn(Schedulers.newThread())
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowTypesDbSuccess, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void getProfiles(String token) {
//        Disposable disposable = apiInterface
//                .getProfiles(token)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::onProfileSuccess, throwable -> {
//                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
//                    handleNetworkError(throwable);
//                });
//        disposables.add(disposable);
    }

    private void onProfileSuccess(ProfileResponse userResponse) {
//        Disposable disposable = Observable.fromCallable(() -> {
//            List<Profile> profiles = userResponse.getProfiles();
//            if (profiles == null) {
//                return false;
//            }
//            database.profileDao().deleteAllProfiles();
//            database.profileDao().insertProfiles(profiles);
//            return true;
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::success, this::userfailure);
//        disposables.add(disposable);
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
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> onDatabaseSavedWorkflowTypeDb(),
                        this::onWorkflowTypesDbFailure
                );
        disposables.add(disposable);
    }

    private void onDatabaseSavedWorkflowTypeDb() {
        Disposable disposable = apiInterface
                .getCategoryListId(auth)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoryListResponse -> {
                    int id = categoryListResponse.getCategoryList();
                    saveIdToPreference.setValue(id);
                    success(true);
                }, throwable -> {
                    Log.d(TAG, "onDatabaseSavedWorkflowTypeDb: Something went wrong trying to get category list id: " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    private void categoryIdSuccess(int categoryId) {

    }

    private void getWorkflowDbSuccess(WorkflowResponseDb workflowsResponse) {
        workflowDbs.addAll(workflowsResponse.getList());
        Disposable disposable = Observable.fromCallable(() -> {
            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            // TODO put in a transaction DAO function
            workflowDbDao.deleteAllWorkflows();
            workflowDbDao.insertWorkflows(workflowDbs);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> getCountryData(auth),
                        this::worflowDbDaoTransactionsFailure
                );
        disposables.add(disposable);
    }

    private void worflowDbDaoTransactionsFailure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }

    private void onWorkflowTypesDbFailure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    @Deprecated
    private void getAllWorkflows(String token, int page) {
        Disposable disposable = apiInterface
                .getWorkflows(token, 50, true, page, true)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowsSuccess, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void getUser(String token) {
        Disposable disposable = apiInterface.getUsers(token).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::onUsersSuccess, throwable -> {
                    Log.d(TAG, "getData: error " + throwable.getMessage() );
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void getWorkflowTypes(String token) {
        Disposable disposable = apiInterface
                .getWorkflowTypes(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowTypesSuccess, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleNetworkError(throwable);
                });
        disposables.add(disposable);
    }

    private void handleNetworkError(Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            // TODO go to timeline but fail because there is no internet connection.
            return;
        }

        if (!(throwable instanceof HttpException)) {
            return;
        }

        HttpException networkError = (HttpException) throwable;
        mSyncLiveData.setValue(false);

        if (networkError.code() != 401) {
            proceedWithUnhandledException();
            return;
        }
        disposables.clear();
        attemptTokenRefresh.setValue(true);
    }

    private void proceedWithUnhandledException() {
        mSyncLiveData.setValue(false);
    }

    public void attemptLogin(String username, String password) {
        Disposable disposable = apiInterface.login(username, password)
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
                    syncData(authToken);
                }, throwable -> {
                    Log.d(TAG, "attemptToLogin: Smomething failed with network request: " + throwable.getMessage());
                    goToDomain.setValue(true);
                });
        disposables.add(disposable);
    }

    private void onUsersSuccess(UserResponse userResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<User> users = userResponse.getProfiles();
            if (users == null) {
                return false;
            }
            database.userDao().clearUser();
            database.userDao().insertAll(users);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success, this::userfailure);
        disposables.add(disposable);
    }

    private void userfailure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }

    private void error(String message) {
        Log.d(TAG, "error: Something happened " + message);
    }

    private void onWorkflowTypesSuccess(WorkflowTypesResponse response) {
        Disposable disposable = Observable.fromCallable(() -> {
            List<WorkflowType> workflowTypes = response.getList();
            if (workflowTypes == null) {
                return false;
            }
//            database.workflowDao().
//            database.workflowDao().insertWorkflow(workflowTypes);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( success -> {

                }, this::failure);
        disposables.add(disposable);
    }

    @Deprecated
    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        workflows.addAll(workflowsResponse.getList());
//        if(!workflowsResponse.getPager().isIsLastPage()){
//            getAllWorkflows(auth, workflowsResponse.getPager().getNextPage());
//        }else{
            Disposable disposable = Observable.fromCallable(() -> {
                database.workflowDao().clearWorkflows();
                database.workflowDao().insertAll(workflows);
                return true;
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::failure);
            disposables.add(disposable);
//        }
    }

    private void success(Boolean o) {
        queriesDoneSoFar++;
        mProgressLiveData.setValue(queriesDoneSoFar);
        if(MAX_ENDPOINT_CALLS == queriesDoneSoFar){
            mSyncLiveData.setValue(true);
        }
    }

    private void failure(Throwable throwable) {
        mSyncLiveData.setValue(false);
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