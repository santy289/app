package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class WorkflowSearchViewModel extends ViewModel {

    private WorkflowSearchRepository mRepository;

    private MutableLiveData<Integer> showToastMessage;
    private MutableLiveData<Boolean> showList;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<Boolean> showBottomSheetLoading;
    protected LiveData<List<WorkflowListItem>> workflowListFromRepo;
    protected LiveData<Boolean> handleShowLoadingByRepo;

    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private String mToken;

    private int mPageNumber;
    private String mQuery;
    private boolean isLoading;

    public WorkflowSearchViewModel(WorkflowSearchRepository repository) {
        this.mRepository = repository;
        this.showLoading = new MutableLiveData<>();
        this.showBottomSheetLoading = new MutableLiveData<>();

        subscribe();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected void init(String token) {
        this.mToken = token;
        mPageNumber = 1;
        getWorkflowList();
    }

    /**
     * This subscribe function will make map transformations to observe LiveData objects in the
     * repository. Here we will handle all incoming data from the repo.
     */
    private void subscribe() {
        // Transformation for observing approval and rejection of workflows.
        workflowListFromRepo = Transformations.map(
                mRepository.getObservableWorkflowList(),
                workflowResponseDb -> {
                    // transform WorkflowDb list to WorkflowListItem list

                    isLoading = false;
                    showBottomSheetLoading.setValue(false);
                    showLoading.setValue(false);

                    List<WorkflowListItem> workflowListItems = new ArrayList<>();
                    for (WorkflowDb workflow : workflowResponseDb.getList()) {
                        workflowListItems.add(new WorkflowListItem(workflow));
                    }

                    showList.setValue(workflowListItems.size() > 0);

                    return workflowListItems;
                }
        );

        // Transformation used in case that a workflow approval or rejection fails.
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorShowLoading(),
                show -> {
                    isLoading = false;
                    showLoading.setValue(false);
                    showBottomSheetLoading.setValue(false);
                    showToastMessage.setValue(R.string.error);
                    return show;
                }
        );
    }

    /**
     * Retrieves the latest workflows without a search query.
     */
    private void getRecentWorkflowList() {
        if (mPageNumber > 1) {
            showBottomSheetLoading.setValue(true);
        } else {
            showLoading.setValue(true);
        }

        mRepository.getRecentWorkflows(mToken, mPageNumber);
    }

    /**
     * Retrieve the workflows that match the text query. If the query is not set, retrieves the list
     * without filtering.
     *
     * @param query text to search.
     */
    protected void getWorkflowList(String query) {
        isLoading = true;

        mQuery = query;

        if (mQuery == null || mQuery.isEmpty()) {
            getRecentWorkflowList();
            return;
        }

        if (mPageNumber > 1) {
            showBottomSheetLoading.setValue(true);
        } else {
            showLoading.setValue(true);
        }

        mRepository.getWorkflowsBySearchQuery(mToken, mPageNumber, query);
    }

    /**
     * Performs a search query with the last saved value.
     */
    protected void getWorkflowList() {
        getWorkflowList(mQuery);
    }

    /**
     * This should be called every time the RecyclerView or ScrollView detects that it has reached
     * the bottom, thus it needs to fetch more items.
     */
    protected void increasePageNumber() {
        mPageNumber++;
    }

    /**
     * This should be called every time that the search query is modified, so we can fetch the first
     * items for the new query.
     */
    protected void resetPageNumber() {
        mPageNumber = 1;
    }

    /**
     * The RecyclerView or ScrollView must check for this so they will not perform a request while
     * another is still being executed.
     *
     * @return whether there is a request being processed.
     */
    protected boolean isLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getObservableShowList() {
        if (showList == null) {
            showList = new MutableLiveData<>();
        }
        return showList;
    }

    protected LiveData<Integer> getObservableShowToastMessage() {
        if (showToastMessage == null) {
            showToastMessage = new MutableLiveData<>();
        }
        return showToastMessage;
    }
}
