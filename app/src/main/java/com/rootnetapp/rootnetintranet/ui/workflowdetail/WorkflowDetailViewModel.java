package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_ACTIVATE_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_DELETE;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_MY_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EXPORT;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_OPEN_ALL;

public class WorkflowDetailViewModel extends ViewModel {

    private static final String TAG = "WorkflowDetailViewModel";
    private static final int INDEX_STATUS_OPEN = 0;
    private static final int INDEX_STATUS_CLOSED = 1;

    protected static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 700;

    private WorkflowDetailRepository mRepository;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Integer> mFilesTabCounter;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<WorkflowListItem> initUiWithWorkflowListItem;
    private MutableLiveData<WorkflowListItem> initSoftUiWithWorkflowListItem;
    private MutableLiveData<String> mWorkflowTypeVersionLiveData;
    private MutableLiveData<Boolean> mShowNotFoundViewLiveData;
    private MutableLiveData<Boolean> mShowExportPdfButtonLiveData;
    private MutableLiveData<Boolean> mShowDeleteLiveData;
    private MutableLiveData<Boolean> mShowEnableDisableLiveData;
    private MutableLiveData<Boolean> mShowOpenCloseLiveData;
    private MutableLiveData<String> mShareWorkflowLiveData;
    private LiveData<WorkflowListItem> handleRepoWorkflowRequest;
    private LiveData<Boolean> handleFetchFromServer;
    protected LiveData<Boolean> updateOpenClosedStatusFromUserAction;
    protected LiveData<Boolean> updateEnabledDisabledStatusFromUserAction;
    protected LiveData<Boolean> deleteWorkflowRepsonseLiveData;

    protected MutableLiveData<Boolean> showLoading;

    protected LiveData<File> retrieveWorkflowPdfFile;
    protected LiveData<Boolean> handleShowLoadingByRepo;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the mWorkflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.
    private boolean hasExportPermissions;
    private boolean hasDeletePermissions;
    private boolean hasEnableDisablePermissions;
    private boolean hasOpenClosePermissions;
    private boolean hasEditPermissions;

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.mRepository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.initUiWithWorkflowListItem = new MutableLiveData<>();
        this.initSoftUiWithWorkflowListItem = new MutableLiveData<>();
        subscribe();
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    /**
     * Initialize the Workflow detail screen using a WorkflowListItem coming from the user selection
     * on the workflow list.
     *
     * @param token             auth token
     * @param workflow          workflow item
     * @param loggedUserId      user id
     * @param permissionsString user permissions
     */
    protected void initWithDetails(String token, WorkflowListItem workflow, String loggedUserId,
                                   String permissionsString) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        initSoftUiWithWorkflowListItem.setValue(workflow);

        int userId = loggedUserId == null ? 0 : Integer.parseInt(loggedUserId);
        checkPermissions(permissionsString, userId);

