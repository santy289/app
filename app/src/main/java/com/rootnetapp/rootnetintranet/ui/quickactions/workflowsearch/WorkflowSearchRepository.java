package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.interfaces.BoundaryCallbackInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.IncomingWorkflowsCallback;

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
    protected static final int PAGE_LIMIT = 20;
    public static int ENDPOINT_PAGE_SIZE = 60;
    private static int LIST_PAGE_SIZE = 30;

    private MutableLiveData<Boolean> messageErrorToViewModel;
    private MutableLiveData<WorkflowResponseDb> responseWorkflowList;
    private MutableLiveData<Boolean> messagePagedListSet;
    private MutableLiveData<Boolean> messagePagedQuerySet;
    private MutableLiveData<Boolean> messageLoadingCompleted;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;

    private WorkflowDbDao workflowDbDao;
    private ApiInterface service;
    private WorkflowSearchBoundaryCallback callback;
    private WorkflowSearchQueryCallback queryCallback;
    private PagedList.Config pagedListConfig;

    private int currentPage = 1;
    private int lastPage = 1;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected WorkflowSearchRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        workflowDbDao = database.workflowDbDao();
        pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)// default: true
                .setPageSize(LIST_PAGE_SIZE)
//                .setInitialLoadSizeHint(60) // default: page size * 3, request the inital load to be larger, avoids immediate load of pages when you first fetch data.
                .setPrefetchDistance(20) // default: page size but if we have latency maybe we want lower number than page size.
                .build();
    }

    @Override
    public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
        this.lastPage = lastPage;
        insertWorkflows(workflowsResponse, callback);
    }

    @Override
    public void showLoadingMore(boolean loadMore) {
        // No need to implement we are using a LiveData implementation to replace this method.
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
    public void insertWorkflows(List<WorkflowDb> worflows, BoundaryCallbackInterface callback) {
        Disposable disposable = Observable.fromCallable(() -> {
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
                    messageLoadingCompleted.setValue(true);
                }, throwable -> {
                    callback.updateIsLoading(false);
//                    showLoadingMore(false);
                    messageErrorToViewModel.setValue(false);
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
        currentPage = 1;
        lastPage = 1;

        Disposable disposable = Observable.fromCallable(() -> {
            DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflowsByUpdatedAt();

            if (callback != null) {
                callback.clearDisposables();
            }

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
            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    messagePagedListSet.setValue(result);
                }, throwable -> {
                    messageErrorToViewModel.setValue(false);
                    Log.d(TAG, "failure: Can't init LivePagedListBuilder " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    /**
     * Sets a new boundary callback and DataSource for a new LiveData PagedList.
     *
     * @param token
     * @param query
     */
    public void setQuerySearchList(String token, String query) {
        currentPage = 1;
        lastPage = 1;

        Disposable disposable = Observable.fromCallable(() -> {
            DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.searchWorkflow(query);

            if (queryCallback != null) {
                // Check if callback exists and if it is NOT null clear disposables.
                // We are creating a new instance.
                queryCallback.clearDisposables();
            }



            queryCallback = new WorkflowSearchQueryCallback(
                    service,
                    token,
                    query,
                    currentPage,
                    new IncomingWorkflowsCallback() {
                        @Override
                        public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
                            WorkflowSearchRepository.this.lastPage = lastPage;
                            insertWorkflows(workflowsResponse, queryCallback);
                        }

                        @Override
                        public void showLoadingMore(boolean loadMore) {

                        }
                    }
            );

            if (allWorkflows != null && allWorkflows.hasActiveObservers()) {
                Log.d(TAG, "setWorkflowList: YES");
            }

            if (allWorkflows != null && allWorkflows.hasObservers()) {
                Log.d(TAG, "setWorkflowList: YES");
            }


            allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                    .setBoundaryCallback(queryCallback)
                    .build();


            if (allWorkflows != null && allWorkflows.hasActiveObservers()) {
                Log.d(TAG, "setWorkflowList: YES");
            }

            if (allWorkflows != null && allWorkflows.hasObservers()) {
                Log.d(TAG, "setWorkflowList: YES");
            }

            return true;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    messagePagedQuerySet.setValue(result);
                }, throwable -> {
                    messageErrorToViewModel.setValue(false);
                    Log.d(TAG, "failure: Can't init LivePagedListBuilder " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    /**
     * It assumes that allWorkflows was previously initialized with a LiveData PagedList otherwise
     * it will not return any data.
     *
     * @return
     */
    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    public LiveData<Boolean> getObservableMessageLoadingMoreToUiFromCallback() {
        return callback.getObservableMessageLoadingMoreToUi();
    }

    public LiveData<Boolean> getObservableSearchMessageLoadingMoreToUiFromCallback() {
        return queryCallback.getObservableMessageLoadingMoreToUi();
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
                    messageErrorToViewModel.setValue(false);
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
                    messageErrorToViewModel.setValue(false);
                });
        disposables.add(disposable);
    }

    protected LiveData<WorkflowResponseDb> getObservableWorkflowList() {
        if (responseWorkflowList == null) {
            responseWorkflowList = new MutableLiveData<>();
        }
        return responseWorkflowList;
    }

    protected LiveData<Boolean> getObservableMessageError() {
        if (messageErrorToViewModel == null) {
            messageErrorToViewModel = new MutableLiveData<>();
        }
        return messageErrorToViewModel;
    }

    protected LiveData<Boolean> getObservableMessagePagedListSet() {
        if (messagePagedListSet == null) {
            messagePagedListSet = new MutableLiveData<>();
        }
        return messagePagedListSet;
    }

    protected LiveData<Boolean> getObservableMessageQueryListSet() {
        if (messagePagedQuerySet == null) {
            messagePagedQuerySet = new MutableLiveData<>();
        }
        return messagePagedQuerySet;
    }

    protected LiveData<Boolean> getObservableLoadingCompleted() {
        if (messageLoadingCompleted == null) {
            messageLoadingCompleted = new MutableLiveData<>();
        }
        return messageLoadingCompleted;
    }

}
