package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowRepository implements IncomingWorkflowsCallback {

    public static int ENDPOINT_PAGE_SIZE = 20;
    private static int LIST_PAGE_SIZE = 20;

    private int currentPage = 1;
    private int lastPage = 1;
    private AppDatabase database;
    private ApiInterface service;
    private WorkflowDbDao workflowDbDao;
    private WorkflowTypeDbDao workflowTypeDbDao;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;
    private LiveData<List<WorkflowTypeItemMenu>> workflowTypeMenuItems;
    private DataSource<Integer, WorkflowListItem> workflowListItemDataSource;
    private WorkflowListBoundaryCallback callback;
    private PagedList.Config pagedListConfig;

    private final static String TAG = "WorkflowRepository";
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final String baseWorkflowListQuery;
    public final static String WORKFLOWID = "workflowdb.id";
    public final static String WORKFLOW_CREATED = "workflowdb.created_at";
    public final static String WORKFLOW_UPDATED = "workflowdb.updated_at";

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowDbDao = this.database.workflowDbDao();
        workflowTypeDbDao = this.database.workflowTypeDbDao();
        pagedListConfig = (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(LIST_PAGE_SIZE)
                    .build();
        this.workflowTypeMenuItems = workflowTypeDbDao.getObservableTypesForMenu();
        baseWorkflowListQuery = "SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
                "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
                "workflowdb.full_name, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
                "workflowdb.start, workflowdb.status, workflowdb.`end` " +
                "FROM workflowtypedb INNER JOIN workflowdb " +
                "ON workflowdb.workflow_type_id = workflowtypedb.id ";
    }

    @Override
    public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
        this.lastPage = lastPage;
        insertWorkflows(workflowsResponse);
    }

    public LiveData<List<WorkflowTypeItemMenu>> getWorkflowTypeMenuItems() {
        return workflowTypeMenuItems;
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

    public void rawQueryWorkflowListByFilters(boolean status, String token) {
        String queryString = baseWorkflowListQuery +
                "WHERE workflowdb.status = ? ";
        Object[] objects = new Object[]{status};
        startRawQuery(queryString, token, objects);
    }

    public void rawQueryWorkflowListByFilters(boolean status, int workflowTypeId, String token) {
        String queryString = baseWorkflowListQuery +
                "WHERE workflowdb.status = ? " +
                "AND workflowdb.workflow_type_id = ?";
        Object[] objects = new Object[]{status, workflowTypeId};
        startRawQuery(queryString, token, objects);
    }

    public void rawQueryWorkflowListByFilters(boolean status, int workflowTypeId, String column, boolean isDescending, String token) {
        String queryString = baseWorkflowListQuery +
                "WHERE workflowdb.status = ? " +
                "AND workflowdb.workflow_type_id = ? ";
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC";
        } else {
            queryString += "ORDER BY " + column + " ASC";
        }

        Object[] objects = new Object[]{status, workflowTypeId};
        startRawQuery(queryString, token, objects);
    }

    public void rawQueryWorkflowListByFilters(boolean status, String column, boolean isDescending, String token) {
        String queryString = baseWorkflowListQuery +
                "WHERE workflowdb.status = ? ";
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC ";
        } else {
            queryString += "ORDER BY " + column + " ASC ";
        }
        Object[] objects = new Object[]{status};
        startRawQuery(queryString, token, objects);
    }

    private void startRawQuery(String queryString, String token, Object[] objects) {
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString, objects);
        getWorkflowsByFilters(token, sqlQuery);
    }


    private void getWorkflowsByFilters(String token, SupportSQLiteQuery query) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflowsWithFilter(query);
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

    public void invalidateDataSource() {
        workflowListItemDataSource.invalidate();
    }

    public void clearDisposables() {
        disposables.clear();
        callback.clearDisposables();
    }

    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    public void insertWorkflows(List<WorkflowDb> worflows) {
        Disposable disposable = Observable.fromCallable(() -> {
            WorkflowDbDao workflowDbDao = database.workflowDbDao();
            workflowDbDao.insertWorkflows(worflows);
            callback.updateIsLoading(false);
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (currentPage < lastPage) {
                        currentPage = currentPage + 1;
                    }
                    callback.updateCurrentPage(currentPage);
                }, throwable -> {
                    callback.updateIsLoading(false);
                    Log.d(TAG, "failure: Can't save to DB: " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    public void insertWorkflow(WorkflowDb workflow) {
        Disposable disposable = Completable.fromCallable(() -> {
            database.workflowDbDao().insertWorkflow(workflow);
            return true;
        }).subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe();
        disposables.add(disposable);
    }

    public Observable<List<WorkflowDb>> getWorkflowsFromInternal() {
        return Observable.fromCallable(()-> database.workflowDbDao().getAllWorkflows())
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponseDb> getWorkflowsFromService(String auth, int page) {
        return service.getWorkflowsDb(auth, 50, true, page,  false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<WorkflowDb>> setWorkflowsLocalUpdate(List<WorkflowDb> workflows){
        return Observable.fromCallable(() -> {
            database.workflowDbDao().insertWorkflows(workflows);
            return workflows;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}