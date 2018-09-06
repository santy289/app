package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeDbResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class SyncHelper {

    private MutableLiveData<Boolean> mSyncLiveData;
    private MutableLiveData<Integer> mProgressLiveData;
    private MutableLiveData<Boolean> attemptTokenRefresh;
    private MutableLiveData<Boolean> goToDomain;
    private MutableLiveData<String> saveToPreference;

    private ApiInterface apiInterface;
    private AppDatabase database;
    private List<Workflow> workflows;
    private List<WorkflowDb> workflowDbs;
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
    }

    protected void syncData(String token) {
        this.auth = token;
        getUser(token);
        getAllWorkflows(token, 0);

        // TODO testing remove this later
        getWorkflowTypesDb(token);
        getWorkflowsDb(token, 0);
    }

    /*************** *****************/

    private void getWorkflowsDb(String token, int page) {
        Disposable disposable = apiInterface
                .getWorkflowsDb(token, 50, true, page, true)
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
                .testGetWorkflowTypes(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWorkflowTypesDbSuccess, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
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

            getWorkflowsDb(auth, 0);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::success, this::onWorkflowTypesDbFailure);
        disposables.add(disposable);
    }

    private void getWorkflowDbSuccess(WorkflowResponseDb workflowsResponse) {
        workflowDbs.addAll(workflowsResponse.getList());

        if(!workflowsResponse.getPager().isIsLastPage()){
            getWorkflowsDb(auth, workflowsResponse.getPager().getNextPage());
        }else{
            Disposable disposable = Observable.fromCallable(() -> {
                WorkflowDbDao workflowDbDao = database.workflowDbDao();
                workflowDbDao.deleteAllWorkflows();
                workflowDbDao.insertWorkflows(workflowDbs);
                return true;
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::worflowDbDaoTransactionsFailure);
            disposables.add(disposable);
        }
    }

    private void worflowDbDaoTransactionsFailure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }

    private void onWorkflowTypesDbFailure(Throwable throwable) {
        mSyncLiveData.setValue(false);
    }


    /*************** *****************/


    protected void clearDisposables() {
        disposables.clear();
    }

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
                .subscribe(this::success, this::failure);
        disposables.add(disposable);
    }



    private void onWorkflowsSuccess(WorkflowsResponse workflowsResponse) {
        workflows.addAll(workflowsResponse.getList());
        if(!workflowsResponse.getPager().isIsLastPage()){
            getAllWorkflows(auth, workflowsResponse.getPager().getNextPage());
        }else{
            Disposable disposable = Observable.fromCallable(() -> {
                database.workflowDao().clearWorkflows();
                database.workflowDao().insertAll(workflows);
                return true;
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::success, this::failure);
            disposables.add(disposable);
        }
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