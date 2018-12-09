package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.IncomingWorkflowsCallback;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowListBoundaryCallback;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowSearchRepository implements IncomingWorkflowsCallback {

    private static final String TAG = "WorkflowSearchRepo";
    private static final int PAGE_LIMIT = 20;
    public static int ENDPOINT_PAGE_SIZE = 60;
    private static int LIST_PAGE_SIZE = 60;

    private MutableLiveData<Boolean> showLoading;
    private MutableLiveData<WorkflowResponseDb> responseWorkflowList;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;

    private WorkflowDbDao workflowDbDao;
    private ApiInterface service;
    private WorkflowSearchBoundaryCallback callback;
    private PagedList.Config pagedListConfig;

    private int currentPage = 1;
    private int lastPage = 1;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected WorkflowSearchRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        workflowDbDao = database.workflowDbDao();
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

    @Override
    public void showLoadingMore(boolean loadMore) {
        // TODO update UI with loading show more or something.

    }

    /**
     * Method in charge of updating the database with the latest results coming from the network.
     * This will modify our WorkflowDb table. It also updates our boundary callback and updates it
     * to know if we are loading this data still on the database or we are finish loading the data
     * to the database.
     *
     * @param worflows
     *  List of workflows to save into the database.
     */
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

    /**
     * Prepares the DataSource with a query from the database and sets up the callback with a new
     * instance.
     * @param token
     */
    public void setWorkflowList(String token) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflowsByUpdatedAt();

        // TODO test if callback exists and if it is NOT null clear disposables. We are creating a new instance.
        callback = new WorkflowSearchBoundaryCallback(
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

    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    /**
     * Clears any disposables in the current repository and from boundary callback (pagination
     * feature).
     */
    protected void clearDisposables() {
        disposables.clear();
        callback.clearDisposables();
    }

    /**
     * Performs a call to the endpoint requesting the workflows that match the specified text query.
     *
     * @param auth authentication token.
     * @param page number of the page.
     * @param query text to search.
     */
    protected void getWorkflowsBySearchQuery(String auth, int page,
                                             String query) {
        Disposable disposable = service.getWorkflowsBySearchQuery(
                auth,
                PAGE_LIMIT,
                page,
                true,
                query,
                true
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> responseWorkflowList.setValue(success), throwable -> {
                    Log.d(TAG, "workflowListByQuery: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    /**
     * Performs a call to the endpoint requesting the latest workflows.
     *
     * @param auth authentication token.
     */
    protected void getRecentWorkflows(String auth, int pageNumber) {
        Disposable disposable = service.getWorkflowsDb(
                auth,
                PAGE_LIMIT,
                true,
                pageNumber,
                true
        )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> responseWorkflowList.setValue(success), throwable -> {
                    Log.d(TAG, "recentWorkflows: " + throwable.getMessage());
                    showLoading.setValue(false);
                });
        disposables.add(disposable);
    }

    protected LiveData<WorkflowResponseDb> getObservableWorkflowList() {
        if (responseWorkflowList == null) {
            responseWorkflowList = new MutableLiveData<>();
        }
        return responseWorkflowList;
    }

    protected LiveData<Boolean> getErrorShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

}
