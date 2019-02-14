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
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;

public class WorkflowSearchViewModel extends ViewModel {

    private WorkflowSearchRepository mRepository;

    private MutableLiveData<Integer> showToastMessage;
    private MutableLiveData<Boolean> showList;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<Boolean> showBottomSheetLoading;
    private MutableLiveData<Boolean> messageViewSetLoadingMore;
    private MutableLiveData<Boolean> messageViewSetQueryLoadingMore;
    private MutableLiveData<PagedList<WorkflowListItem>> updateWithSortedList;
    private MutableLiveData<Boolean> messageUiResetListDataSource;
    protected LiveData<List<WorkflowListItem>> workflowListFromRepo;
    protected LiveData<Boolean> handleShowLoadingByRepo;
    private LiveData<PagedList<WorkflowListItem>> liveWorkflows;
    private LiveData<Boolean> handleUiLoadingCompleted;

    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private String mToken;

    private int mPageNumber;
    private String mQuery;
    private boolean isLoading;

    public WorkflowSearchViewModel(WorkflowSearchRepository repository) {
        this.mRepository = repository;
        this.showLoading = new MutableLiveData<>();
        this.showBottomSheetLoading = new MutableLiveData<>();
        this.messageUiResetListDataSource = new MutableLiveData<>();
        subscribe();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected void init(String token) {
        this.mToken = token;
        mPageNumber = 1;
        setWorkflowListNoFilters(token);
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

        // Transformation used in case of failures in repo.
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getObservableMessageError(),
                show -> {
                    isLoading = false;
                    showBottomSheetLoading.setValue(false);
                    showToastMessage.setValue(R.string.error);
                    return show;
                }
        );

        // Transformation used to observe when LivePagedList is set and ready to be used in Repo.
        subscribeViewModelForUnfilteredList();

        // Transformation maps is sending false when the result is true because for now we
        // only hide a bottom sheet when we save workflows in the database after loading more.
        handleUiLoadingCompleted = Transformations.map(
                mRepository.getObservableLoadingCompleted(),
                result ->  !result
        );
    }

    /**
     * Start observing DataSource from repo which is not filtered,
     * or not a search query.
     */
    private void subscribeViewModelForUnfilteredList() {
       liveWorkflows = Transformations.switchMap(
                mRepository.getObservableMessagePagedListSet(),
                result -> {
                    messageViewSetLoadingMore.setValue(true);
                    showLoading.setValue(false);
                    showBottomSheetLoading.setValue(false);
                    return mRepository.getAllWorkflows();
                }
        );
       messageUiResetListDataSource.setValue(true);
    }

    /**
     * Set list source LiveData to start observing DataSource from repo which is a search query.
     */
    private void subscribeViewModelForQuery() {
        liveWorkflows = Transformations.switchMap(
                mRepository.getObservableMessageQueryListSet(),
                result -> {
                    messageViewSetQueryLoadingMore.setValue(true);
                    showLoading.setValue(false);
                    showBottomSheetLoading.setValue(false);
                    return mRepository.getAllWorkflows();
                }
        );
        messageUiResetListDataSource.setValue(true);
    }

    /**
     * This will be the called when we initialize the list of workflows for quick action. Also
     * it will start the loading animation and pass a token to the repo in case the repo needs to
     * call the network when we reached the end of the page.
     *
     * @param token
     */
    private void setWorkflowListNoFilters(String token) {
        showLoading.setValue(true);
        mRepository.setWorkflowList(token);
    }

    /**
     * This will be call with the upcoming PagedList wrapper in order to fist determine if we have
     * data and this will let the ViewModel take all the necessary decisions to update the UI
     * accordingly. And finally it will pass on the data to the View if we have data to pass.
     *
     * @param listWorkflows
     *  PagedList with the content coming from the database.
     */
    protected void handleUiAndIncomingList(PagedList<WorkflowListItem> listWorkflows) {
        if (listWorkflows == null) {
            showList.setValue(false);
            return;
        }
        if(listWorkflows.size() < 1){
            showList.setValue(false);
            return;
        }
        updateWithSortedList.setValue(listWorkflows);
        showList.setValue(true);
    }

    protected LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return liveWorkflows;
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
    private void getWorkflowList(String query) {
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
     * This should be called by the View when the user has commanded a new search action.
     *
     * @param query input by the user.
     */
    protected void performSearch(String query) {
//        resetPageNumber();
//        getWorkflowList(query);

        subscribeViewModelForQuery();
        mRepository.setQuerySearchList(mToken, query);
    }

    /**
     * This should be called every time the RecyclerView or ScrollView detects that it has reached
     * the bottom, thus it needs to fetch more items.
     */
    private void increasePageNumber() {
        mPageNumber++;
    }

    /**
     * This should be called every time that the search query is modified, so we can fetch the first
     * items for the new query.
     */
    private void resetPageNumber() {
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

    protected LiveData<Boolean> getObservableShowList() {
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

    protected LiveData<PagedList<WorkflowListItem>> getObservableUpdateWithSortedList() {
        if (updateWithSortedList == null) {
            updateWithSortedList = new MutableLiveData<>();
        }
        return updateWithSortedList;
    }

    protected LiveData<Boolean> getObservableMessageViewSetLoadingMore() {
        if (messageViewSetLoadingMore == null) {
            messageViewSetLoadingMore = new MutableLiveData<>();
        }
        return messageViewSetLoadingMore;
    }

    protected LiveData<Boolean> getObservableMessageViewSetQueryLoadingMore() {
        if (messageViewSetQueryLoadingMore == null) {
            messageViewSetQueryLoadingMore = new MutableLiveData<>();
        }
        return messageViewSetQueryLoadingMore;
    }


    protected LiveData<Boolean> getObservableFromRepoLoadingMoreCallback() {
        return mRepository.getObservableMessageLoadingMoreToUiFromCallback();
    }

    protected LiveData<Boolean> getObservableFromqueryLoadingMorecallback() {
        return mRepository.getObservableSearchMessageLoadingMoreToUiFromCallback();
    }

    protected LiveData<Boolean> getObservableHandleUiLoadingCompleted() {
        if (handleUiLoadingCompleted == null) {
            handleUiLoadingCompleted = new MutableLiveData<>();
        }
        return handleUiLoadingCompleted;
    }

    protected LiveData<Boolean> getObservableMessageUiResetListDataSource() {
        if (messageUiResetListDataSource == null) {
            messageUiResetListDataSource = new MutableLiveData<>();
        }
        return messageUiResetListDataSource;
    }

}
