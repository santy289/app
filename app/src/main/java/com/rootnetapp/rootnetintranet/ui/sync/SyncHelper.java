package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.user.UserResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;

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
    private final CompositeDisposable disposables = new CompositeDisposable();

    private int totalQueries =2, queriesCompleted =0;
    private String auth;

    private final static String TAG = "SyncHelper";

    public SyncHelper(ApiInterface apiInterface, AppDatabase database) {
        this.apiInterface = apiInterface;
        this.database = database;
        this.workflows = new ArrayList<>();
    }

    protected void syncData(String token) {
        this.auth = token;
        getUser(token);
        getAllWorkflows(token, 0);
    }

    protected void clearDisposables() {
        disposables.clear();
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
        HttpException networkError = (HttpException) throwable;
        if (networkError == null) {
            mSyncLiveData.setValue(false);
            return;
        }
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
                .subscribe(this::success, this::failure);
        disposables.add(disposable);
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
        queriesCompleted++;
        mProgressLiveData.setValue(queriesCompleted);
        if(totalQueries == queriesCompleted){
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