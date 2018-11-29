package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class PerformActionViewModel extends ViewModel {

    private PerformActionRepository mRepository;

    private MutableLiveData<Integer> showToastMessage;
    private MutableLiveData<Boolean> showList;

    protected MutableLiveData<Boolean> showLoading;
    protected LiveData<List<WorkflowDb>> workflowListFromRepo;
    protected LiveData<Boolean> handleShowLoadingByRepo;

    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private String mToken;

    public PerformActionViewModel(PerformActionRepository repository) {
        this.mRepository = repository;
        this.showLoading = new MutableLiveData<>();

        subscribe();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected void init(String token) {
        this.mToken = token;
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
                    // todo transform WorkflowResponseDb to WorkflowListItem

                    updateUIWithWorkflowList(workflowResponseDb.getList());
                    showLoading.setValue(false);
                    showToastMessage.setValue(R.string.request_successfully);
                    return workflowResponseDb.getList();
                }
        );

        // Transformation used in case that a workflow approval or rejection fails.
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorShowLoading(),
                show -> {
                    showLoading.setValue(false);
                    showToastMessage.setValue(R.string.error);
                    return show;
                }
        );
    }

    /**
     * Retrieves the latest workflows.
     */
    private void getRecentWorkflowList() {
        showLoading.setValue(true);
        mRepository.getRecentWorkflows(mToken);
    }

    /**
     * Retrieve the workflows that match the text query.
     *
     * @param query text to search.
     */
    protected void getWorkflowList(String query) {
        showLoading.setValue(true);
        mRepository.getWorkflowsBySearchQuery(mToken, 1, query); //todo paging
    }

    /*protected void handleUiAndIncomingList(PagedList<WorkflowListItem> listWorkflows) {
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
    }*/

    /**
     * Passes the updated list to the UI using LiveData.
     *
     * @param workflowList updated list.
     */
    protected void updateUIWithWorkflowList(List<WorkflowDb> workflowList) {
        if (workflowList == null || workflowList.size() < 1) {
            showList.setValue(false);
            return;
        }

        showList.setValue(true);
    }

    /*protected LiveData<PagedList<WorkflowListItem>> getObservableAllWorkflows() {
        if (liveWorkflows == null) {
            liveWorkflows = new MutableLiveData<>();
        }
        return liveWorkflows;
    }

    protected LiveData<PagedList<WorkflowListItem>> getObservableUpdateWithSortedList() {
        if (updateWithSortedList == null) {
            updateWithSortedList = new MutableLiveData<>();
        }
        return updateWithSortedList;
    }*/

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
