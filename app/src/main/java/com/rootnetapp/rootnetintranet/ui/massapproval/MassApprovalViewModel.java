package com.rootnetapp.rootnetintranet.ui.massapproval;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.massapproval.models.StatusApproval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MassApprovalViewModel extends ViewModel {

    private static final String TAG = "MassApprovalViewModel";

    private MassApprovalRepository mRepository;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<WorkflowListItem> mInitWorkflowUiLiveData;
    private MutableLiveData<String> mWorkflowTypeVersionLiveData;
    private MutableLiveData<List<StatusApproval>> mPendingStatusListLiveData;
    private MutableLiveData<Boolean> mShowSubmitButtonLiveData;
    private MutableLiveData<Boolean> mEnableSubmitButtonLiveData;
    private MutableLiveData<Boolean> mHandleResultLiveData;
    protected MutableLiveData<Boolean> showLoading;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the mWorkflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.
    private WorkflowTypeDb currentWorkflowType;
    private int mUserId;

    public MassApprovalViewModel(MassApprovalRepository massApprovalRepository) {
        this.mRepository = massApprovalRepository;
        this.showLoading = new MutableLiveData<>();
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
        mInitWorkflowUiLiveData.setValue(workflow);

        mUserId = loggedUserId == null ? 0 : Integer.parseInt(loggedUserId);
//        checkPermissions(permissionsString, userId);

        showLoading.setValue(true);

        getWorkflow(this.mToken, this.mWorkflowListItem.getWorkflowId());
    }

    protected WorkflowTypeDb getCurrentWorkflowType() {
        return currentWorkflowType;
    }

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mWorkflow = workflowResponse.getWorkflow();
        getWorkflowType(mToken, mWorkflowListItem.getWorkflowTypeId());
    }

    /**
     * Calls the repository for obtaining a new Workflow Type by a type id.
     *
     * @param auth   Access token to use for endpoint request.
     * @param typeId Id that will be passed on to the endpoint.
     */
    private void getWorkflowType(String auth, int typeId) {
        Disposable disposable = mRepository
                .getWorkflowType(auth, typeId)
                .subscribe(this::onTypeSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Handles success response from endpoint when looking for a workflow type.
     *
     * @param response Incoming response from server.
     */
    private void onTypeSuccess(WorkflowTypeResponse response) {
        showLoading.setValue(false);
        currentWorkflowType = response.getWorkflowType();
        if (currentWorkflowType == null) {
            return;
        }
        updateUIWithWorkflowType(currentWorkflowType);
    }

    private void updateUIWithWorkflowType(WorkflowTypeDb currentWorkflowType) {
        List<StatusApproval> pendingStatusesForUser = new ArrayList<>();

        List<Status> allStatusesListForUser = currentWorkflowType
                .getAllStatusForApprover(mUserId);

        for (Status status : allStatusesListForUser) {
            Boolean isApproved = ApproverHistory.getApprovalStateForStatusAndApprover(
                    mWorkflow.getWorkflowApprovalHistory(), status.getId(), mUserId);

            if (isApproved == null) pendingStatusesForUser.add(new StatusApproval(status));
        }

        if (pendingStatusesForUser.isEmpty()) {
            //todo handle error
        } else {
            mPendingStatusListLiveData.setValue(pendingStatusesForUser);
            mShowSubmitButtonLiveData.setValue(true);
        }
    }

    protected void processMassApproval(List<StatusApproval> statusApprovalList) {
        List<StatusApproval> listToPost = new ArrayList<>();

        for (StatusApproval sa : statusApprovalList) {
            if (sa.getSelectedStatus() != null || sa.isRejected()) {
                listToPost.add(sa);
            }
        }

        if (listToPost.isEmpty()) {
            //todo handle error
        } else {
            postSendToServer(listToPost);
        }

    }

    private void postSendToServer(List<StatusApproval> statusApprovalList) {
        //we cannot use a POJO for this request because the payload has dynamic fields.
        Map<String, Object> mapBody = new HashMap<>();
        mapBody.put("workflow", mWorkflow.getId());

        Map<String, Object> statusApprovalMap = new HashMap<>();
        for (StatusApproval sa : statusApprovalList) {
            String key = String.valueOf(sa.getStatus().getId());
            Object value;
            if (sa.isRejected()) {
                value = false;
            } else {
                value = sa.getSelectedStatus().getId();
            }

            statusApprovalMap.put(key, value);
        }

        mapBody.put("workflow_approvals", statusApprovalMap);

        mEnableSubmitButtonLiveData.setValue(false);

        showLoading.setValue(true);

        Disposable disposable = mRepository
                .postMassApproval(mToken, mapBody)
                .subscribe(this::onMassApprovalSuccess, this::onMassApprovalFailure);

        mDisposables.add(disposable);
    }

    private void onMassApprovalFailure(Throwable throwable) {
        onFailure(throwable);
    }

    private void onMassApprovalSuccess(WorkflowResponse workflowResponse) {
        mHandleResultLiveData.setValue(workflowResponse.getWorkflow() != null);
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

    protected LiveData<WorkflowListItem> getObservableInitWorkflowUi() {
        if (mInitWorkflowUiLiveData == null) {
            mInitWorkflowUiLiveData = new MutableLiveData<>();
        }
        return mInitWorkflowUiLiveData;
    }

    protected LiveData<String> getObservableWorkflowTypeVersion() {
        if (mWorkflowTypeVersionLiveData == null) {
            mWorkflowTypeVersionLiveData = new MutableLiveData<>();
        }
        return mWorkflowTypeVersionLiveData;
    }

    protected LiveData<List<StatusApproval>> getObservablePendingStatusList() {
        if (mPendingStatusListLiveData == null) {
            mPendingStatusListLiveData = new MutableLiveData<>();
        }
        return mPendingStatusListLiveData;
    }

    protected LiveData<Boolean> getObservableShowSubmitButton() {
        if (mShowSubmitButtonLiveData == null) {
            mShowSubmitButtonLiveData = new MutableLiveData<>();
        }
        return mShowSubmitButtonLiveData;
    }

    protected LiveData<Boolean> getObservableEnableSubmitButton() {
        if (mEnableSubmitButtonLiveData == null) {
            mEnableSubmitButtonLiveData = new MutableLiveData<>();
        }
        return mEnableSubmitButtonLiveData;
    }

    protected LiveData<Boolean> getObservableHandleResult() {
        if (mHandleResultLiveData == null) {
            mHandleResultLiveData = new MutableLiveData<>();
        }
        return mHandleResultLiveData;
    }
}
