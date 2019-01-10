package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.content.pm.PackageManager;
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
import java.util.List;

import androidx.annotation.NonNull;
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

    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 700;

    private WorkflowDetailRepository mRepository;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Integer> mFilesTabCounter;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<WorkflowListItem> initUiWithWorkflowListItem;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<StatusUiData> setWorkflowIsOpen;

    protected LiveData<StatusUiData> updateActiveStatusFromUserAction;
    protected LiveData<File> retrieveWorkflowPdfFile;
    protected LiveData<Boolean> handleShowLoadingByRepo;
    protected LiveData<StatusUiData> handleSetWorkflowIsOpenByRepo;

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
        this.initUiWithWorkflowListItem = new MutableLiveData<>();
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
        initUiWithWorkflowListItem.setValue(workflow);
        getWorkflow(this.mToken, this.mWorkflowListItem.getWorkflowId());
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

                    updateStatusUiData(mWorkflow.isOpen(), true);
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

        // Transformation used in case that any repo request fails
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorShowLoading(),
                show -> {
                    mShowToastMessage.setValue(R.string.failure_connect);
                    return show;
                }
        );

        // Transformation used in case that the workflow activation fails
        handleSetWorkflowIsOpenByRepo = Transformations.map(
                mRepository.getActivationFailed(),
                statusUiData -> {
                    /*
                    Set the original status. mWorkflow object is only updated if the request is successful.
                    Thus, it will always hold the correct status
                     */
                    updateStatusUiData(mWorkflow.isOpen(), true);
                    return mStatusUiData;
                }
        );
    }

    /**
     * Sends the parameters to modify the UI regarding the open/closed status.
     *
     * @param open            whether the workflow is open or closed.
     * @param userInteraction whether to use the {@link #setWorkflowIsOpen} LiveData. If true, it's
     *                        because it's been called from user interaction, so the {@link
     *                        #updateActiveStatusFromUserAction} LiveData will handle the update.
     */
    private void updateStatusUiData(boolean open, boolean userInteraction) {
        int iconRes = open ? R.drawable.ic_lock_open_black_24dp : R.drawable.ic_lock_outline_black_24dp;
        int colorRes = open ? R.color.green : R.color.red;
        mStatusUiData = new StatusUiData(iconRes, colorRes);

        if (!userInteraction) setWorkflowIsOpen.setValue(mStatusUiData);
    }

    /**
     * Calls the endpoint to change the Workflow active status. This will toggle the current state,
     * set it to closed if it's open and vice-versa.
     */
    protected void toggleWorkflowActivation() {
        showLoading.setValue(true);
        mRepository.postWorkflowActivation(mToken, mWorkflow.getId(), !mWorkflow.isOpen());
    }

    /**
     * Calls the endpoint to retrieve the PDF file for this Workflow.
     */
    protected void handleExportPdf() {
        showLoading.setValue(true);
        mRepository.getWorkflowPdfFile(mToken, mWorkflow.getId());
    }

    /**
     * Checks if the requested permissions were granted and then proceed to export the PDF file.
     *
     * @param requestCode  to identify the request
     * @param grantResults array containing the request results.
     */
    protected void handleRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSIONS: {
                // check for both permissions
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permissions granted
                    handleExportPdf();

                } else {
                    // at least one permission was denied
                    mShowToastMessage.setValue(
                            R.string.workflow_detail_activity_permissions_not_granted);
                }
            }
        }
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
        updateStatusUiData(mWorkflow.isOpen(), false);
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

    protected LiveData<WorkflowListItem> getObservableWorflowListItem() {
        return initUiWithWorkflowListItem;
    }
}