        getWorkflow(this.mToken, this.mWorkflowListItem.getWorkflowId());
    }

    /**
     * Initialize the Workflow detail screen using an id. This method is responsible for looking the
     * actual WorkflowDb object from the local database or network.
     *
     * @param token             auth token
     * @param id                workflow id
     * @param loggedUserId      user id
     * @param permissionsString user permissions
     */
    protected void initWithId(String token, String id, String loggedUserId,
                              String permissionsString) {
        this.mToken = token;

        handleRepoWorkflowRequest = Transformations.map(
                mRepository.getObservableRetrieveFromDbWorkflow(),
                workflowDb -> {
                    initSoftUiWithWorkflowListItem.setValue(workflowDb);
                    getWorkflow(mToken, workflowDb.getWorkflowId());
                    return workflowDb;
                }
        );

        handleFetchFromServer = Transformations.map(
                mRepository.getObservableFetchWorkflowFromServer(),
                needToFetch -> {
                    getWorkflow(mToken, Integer.parseInt(id));
                    return needToFetch;
                }
        );

        int userId = loggedUserId == null ? 0 : Integer.parseInt(loggedUserId);
        checkPermissions(permissionsString, userId);

        mRepository.getWorkflowFromDataSources(token, Integer.valueOf(id));
    }

    /**
     * Verifies all of the user permissions related to this ViewModel and {@link
     * WorkflowDetailActivity}. Hide the UI related to the unauthorized actions.
     *
     * @param permissionsString users permissions.
     */
    private void checkPermissions(String permissionsString, int userId) {
        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);

        hasExportPermissions = permissionsUtils.hasPermission(WORKFLOW_EXPORT);
        hasDeletePermissions = permissionsUtils.hasPermission(WORKFLOW_DELETE);
        hasEnableDisablePermissions = permissionsUtils.hasPermission(WORKFLOW_ACTIVATE_ALL);
        hasOpenClosePermissions = permissionsUtils.hasPermission(WORKFLOW_OPEN_ALL);

        List<String> permissionsToCheck = new ArrayList<>();

        if (mWorkflowListItem != null && mWorkflowListItem.getOwnerId() == userId) {
            permissionsToCheck.add(WORKFLOW_EDIT_MY_OWN);
            permissionsToCheck.add(WORKFLOW_EDIT_OWN);
        } else {
            permissionsToCheck.add(WORKFLOW_EDIT_ALL);
        }
        hasEditPermissions = permissionsUtils.hasPermissions(permissionsToCheck);

        mShowExportPdfButtonLiveData.setValue(hasExportPermissions);
        mShowDeleteLiveData.setValue(hasDeletePermissions);
        mShowEnableDisableLiveData.setValue(hasEnableDisablePermissions);
        mShowOpenCloseLiveData.setValue(hasOpenClosePermissions);
    }

    protected boolean hasExportPermissions() {
        return hasExportPermissions;
    }

    protected boolean hasDeletePermissions() {
        return hasDeletePermissions;
    }

    protected boolean hasEnableDisablePermissions() {
        return hasEnableDisablePermissions;
    }

    protected boolean hasOpenClosePermissions() {
        return hasOpenClosePermissions;
    }

    protected boolean hasEditPermissions() {
        return hasEditPermissions;
    }

    protected WorkflowListItem getWorkflowListItem() {
        return mWorkflowListItem;
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

        // Transformation for observing open/close actions
        updateOpenClosedStatusFromUserAction = Transformations.map(
                mRepository.getOpenCloseResponse(),
                activationResponse -> {
                    // transform WorkflowActivationResponse to Boolean

                    showLoading.setValue(false);

                    // if correct, this API will only return one workflow

                    // check for emptiness of main list
                    List<List<WorkflowDb>> responseList = activationResponse.getData();
                    if (responseList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return !mWorkflow.isOpen();
                    }

                    // check for emptiness of workflow list
                    List<WorkflowDb> workflowDbList = responseList.get(0);
                    if (workflowDbList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return !mWorkflow.isOpen();
                    }

                    mWorkflow = workflowDbList.get(0);

                    mShowToastMessage.setValue(R.string.request_successfully);

                    return !mWorkflow.isOpen();
                }
        );

        // Transformation for observing enable/disable actions
        updateEnabledDisabledStatusFromUserAction = Transformations.map(
                mRepository.getEnableDisableResponse(),
                activationResponse -> {
                    // transform WorkflowActivationResponse to Boolean

                    showLoading.setValue(false);

                    // if correct, this API will only return one workflow

                    // check for emptiness of main list
                    List<List<WorkflowDb>> responseList = activationResponse.getData();
                    if (responseList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return !mWorkflow.isStatus();
                    }

                    // check for emptiness of workflow list
                    List<WorkflowDb> workflowDbList = responseList.get(0);
                    if (workflowDbList.isEmpty()) {
                        mShowToastMessage.setValue(R.string.error);
                        return !mWorkflow.isStatus();
                    }

                    mWorkflow = workflowDbList.get(0);

                    mShowToastMessage.setValue(R.string.request_successfully);

                    return !mWorkflow.isStatus();
                }
        );

        // Transformation for observing delete actions
        deleteWorkflowRepsonseLiveData = Transformations.map(
                mRepository.getDeleteWorkflowResponse(),
                deleteWorkflowResponse -> {
                    // transform DeleteWorkflowResponse to Boolean

                    showLoading.setValue(false);

                    // if correct, this API will return code 200

                    mShowToastMessage.setValue(R.string.request_successfully);

                    return deleteWorkflowResponse.getCode() == 200;
                }
        );

        // Transformation used in case that any repo request fails
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorLiveData(),
                throwable -> {
                    showLoading.setValue(false);
                    mShowToastMessage.setValue(Utils.getOnFailureStringRes(throwable));
                    return false;
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
                .subscribe(this::onWorkflowSuccess, this::onWorkflowFailure);
        mDisposables.add(disposable);
    }

    /**
     * Handles success when requesting for a mWorkflow by id to the endpoint.
     *
     * @param workflowResponse Network response with mWorkflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mShowNotFoundViewLiveData.setValue(false);
        showLoading.setValue(false);
        mWorkflow = workflowResponse.getWorkflow();

        mWorkflowListItem = new WorkflowListItem(mWorkflow);
        initUiWithWorkflowListItem.setValue(mWorkflowListItem);

        int version = mWorkflow.getWorkflowType().getVersion();
        String versionString = String.format(Locale.US, "v%d", version);
        mWorkflowTypeVersionLiveData.setValue(versionString);

        mShowEnableDisableLiveData.setValue(!mWorkflow.isStatus());
        mShowOpenCloseLiveData.setValue(!mWorkflow.isOpen());
    }

    private void onWorkflowFailure(Throwable throwable) {
        showLoading.setValue(false);

        if (throwable instanceof HttpException) {
            int httpCode = ((HttpException) throwable).code();
            if (httpCode == 404) {
                mShowNotFoundViewLiveData.setValue(true);
                return;
            }
        }

        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
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

    /**
     * Calls the endpoint to change the Workflow open/closed status.
     */
    protected void setWorkflowOpenStatus(boolean open) {
        showLoading.setValue(true);
        mRepository.postWorkflowActivationOpenClose(mToken, mWorkflow.getId(), open);
    }

    /**
     * Calls the endpoint to change the Workflow enabled/disabled status.
     */
    protected void setWorkflowEnabledStatus(boolean enabled) {
        showLoading.setValue(true);
        mRepository.postWorkflowActivationEnableDisable(mToken, mWorkflow.getId(), enabled);
    }

    /**
     * Calls the endpoint to delete this workflow.
     */
    protected void deleteWorkflow() {
        showLoading.setValue(true);
        mRepository.deleteWorkflow(mToken, mWorkflow.getId());
    }

    /**
     * Generates the text to share this workflow, including the title, key and URL.
     *
     * @param domainJson the domain saved in the preferences.
     */
    protected void shareWorkflow(String domainJson) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        ClientResponse domain;

        try {
            domain = jsonAdapter.fromJson(domainJson);

            String workflowUrl = Utils.getWebProtocol(domain.getClient().getDomain()) + domain
                    .getClient().getDomain()
                    + "/Intranet/workflow/" + mWorkflowListItem.getWorkflowId();
            String shareText = String.format(
                    Locale.US,
                    "%s - %s (%s)",
                    mWorkflowListItem.getTitle(),
                    mWorkflowListItem.getWorkflowTypeKey(),
                    workflowUrl
            );

            mShareWorkflowLiveData.setValue(shareText);

        } catch (IOException e) {
            Log.e(TAG, "shareWorkflow: error: " + e.getMessage());
            onFailure(e);
        }
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

    protected LiveData<Boolean> getObservableShowNotFoundView() {
        if (mShowNotFoundViewLiveData == null) {
            mShowNotFoundViewLiveData = new MutableLiveData<>();
        }
        return mShowNotFoundViewLiveData;
    }

    protected LiveData<Boolean> getObservableShowExportPdfButton() {
        if (mShowExportPdfButtonLiveData == null) {
            mShowExportPdfButtonLiveData = new MutableLiveData<>();
        }
        return mShowExportPdfButtonLiveData;
    }

    protected LiveData<Boolean> getObservableShowDelete() {
        if (mShowDeleteLiveData == null) {
            mShowDeleteLiveData = new MutableLiveData<>();
        }
        return mShowDeleteLiveData;
    }

    protected LiveData<Boolean> getObservableShowEnableDisable() {
        if (mShowEnableDisableLiveData == null) {
            mShowEnableDisableLiveData = new MutableLiveData<>();
        }
        return mShowEnableDisableLiveData;
    }

    protected LiveData<Boolean> getObservableShowOpenClose() {
        if (mShowOpenCloseLiveData == null) {
            mShowOpenCloseLiveData = new MutableLiveData<>();
        }
        return mShowOpenCloseLiveData;
    }

    protected LiveData<String> getObservableShareWorkflow() {
        if (mShareWorkflowLiveData == null) {
            mShareWorkflowLiveData = new MutableLiveData<>();
        }
        return mShareWorkflowLiveData;
    }

    protected LiveData<WorkflowListItem> getObservableWorkflowListItem() {
        return initUiWithWorkflowListItem;
    }

    protected LiveData<WorkflowListItem> getObservableSoftWorkflowListItem() {
        return initSoftUiWithWorkflowListItem;
    }

    protected LiveData<WorkflowListItem> getObservableHandleRepoWorkflowRequest() {
        return handleRepoWorkflowRequest;
    }

    protected LiveData<Boolean> getObservableFetchFromServer() {
        return handleFetchFromServer;
    }
}
