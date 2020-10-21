package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.text.TextUtils;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowListBoundaryCallback extends PagedList.BoundaryCallback<WorkflowListItem> {
    private ApiInterface service;
    private String token;
    private IncomingWorkflowsCallback callback;
    private boolean isLoading;

    private int currentPage;
    private int lastPage;
    private String id;
    private int workflowTypeId;
    private String searchText;
    private Map<String, Object> queryOptions;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "ListBoundaryCallback";

    public static final int NO_WORKFLOW_TYPE = -999;

    /**
     * Constructor used when we want request a full workflow list request.
     *
     * @param service
     * @param token
     * @param currentPage
     * @param workflowsCallback
     * @param id
     */
    public WorkflowListBoundaryCallback(
            ApiInterface service,
            String token,
            int currentPage,
            IncomingWorkflowsCallback workflowsCallback,
            String id,
            int workflowTypeId, //TODO remove
            String searchText, //TODO remove
            Map<String, Object> queryOptions) {
        this.service = service;
        this.token = token;
        this.currentPage = currentPage;
        this.callback = workflowsCallback;
        this.isLoading = false;
        this.lastPage = 2;
        this.id = id;
        this.workflowTypeId = workflowTypeId;
        this.searchText = searchText;
        this.queryOptions = queryOptions;
    }

    /**
     * Constructor used when we do a request without some specific id.
     *
     * @param service
     * @param token
     * @param currentPage
     * @param workflowsCallback
     */
    public WorkflowListBoundaryCallback(
            ApiInterface service,
            String token,
            int currentPage,
            IncomingWorkflowsCallback workflowsCallback,
            String searchText,
            Map<String, Object> queryOptions) { //TODO put here more queryOptions
        this(service, token, currentPage, workflowsCallback, "", NO_WORKFLOW_TYPE, searchText, queryOptions);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull WorkflowListItem itemAtEnd) {
        if (isLoading) {
            return;
        }
        int nextPage = currentPage + 1;
        if (nextPage > lastPage) {
            return;
        }
        callback.showLoadingMore(true);
        updateIsLoading(true);
        Disposable disposable;

        if (this.workflowTypeId != NO_WORKFLOW_TYPE) {
            disposable = service
                    .getWorkflowsDbWithQueryOptions(
                            token,
                            WorkflowRepository.ENDPOINT_PAGE_SIZE,
                            true,
                            nextPage,
                            false,
                            queryOptions)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::saveInDatabase,
                            throwable -> {
                                Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                updateIsLoading(false);
                                callback.showLoadingMore(false);
                            }
                    );
            disposables.add(disposable);
            return;
        }

        if (TextUtils.isEmpty(id)) {
            //TODO put query optiones here
            if (queryOptions.size() > 0) {
                disposable = service
                        .getWorkflowsDbWithQueryOptions(
                                token,
                                WorkflowRepository.ENDPOINT_PAGE_SIZE,
                                true,
                                nextPage,
                                false,
                                queryOptions)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::saveInDatabase,
                                throwable -> {
                                    Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                    updateIsLoading(false);
                                    callback.showLoadingMore(false);
                                }
                        );
            } else {
                disposable = service
                        .getWorkflowsDbWithSearch(
                                token,
                                WorkflowRepository.ENDPOINT_PAGE_SIZE,
                                true,
                                nextPage,
                                false,
                                searchText)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::saveInDatabase,
                                throwable -> {
                                    Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                    updateIsLoading(false);
                                    callback.showLoadingMore(false);
                                }
                        );
            }


            disposables.add(disposable);
            return;
        }

        int userId = Integer.parseInt(id);

        if (queryOptions.size() == 0) {
            disposable = service
                    .getMyPendingWorkflowsDb(
                            token,
                            WorkflowRepository.ENDPOINT_PAGE_SIZE,
                            true,
                            nextPage,
                            false,
                            userId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::saveInDatabase,
                            throwable -> {
                                Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                updateIsLoading(false);
                                callback.showLoadingMore(false);
                            }
                    );
        } else {
            disposable = service
                    .getMyPendingWorkflowsDb(
                            token,
                            WorkflowRepository.ENDPOINT_PAGE_SIZE,
                            true,
                            nextPage,
                            false,
                            userId,
                            queryOptions)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::saveInDatabase,
                            throwable -> {
                                Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                updateIsLoading(false);
                                callback.showLoadingMore(false);
                            }
                    );
        }


        disposables.add(disposable);
    }

    public void clearDisposables() {
        disposables.clear();
    }

    public void updateCurrentPage(int pageNumber) {
        currentPage = pageNumber;
    }

    public void updateIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    private void saveInDatabase(WorkflowResponseDb workflowsResponse) {
        callback.showLoadingMore(false);
        List<WorkflowDb> workflowDbs = workflowsResponse.getList();
        if (workflowDbs == null || workflowDbs.size() < 1) {
            return;
        }
        lastPage = workflowsResponse.getPager().getLastPage();
        callback.handleResponse(workflowDbs, lastPage);
    }
}
