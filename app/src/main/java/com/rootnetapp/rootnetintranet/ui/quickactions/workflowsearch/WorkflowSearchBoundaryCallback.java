package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.interfaces.BoundaryCallbackInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.IncomingWorkflowsCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowSearchBoundaryCallback
        extends PagedList.BoundaryCallback<WorkflowListItem>
        implements BoundaryCallbackInterface {
    private ApiInterface service;
    private String token;
    private IncomingWorkflowsCallback callback;
    private boolean isLoading;
    private MutableLiveData<Boolean> messageLoadingMoreToUi;

    private int currentPage;
    private int lastPage;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "SearchBoundaryCallback";

    public WorkflowSearchBoundaryCallback(
            ApiInterface service,
            String token,
            int currentPage,
            IncomingWorkflowsCallback workflowsCallback) {
        this.service = service;
        this.token = token;
        this.currentPage = currentPage;
        this.callback = workflowsCallback;
        this.isLoading = false;
        this.lastPage = 2;
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
        updateIsLoading(true);
        messageLoadingMoreToUi.setValue(true);
        Disposable disposable = service
                .getWorkflowsDb(
                        token,
                        WorkflowSearchRepository.ENDPOINT_PAGE_SIZE,
                        true,
                        nextPage,
                        false)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::saveInDatabase,
                        throwable -> {
                            Log.d(TAG, "WorkflowSearchBoundaryCallback: Cant get workflows from network - " + throwable.getMessage());
                            updateIsLoading(false);
                            messageLoadingMoreToUi.setValue(false);
                        }
                );

        disposables.add(disposable);
    }

    /**
     * Method to clear any background thread work that is pending and ongoing.
     */
    public void clearDisposables() {
        disposables.clear();
    }

    /**
     * This will keep updated the current page number that we have received from a network request.
     *
     * @param pageNumber
     *  Int type variable to track the current page number.
     */
    public void updateCurrentPage(int pageNumber) {
        currentPage = pageNumber;
    }

    /**
     * Blocks any new data retrieval if we are loading something already.
     *
     * @param isLoading
     *  Updates isLoading to avoid any new requests to the network.
     */
    public void updateIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    /**
     * This will check if we have actual data coming from the response. Updates the lastPage
     * we might be in the last page or not. And calls the repository in order to save the new
     * upcoming content.
     *
     * @param workflowsResponse
     *  Network response with the new data requested.
     */
    private void saveInDatabase(WorkflowResponseDb workflowsResponse) {
        List<WorkflowDb> workflowDbs = workflowsResponse.getList();
        if (workflowDbs == null || workflowDbs.size() < 1) {
            return;
        }
        lastPage = workflowsResponse.getPager().getLastPage();
        callback.handleResponse(workflowDbs, lastPage);
    }

    protected LiveData<Boolean> getObservableMessageLoadingMoreToUi() {
        if (messageLoadingMoreToUi == null) {
            messageLoadingMoreToUi = new MutableLiveData<>();
        }
        return messageLoadingMoreToUi;
    }
}
