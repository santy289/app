package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import androidx.paging.PagedList;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;

import java.util.List;

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

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "ListBoundaryCallback";

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
            String id) {
        this.service = service;
        this.token = token;
        this.currentPage = currentPage;
        this.callback = workflowsCallback;
        this.isLoading = false;
        this.lastPage = 2;
        this.id = id;
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
            IncomingWorkflowsCallback workflowsCallback) {
        this(service, token, currentPage, workflowsCallback, "");
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
        Disposable disposable;
        if (TextUtils.isEmpty(id)) {
            disposable = service
                    .getWorkflowsDb(
                            token,
                            WorkflowRepository.ENDPOINT_PAGE_SIZE,
                            true,
                            nextPage,
                            false)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::saveInDatabase,
                            throwable -> {
                                Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                                callback.showLoadingMore(false);
                            }
                    );
        } else {
            int userId = Integer.valueOf(id);
            disposable = service
                    .getMyPendingWorkflowsDb(
                            token,
                            WorkflowRepository.ENDPOINT_PAGE_SIZE,
                            true,
                            nextPage,
                            false,
                            userId,
                            null)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::saveInDatabase,
                            throwable -> Log.d(TAG, "WorkflowListBoundaryCallback: Cant get workflows from network - " + throwable.getMessage())
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
        List<WorkflowDb> workflowDbs = workflowsResponse.getList();
        if (workflowDbs == null || workflowDbs.size() < 1) {
            return;
        }
        lastPage = workflowsResponse.getPager().getLastPage();
        callback.handleResponse(workflowDbs, lastPage);
    }
}
