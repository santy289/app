package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
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
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;
    private DataSource<Integer, WorkflowListItem> workflowListItemDataSource;
    private WorkflowListBoundaryCallback callback;
    private PagedList.Config pagedListConfig;

    private final static String TAG = "WorkflowRepository";
    private final CompositeDisposable disposables = new CompositeDisposable();

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowDbDao = this.database.workflowDbDao();
        pagedListConfig = (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(LIST_PAGE_SIZE)
                    .build();
    }

    @Override
    public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
        this.lastPage = lastPage;
        insertWorkflows(workflowsResponse);
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
        return service.getWorkflowsDb(auth, 50, true, page, true, false)
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