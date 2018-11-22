package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class WorkflowDetailViewModel extends ViewModel {

    private WorkflowDetailRepository mRepository;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Integer> mFilesTabCounter;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<Boolean> setWorkflowIsOpen;
    protected MutableLiveData<Integer> showToastMessage;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the mWorkflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.

    private static final String TAG = "WorkflowDetailViewModel";

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.mRepository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.setWorkflowIsOpen = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        getWorkflow(this.mToken, this.mWorkflowListItem.getWorkflowId());
    }

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Handles success when requesting for a mWorkflow by id to the endpoint.
     *
     * @param workflowResponse Network response with mWorkflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        showLoading.setValue(false);
        mWorkflow = workflowResponse.getWorkflow();
        updateUIWithWorkflow(mWorkflow);
    }

    private void updateUIWithWorkflow(WorkflowDb workflow) {
        setWorkflowIsOpen.setValue(workflow.isOpen());
    }

    /**
     * Defines the {@link ViewPager} Comments tab counter, called by {@link CommentsFragment}.
     *
     * @param count comments count
     */
    public void setCommentsTabCounter(int count) {
        mCommentsTabCounter.setValue(count);
    }

    /**
     * Defines the {@link ViewPager} Files tab counter, called by {@link FilesFragment}.
     *
     * @param count comments count
     */
    public void setFilesCounter(int count) {
        mFilesTabCounter.setValue(count);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Integer> getObservableShowToastMessage() {
        if (showToastMessage == null) {
            showToastMessage = new MutableLiveData<>();
        }
        return showToastMessage;
    }

    protected LiveData<Integer> getObservableCommentsTabCounter() {
        if (mCommentsTabCounter == null) {
            mCommentsTabCounter = new MutableLiveData<>();
        }
        return mCommentsTabCounter;
    }

    protected LiveData<Integer> getObservableFilesTabCounter() {
        if (mFilesTabCounter == null) {
            mFilesTabCounter = new MutableLiveData<>();
        }
        return mFilesTabCounter;
    }
}
