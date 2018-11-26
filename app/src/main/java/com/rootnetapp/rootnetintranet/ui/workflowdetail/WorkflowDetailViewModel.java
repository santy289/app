package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class WorkflowDetailViewModel extends ViewModel {

    private static final int INDEX_STATUS_OPEN = 0;
    private static final int INDEX_STATUS_CLOSED = 1;

    private WorkflowDetailRepository mRepository;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Integer> mFilesTabCounter;
    private MutableLiveData<StatusUiData> mStatusSpinnerLiveData;
    private MutableLiveData<Integer> mShowToastMessage;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<StatusUiData> setWorkflowIsOpen;

    protected LiveData<StatusUiData> updateActiveStatusFromUserAction;
    protected LiveData<File> retrieveWorkflowPdfFile;
    protected LiveData<Boolean> handleShowLoadingByRepo;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the mWorkflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.

    private StatusUiData mStatusUiData;

    private static final String TAG = "WorkflowDetailViewModel";

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.mRepository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.setWorkflowIsOpen = new MutableLiveData<>();

        subscribe();
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
        getStatusList();
    }

    /**
     * This subscribe function will make map transformations to observe LiveData objects in the
     * repository. Here we will handle all incoming data from the repo.
     */
    private void subscribe() {
        // Transformation for observing approval and rejection of workflows.
        updateActiveStatusFromUserAction = Transformations.map(
                mRepository.getActivationResponse(),
                activationResponse -> {
                    // transform WorkflowActivationResponse to StatusUiData

                    showLoading.setValue(false);

                    // if correct, this API will only return one workflow

                    // check for emptiness of main list
                    List<List<WorkflowDb>> responseList = activationResponse.getData();
                    if (responseList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return mStatusUiData;
                    }

                    // check for emptiness of workflow list
                    List<WorkflowDb> workflowDbList = responseList.get(0);
                    if (workflowDbList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return mStatusUiData;
                    }

                    mWorkflow = workflowDbList.get(0);

                    mShowToastMessage.setValue(R.string.request_successfully);

                    mStatusUiData.setSelectedIndex(
                            mWorkflow.isOpen() ? INDEX_STATUS_OPEN : INDEX_STATUS_CLOSED);
                    return mStatusUiData;
                }
        );

        // Transformation for observing the workflow export pdf file
        retrieveWorkflowPdfFile = Transformations.map(
                mRepository.getExportPdfResponse(),
                exportPdfResponse -> {
                    // transform ExportPdfResponse to File

                    showLoading.setValue(false);

                    // the API will return a base64 string representing the file

                    String base64 = exportPdfResponse.getProject();
                    if (base64 == null || base64.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return null;
                    }

                    String fileName = mWorkflow.getWorkflowTypeKey() + " - " + mWorkflow.getTitle();
                    try {
                        return Utils.decodePdfFromBase64Binary(base64, fileName);

                    } catch (IOException e) {
                        Log.e(TAG, "exportPDF: ", e);
                        mShowToastMessage.setValue(R.string.error);
                        return null;
                    }

                }
        );

        // Transformation used in case that a workflow approval or rejection fails.
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorShowLoading(),
                show -> {
                    mShowToastMessage.setValue(R.string.failure_connect);
                    return show;
                }
        );
    }

    private void getStatusList() {
        List<Integer> stringResList = new ArrayList<>();
        stringResList.add(INDEX_STATUS_OPEN, R.string.open);
        stringResList.add(INDEX_STATUS_CLOSED, R.string.closed);

        List<Integer> colorResList = new ArrayList<>();
        colorResList.add(INDEX_STATUS_OPEN, R.color.green);
        colorResList.add(INDEX_STATUS_CLOSED, R.color.red);

        mStatusUiData = new StatusUiData(stringResList, colorResList);
        mStatusSpinnerLiveData.setValue(mStatusUiData);
    }

    protected void setStatusSelection(int selectedIndex) {
        mStatusUiData.setSelectedIndex(selectedIndex);
        setWorkflowIsOpen.setValue(mStatusUiData);
    }

    /**
     * Calls the endpoint to change the Workflow active status.
     */
    protected void handleWorkflowActivation(int selectedIndex) {
        showLoading.setValue(true);
        mRepository.postWorkflowActivation(mToken, mWorkflow.getId(),
                selectedIndex == INDEX_STATUS_OPEN);
    }

    /**
     * Calls the endpoint to retrieve the PDF file for this Workflow
     */
    protected void handleExportPdf() {
        showLoading.setValue(true);
        mRepository.getWorkflowPdfFile(mToken, mWorkflow.getId());
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
        mStatusUiData.setSelectedIndex(workflow.isOpen() ? INDEX_STATUS_OPEN : INDEX_STATUS_CLOSED);
        setWorkflowIsOpen.setValue(mStatusUiData);
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
        if (mShowToastMessage == null) {
            mShowToastMessage = new MutableLiveData<>();
        }
        return mShowToastMessage;
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

    protected LiveData<StatusUiData> getObservableStatusSpinner() {
        if (mStatusSpinnerLiveData == null) {
            mStatusSpinnerLiveData = new MutableLiveData<>();
        }
        return mStatusSpinnerLiveData;
    }
}
