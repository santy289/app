package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import android.text.TextUtils;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.user.UserDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowRepository implements IncomingWorkflowsCallback {

    public static int ENDPOINT_PAGE_SIZE = 20;
    private static int LIST_PAGE_SIZE = 20;
    public final static String WORKFLOWID = "workflowdb.id";
    public final static String WORKFLOW_CREATED = "workflowdb.created_at";
    public final static String WORKFLOW_UPDATED = "workflowdb.updated_at";

    private int currentPage = 1;
    private int lastPage = 1;
    private AppDatabase database;
    private ApiInterface service;
    private WorkflowDbDao workflowDbDao;
    private WorkflowTypeDbDao workflowTypeDbDao;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;
    private LiveData<List<WorkflowTypeItemMenu>> workflowTypeMenuItems;
    private DataSource<Integer, WorkflowListItem> workflowListItemDataSource;
    private MutableLiveData<Boolean> handleRepoError;
    private MutableLiveData<Boolean> handleRepoSuccess;
    private MutableLiveData<Boolean> handleRepoSuccessNoFilters;
    public MutableLiveData<Boolean> showLoadMore;
    private WorkflowListBoundaryCallback callback;
    private PagedList.Config pagedListConfig;
    private UserDao profileDao;

    private final static String TAG = "WorkflowRepository";
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final String baseWorkflowListQuery;


    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowDbDao = this.database.workflowDbDao();
        workflowTypeDbDao = this.database.workflowTypeDbDao();
        this.profileDao = this.database.userDao();
        pagedListConfig = (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(LIST_PAGE_SIZE)
                    .build();
        this.workflowTypeMenuItems = workflowTypeDbDao.getObservableTypesForMenu();
        showLoadMore = new MutableLiveData<>();
        baseWorkflowListQuery = "SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
                "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
                "workflowdb.full_name, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
                "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
                "FROM workflowtypedb INNER JOIN workflowdb " +
                "ON workflowdb.workflow_type_id = workflowtypedb.id ";
    }

    @Override
    public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
        this.lastPage = lastPage;
        insertWorkflows(workflowsResponse);
    }

    public Observable<ListsResponse> getList(String auth, int id) {
        return service.getListItems(auth, id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void showLoadingMore(boolean show) {
        showLoadMore.setValue(show);
    }

    public LiveData<List<WorkflowTypeItemMenu>> getWorkflowTypeMenuItems() {
        return workflowTypeMenuItems;
    }

    public List<WorkflowTypeItemMenu> getWorkflowTypesForMenu() {
        return workflowTypeDbDao.getTypesForMenu();
    }

    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    public void setWorkflowList(String token) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflows();

        callback = new WorkflowListBoundaryCallback(
                service,
                token,
                currentPage,
                this
        );

        allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setBoundaryCallback(callback)
                .build();

//        DataSourceWorkflowListFactory dataSourceFactory = new DataSourceWorkflowListFactory(database);
//        workflowListItemDataSource = dataSourceFactory.create();
//        allWorkflows = new LivePagedListBuilder<>(dataSourceFactory, pagedListConfig).build();
    }

    public List<FormFieldsByWorkflowType> getFiedsByWorkflowType(int byId) {
        return workflowTypeDbDao.getFields(byId);
    }

    public void setWorkflowListByType(String token, int typeId) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflowsBy(typeId);
        callback = new WorkflowListBoundaryCallback(
                service,
                token,
                currentPage,
                this
        );

        allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setBoundaryCallback(callback)
                .build();
    }

    public void rawQueryWorkflowListByFilters(boolean status, String token, String id, String searchText) {
        Object[] objects;
        String queryString;
        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status};
        } else {
            searchText = "%" + searchText + "%";
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.title LIKE '%' || ? || '%' " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status, searchText};
        }
        startRawQuery(queryString, token, objects, id);
    }

    public void rawQueryWorkflowListByFilters(boolean status, int workflowTypeId, String token, String id, String searchText) {
        Object[] objects;
        String queryString;
        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.workflow_type_id = ? " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status, workflowTypeId};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.workflow_type_id = ? " +
                    "AND workflowdb.title LIKE '%' || ? || '%' " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status, workflowTypeId, searchText};
        }
        startRawQuery(queryString, token, objects, id);
    }

    public void rawQueryWorkflowListByFilters(boolean status, int workflowTypeId, String column, boolean isDescending, String token, String id, String searchText) {
        String queryString;
        Object[] objects;

        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.workflow_type_id = ? ";
            objects = new Object[]{status, workflowTypeId};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.workflow_type_id = ? " +
                    "AND workflowdb.title LIKE '%' || ? || '%' ";
            objects = new Object[]{status, workflowTypeId, searchText};
        }
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC";
        } else {
            queryString += "ORDER BY " + column + " ASC";
        }

        startRawQuery(queryString, token, objects, id);
    }

    public void rawQueryWorkflowListByFilters(boolean status, String column, boolean isDescending, String token, String id, String searchText) {
        String queryString;
        Object[] objects;

        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? ";
            objects = new Object[]{status};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND workflowdb.title LIKE '%' || ? || '%' ";
            objects = new Object[]{status, searchText};
        }
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC ";
        } else {
            queryString += "ORDER BY " + column + " ASC ";
        }
        startRawQuery(queryString, token, objects, id);
    }

    private void startRawQuery(String queryString, String token, Object[] objects, String id) {
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString, objects);
        getWorkflowsByFilters(token, sqlQuery, id);
    }


    private void getWorkflowsByFilters(String token, SupportSQLiteQuery query, String id) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflowsWithFilter(query);

        if (TextUtils.isEmpty(id)) {
            callback = new WorkflowListBoundaryCallback(
                    service,
                    token,
                    currentPage,
                    this
            );
        } else {
            callback = new WorkflowListBoundaryCallback(
                    service,
                    token,
                    currentPage,
                    this,
                    id
            );
        }

        allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setBoundaryCallback(callback)
                .build();
    }

    public void invalidateDataSource() {
        workflowListItemDataSource.invalidate();
    }

    public void clearDisposables() {
        disposables.clear();
        callback.clearDisposables();
    }

    public void insertWorkflows(List<WorkflowDb> worflows) {
        Disposable disposable = Observable.fromCallable(() -> {
//            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            workflowDbDao.insertWorkflows(worflows);
            callback.updateIsLoading(false);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    showLoadingMore(false);
                    if (currentPage < lastPage) {
                        currentPage = currentPage + 1;
                    }
                    callback.updateCurrentPage(currentPage);
                }, throwable -> {
                    callback.updateIsLoading(false);
                    showLoadingMore(false);
                    Log.d(TAG, "failure: Can't save to DB: " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    public void insertWorkflow(WorkflowDb workflow) {
        Disposable disposable = Completable.fromCallable(() -> {
            workflowDbDao.insertWorkflow(workflow);
            return true;
        }).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();
        disposables.add(disposable);
    }

    public void getWorkflowsByType(String token, int typeId) {
        currentPage = 1;
        Disposable disposable = service
                .getWorkflowsByType(
                        token,
                        50,
                        true,
                        1,
                        false,
                        typeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::workflowDbSuccessNoFilter, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }



    public void getWorkflowsByBaseFilters(String token, Map<String, Object> options) {
        Disposable disposable = service
                .getWorkflowsByBaseFilters(
                        token,
                        50,
                        true,
                        1,
                        false,
                        options)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::workflowDbSuccessNoFilter, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }

    public void getAllWorkflowsNoFilters(String token) {
        currentPage = 1;
        Disposable disposable = service
                .getWorkflowsDb(
                        token,
                        50,
                        true,
                        1,
                        false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::workflowDbSuccessNoFilter, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }

    public Observable<WorkflowResponseDb> getWorkflowsByFieldFilters(String token, int workflowTypeId, String metaData) {
        return service.getWorkflowsDbFilteredByDynamicFields(
                token,
                50,
                true,
                1,
                false,
                workflowTypeId,
                metaData
        );
    }

    public void workflowDbSuccess(WorkflowResponseDb workflowsResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
//            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            workflowDbDao.deleteAllWorkflows();
            workflowDbDao.insertWorkflows(workflowsResponse.getList());
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.d(TAG, "workflowDbSuccess: ");
                    handleRepoSuccess.postValue(true);
                }, throwable -> {
                    Log.d(TAG, "getWorkflowDbSuccess: error " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });

        disposables.add(disposable);
    }

    private void workflowDbSuccessNoFilter(WorkflowResponseDb workflowsResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
//            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            workflowDbDao.deleteAllWorkflows();
            workflowDbDao.insertWorkflows(workflowsResponse.getList());
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.d(TAG, "workflowDbSuccess: ");
                    handleRepoSuccessNoFilters.postValue(true);
                }, throwable -> {
                    Log.d(TAG, "getWorkflowDbSuccess: error " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });

        disposables.add(disposable);
    }

    public List<FormCreateProfile> getProfiles() {
        return profileDao.getAllProfiles();
    }

    public Observable<ListsResponse> getCategoryList(String auth, int id) {
        return service.getListItems(auth, id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<Boolean> getObservableHandleRepoError() {
        if (handleRepoError == null) {
            handleRepoError = new MutableLiveData<>();
        }
        return handleRepoError;
    }

    public LiveData<Boolean> getObservableHandleRepoSuccess() {
        if (handleRepoSuccess == null) {
            handleRepoSuccess = new MutableLiveData<>();
        }
        return handleRepoSuccess;
    }

    public LiveData<Boolean> getObservableHandleRepoSuccessNoFilter() {
        if (handleRepoSuccessNoFilters == null) {
            handleRepoSuccessNoFilters = new MutableLiveData<>();
        }
        return handleRepoSuccessNoFilters;
    }

}