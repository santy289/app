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
import java.util.Locale;

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
    private MutableLiveData<String> mWorkflowTypeVersionLiveData;
    private LiveData<WorkflowListItem> handleRepoWorkflowRequest;

    protected MutableLiveData<Boolean> showLoading;

    protected LiveData<File> retrieveWorkflowPdfFile;
    protected LiveData<Boolean> handleShowLoadingByRepo;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the mWorkflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.

    private static final String TAG = "WorkflowDetailViewModel";

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.mRepository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.initUiWithWorkflowListItem = new MutableLiveData<>();
        subscribe();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    /**
     * Initialize the Workflow detail screen using a WorkflowListItem coming from the user
     * selection on the workflow list.
     *
     * @param token
     * @param workflow
     */
    protected void initWithDetails(String token, WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        initUiWithWorkflowListItem.setValue(workflow);
        getWorkflow(this.mToken, this.mWorkflowListItem.getWorkflowId());
    }

    /**
     * Initialize the Workflow detail screen using an id. This method is responsible for looking the
     * actual WorkflowDb object from the local database or network.
     *
     * @param token
     * @param id
     */
    protected void initWithId(String token, String id) {
        this.mToken = token;

        handleRepoWorkflowRequest = Transformations.map(
                mRepository.getObservableRetreiveFromDbWorkflow(),
                workflowDb -> {
                    initUiWithWorkflowListItem.setValue(workflowDb);
                    getWorkflow(token, workflowDb.getWorkflowId());
                    return workflowDb;
                }
        );

        mRepository.getWorkflowFromDataSources(token, Integer.valueOf(id));
    }

    /**
     * This subscribe function will make map transformations to observe LiveData objects in the
     * repository. Here we will handle all incoming data from the repo.
     */
    private void subscribe() {
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

        int version = mWorkflow.getWorkflowType().getVersion();
        String versionString = String.format(Locale.US, "v%d", version);
        mWorkflowTypeVersionLiveData.setValue(versionString);
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
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
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

    protected LiveData<String> getObservableWorkflowTypeVersion() {
        if (mWorkflowTypeVersionLiveData == null) {
            mWorkflowTypeVersionLiveData = new MutableLiveData<>();
        }
        return mWorkflowTypeVersionLiveData;
    }

    protected LiveData<WorkflowListItem> getObservableWorflowListItem() {
        return initUiWithWorkflowListItem;
    }

    protected LiveData<WorkflowListItem> getObservableHandleRepoWorkflowRequest() {
        return handleRepoWorkflowRequest;
    }
}
