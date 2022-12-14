package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.createworkflow.SpecificApprovers;
import com.rootnetapp.rootnetintranet.models.createworkflow.StatusSpecific;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.massapproval.MassApprovalViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment.INDEX_CURRENT_STATUS;
import static com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment.INDEX_LAST_STATUS;
import static com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment.INDEX_NEXT_STATUS;

public class StatusViewModel extends ViewModel {

    private static final String TAG = "StatusViewModel";

    private StatusRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Integer> showToastMessage;
    private MutableLiveData<Boolean> mTieStatusLiveData;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<Boolean> showInitialLoading;
    protected MutableLiveData<List<Approver>> updateCurrentApproversList;
    protected MutableLiveData<List<String>> updateApproveSpinner;
    protected MutableLiveData<Boolean> hideApproveSpinnerOnEmptyData;
    protected MutableLiveData<Boolean> hideApproveSpinnerOnNotApprover;
    protected MutableLiveData<Boolean> hideApproverListOnEmptyData;
    protected MutableLiveData<Boolean> hideMassApprovalLiveData;
    protected MutableLiveData<Boolean> mEnableApproveRejectButtonsLiveData;
    protected MutableLiveData<String[]> updateStatusUi;
    protected LiveData<String[]> updateStatusUiFromUserAction;
    protected LiveData<Boolean> handleShowLoadingByRepo;
    protected MutableLiveData<StatusUiData> setWorkflowIsOpen;

    private @Nullable Integer mApproveSpinnerItemSelection;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.
    private int mUserId;
    private StatusUiData mStatusUiData;

    protected StatusViewModel(StatusRepository statusRepository) {
        this.mRepository = statusRepository;
        this.showLoading = new MutableLiveData<>();
        this.showInitialLoading = new MutableLiveData<>();
        this.updateCurrentApproversList = new MutableLiveData<>();
        this.updateApproveSpinner = new MutableLiveData<>();
        this.hideApproveSpinnerOnEmptyData = new MutableLiveData<>();
        this.hideApproveSpinnerOnNotApprover = new MutableLiveData<>();
        this.hideApproverListOnEmptyData = new MutableLiveData<>();
        this.setWorkflowIsOpen = new MutableLiveData<>();
        this.updateStatusUi = new MutableLiveData<>();
        this.hideMassApprovalLiveData = new MutableLiveData<>();

        subscribe();
    }

    protected void initDetails(String token, WorkflowListItem workflow, String loggedUserId) {
        mToken = token;
        mWorkflowListItem = workflow;
        mUserId = loggedUserId == null ? 0 : Integer.parseInt(loggedUserId);

        showInitialLoading.setValue(true);
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
    }

    protected void updateInfo() {
        if (mWorkflowListItem == null) return;

        showInitialLoading.setValue(true);
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    /**
     * This subscribe function will make map transformations to observe LiveData objects in the
     * repository. Here we will handle all incoming data from the repo.
     */
    private void subscribe() {
        // Transformation for observing approval and rejection of workflows.
        updateStatusUiFromUserAction = Transformations.map(
                mRepository.getApproveRejectResponse(),
                approvalResponse -> {
                    // transform WorkflowApproveRejectResponse to String[]

                    mEnableApproveRejectButtonsLiveData.setValue(true);
                    showLoading.setValue(false);

                    //WorkflowDb incomingWorkflow = approvalResponse.getWorkflow();
                    mWorkflow = approvalResponse.getWorkflow();
                    String[] statuses = buildArrayForStatusUpdate(mWorkflow);

                    updateUIWithWorkflow(mWorkflow);
                    currentWorkflowType = mWorkflow.getWorkflowType();

                    updateUIWithWorkflowType(currentWorkflowType, mWorkflow.getCurrentStatus());

                    showToastMessage.setValue(R.string.request_successfully);
                    return statuses;
                }
        );

        // Transformation used in case that a workflow approval or rejection fails.
        handleShowLoadingByRepo = Transformations.map(
                mRepository.getErrorLiveData(),
                throwable -> {
                    mEnableApproveRejectButtonsLiveData.setValue(true);
                    showLoading.setValue(false);
                    showToastMessage.setValue(Utils.getOnFailureStringRes(throwable));
                    return false;
                }
        );
    }

    /**
     * It generates an array of strings that hold values for last, current, next status labels. This
     * array is eventually sent to the UI for updating the respective Views.
     *
     * @param incomingWorkflow WorkflowDB object that holds info that we need.
     *
     * @return returns a String[] array with all the labels that we need to update the UI.
     */
    private String[] buildArrayForStatusUpdate(WorkflowDb incomingWorkflow) {
        String[] statuses = new String[3];
        String currentStatus = incomingWorkflow.getCurrentStatusName();
        if (TextUtils.isEmpty(currentStatus)) {
            currentStatus = "";
        }

        statuses[INDEX_LAST_STATUS] = getLastStatusLabel(incomingWorkflow,
                currentWorkflowType.getStatus());

        // if there isn't a last status, it's because we are on the initial status, so we set the
        // last status label to the initial status (current status) as well.
        if (statuses[INDEX_LAST_STATUS].isEmpty()) statuses[INDEX_LAST_STATUS] = currentStatus;

        statuses[INDEX_CURRENT_STATUS] = currentStatus;
        statuses[INDEX_NEXT_STATUS] = getNextStatuses(incomingWorkflow, currentWorkflowType);
        return statuses;
    }

    /**
     * It calls getNextStatusLabel to get a string. This is a setup function to prepare and validate
     * all the variables before we call getNextStatusLabel().
     *
     * @param workflow       WorkflowDb object that we need to check and get status ids from.
     * @param workflowTypeDb WorkflowTypeDb object to validate and get status ids from.
     *
     * @return Returns a label to use to update the Next Status view on the UI.
     */
    private String getNextStatuses(WorkflowDb workflow, WorkflowTypeDb workflowTypeDb) {
        if (workflowTypeDb == null || workflow == null) {
            return "";
        }

        List<Integer> nextStatusIds = workflow.getCurrentStatusRelations();
        List<Status> allStatuses = workflowTypeDb.getStatus();
        return getNextStatusLabel(nextStatusIds, allStatuses);
    }

    /**
     * Get the last status label from by checking all statuses in AllStatuses list and comparing it
     * to the current status in workflow to get any related statuses.
     *
     * @param workflow    WorkflowDb object that has the current status.
     * @param allStatuses List with all the statuses to check and find related Status to the current
     *                    status.
     *
     * @return Returns the last status label to use on the UI.
     */
    private String getLastStatusLabel(@NonNull WorkflowDb workflow, List<Status> allStatuses) {
        if (allStatuses == null || allStatuses.size() < 1) {
            return "";
        }

        int currentStatusId = workflow.getCurrentStatus();
        Status status;
        List<Integer> relations;
        Integer relatedId;
        boolean firstTry = true;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < allStatuses.size(); i++) {
            status = allStatuses.get(i);
            relations = status.getRelations();
            if (relations == null) {
                continue;
            }
            for (int j = 0; j < relations.size(); j++) {
                relatedId = relations.get(j);
                if (relatedId == currentStatusId) {
                    if (!firstTry) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(status.getName());
                    firstTry = false;
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private String getNextStatusLabel(List<Integer> nextStatusIds, List<Status> allStatuses) {
        if (nextStatusIds == null || nextStatusIds.size() == 0) {
            return "";
        }

        Status status;
        StringBuilder nextStatusLabel = new StringBuilder();
        int nextStatusId;
        boolean firstTry = true;
        for (int i = 0; i < nextStatusIds.size(); i++) {
            nextStatusId = nextStatusIds.get(i);
            for (int j = 0; j < allStatuses.size(); j++) {
                status = allStatuses.get(j);
                if (status.getId() == nextStatusId) {
                    if (!firstTry) {
                        nextStatusLabel.append(", ");
                    }
                    nextStatusLabel.append(status.getName());
                    firstTry = false;
                    break;
                }
            }
        }
        return nextStatusLabel.toString();
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

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Finds the a Status from the Status List in the current WorkflowType object.
     *
     * @param statusId Status id to find.
     *
     * @return Returns a Status object or null if it doesn't find anything.
     */
    private Status findStatusInListBy(int statusId) {
        List<Status> statusList = currentWorkflowType.getStatus();
        if (statusList == null || statusList.size() < 1) {
            return null;
        }

        Status status;
        for (int i = 0; i < statusId; i++) {
            status = statusList.get(i);
            if (status.getId() == statusId) {
                return status;
            }
        }

        return null;
    }

    /**
     * Calls /approve endpoint and does a Post request to either approve or reject a workflow.
     */
    private void handleApproveOrRejectAction(int selectedItemIndex, boolean approve) {
        mEnableApproveRejectButtonsLiveData.setValue(false);
        showLoading.setValue(true);

        List<Integer> nextStatusIds = mWorkflow.getCurrentStatusRelations();
        int nextStatusId = nextStatusIds.get(selectedItemIndex);
        mRepository.approveWorkflow(mToken, mWorkflow.getId(), approve, nextStatusId);
    }

    protected void handleApproveAction() {
        if (mApproveSpinnerItemSelection == null) {
            showToastMessage.setValue(R.string.workflow_detail_status_fragment_must_select_step);
            return;
        }

        handleApproveOrRejectAction(mApproveSpinnerItemSelection, true);
    }

    protected void handleRejectAction() {
        if (mApproveSpinnerItemSelection == null) {
            showToastMessage.setValue(R.string.workflow_detail_status_fragment_must_select_step);
            return;
        }

        handleApproveOrRejectAction(mApproveSpinnerItemSelection, false);
    }

    private WorkflowTypeDb currentWorkflowType;
    //private Status currentStatus; //TODO make sure we are not using this variable in functions and in here updateUIWithWorkflowType().

    /**
     * Handles success response from endpoint when looking for a workflow type.
     *
     * @param response Incoming response from server.
     */
    private void onTypeSuccess(WorkflowTypeResponse response) {
        showInitialLoading.setValue(false);

        currentWorkflowType = response.getWorkflowType();
        if (currentWorkflowType == null) {
            return;
        }
        updateUIWithWorkflowType(currentWorkflowType, mWorkflowListItem.getCurrentStatus());
    }

    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mWorkflow = workflowResponse.getWorkflow();
        getWorkflowType(mToken, mWorkflowListItem.getWorkflowTypeId());
        updateUIWithWorkflow(mWorkflow);
    }

    /**
     * Update the activation status (Open/Closed) and the tie state of the current status.
     *
     * @param workflow current workflow.
     */
    private void updateUIWithWorkflow(WorkflowDb workflow) {
        boolean isTied = workflow.getNextStatusRequirements().isApproveTie()
                && workflow.getNextStatusRequirements().getApprovedCount() == workflow
                .getNextStatusRequirements().getRejectedCount();
        mTieStatusLiveData.setValue(isTied);
        if (isTied) {
            showToastMessage.setValue(
                    R.string.workflow_detail_status_fragment_status_summary_tied_status_message);
        }

        updateStatusUiData(mWorkflow.isOpen());
    }

    private void updateUIWithWorkflowType(WorkflowTypeDb currentWorkflowType, int statusId) {
        Status currentStatus = findStatusInListBy(statusId);

        updateStatusUi.setValue(buildArrayForStatusUpdate(mWorkflow));

        // Update current approvers list on UI.
        List<Approver> typeConfigurationApprovers = currentStatus.getApproversList();
        for (Approver approver : typeConfigurationApprovers) {
            approver.approved = ApproverHistory.getApprovalStateForStatusAndApprover(
                    mWorkflow.getWorkflowApprovalHistory(), approver.statusId, approver.entityId);
        }
        SpecificApprovers currentSpecificApprovers = mWorkflow.getCurrentSpecificApprovers();

        updateCurrentApproverUi(typeConfigurationApprovers, currentSpecificApprovers);

        // Update approval spinner.
        List<Integer> nextStatusIds = mWorkflow.getCurrentStatusRelations();
        updateApproveSpinnerUi(mWorkflow, nextStatusIds);

        // Check Mass Approval
        hideMassApprovalLiveData.setValue(MassApprovalViewModel.getPendingStatusesForUser(currentWorkflowType, mWorkflow, mUserId).isEmpty());
    }

    /**
     * Updates UI section for current approvers. If this list is empty it will hide its recycler
     * view.
     *
     * @param typeConfigurationApprovers List of approvers.
     * @param currentSpecificApprovers   List of current approvers.
     */
    private void updateCurrentApproverUi(List<Approver> typeConfigurationApprovers,
                                         SpecificApprovers currentSpecificApprovers) {
        List<Approver> result = new ArrayList<>();

        if (typeConfigurationApprovers.size() > 0) {
            result = typeConfigurationApprovers;
        }

        List<Integer> globalList = currentSpecificApprovers.global;
        List<StatusSpecific> statusSpecificList = currentSpecificApprovers.statusSpecific;

        // TODO look for RxJava chaining instead of calling multiple functions.
        generateApproverListForProfileIds(globalList, statusSpecificList, result);
    }

    private void generateApproverListForProfileIds(List<Integer> globalList,
                                                   List<StatusSpecific> statusSpecificList,
                                                   List<Approver> approverList) {
        if (globalList == null || globalList.size() < 1) {
            generateApproverListForStatusSpecificIds(statusSpecificList, approverList);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            Approver approver;
            ProfileInvolved profileInvolved;
            for (int i = 0; i < globalList.size(); i++) {
                int id = globalList.get(i);

                profileInvolved = mRepository.getProfileBy(id);
                if (profileInvolved == null) {
                    continue;
                }
                approver = generateApproverWith(profileInvolved);
                approver.entityId = id;
                approver.approved = ApproverHistory.getApprovalStateForStatusAndApprover(
                        mWorkflow.getWorkflowApprovalHistory(), approver.statusId,
                        approver.entityId);
                approver.isGlobal = true;

                approverList.add(approver);
            }
            return approverList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approverListResult -> generateApproverListForStatusSpecificIds(
                        statusSpecificList,
                        approverListResult), throwable -> {
                    generateApproverListForStatusSpecificIds(statusSpecificList, approverList);
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable
                            .getMessage());
                });

        mDisposables.add(disposable);
    }

    protected Approver generateApproverWith(ProfileInvolved profileInvolved) {
        Approver approver = new Approver();
        approver.isRequire = false;
        approver.entityAvatar = profileInvolved.picture;
        approver.canChangeMind = false;
        approver.entityName = profileInvolved.fullName;
        return approver;
    }

    private void generateApproverListForStatusSpecificIds(List<StatusSpecific> statusSpecificList,
                                                          List<Approver> approverList) {
        if (statusSpecificList == null || statusSpecificList.size() < 1) {
            if (approverList.size() < 1) {
                hideApproverListOnEmptyData.setValue(true);
            } else {
                updateCurrentApproversList.setValue(approverList);
            }
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            Approver approver;
            ProfileInvolved profileInvolved;
            for (int i = 0; i < statusSpecificList.size(); i++) {
                int id = statusSpecificList.get(i).user;
                profileInvolved = mRepository.getProfileBy(id);
                if (profileInvolved == null) {
                    continue;
                }
                approver = generateApproverWith(profileInvolved);

                approver.entityId = id;
                approver.approved = ApproverHistory.getApprovalStateForStatusAndApprover(
                        mWorkflow.getWorkflowApprovalHistory(), approver.statusId,
                        approver.entityId);
                approver.isStatusSpecific = true;

                approverList.add(approver);
            }
            return approverList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approverListResult -> {
                    if (approverListResult.size() < 1) {
                        hideApproverListOnEmptyData.setValue(true);
                    } else {
                        updateCurrentApproversList.setValue(approverListResult);
                    }
                }, throwable -> {
                    if (approverList.size() < 1) {
                        hideApproverListOnEmptyData.setValue(true);
                    } else {
                        updateCurrentApproversList.setValue(approverList);
                    }
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable
                            .getMessage());
                });

        mDisposables.add(disposable);
    }

    /**
     * Update the spinner in the Next Approvers section. This spinner is used to approve or reject a
     * status. It may send an empty list or all the necessary names for the spinner.
     *
     * @param workflow      Current workflow.
     * @param nextStatusIds List of ids specified by a Workflow in order to look in a WorkflowType.
     */
    private void updateApproveSpinnerUi(WorkflowDb workflow, List<Integer> nextStatusIds) {
        List<String> nextStatusList = new ArrayList<>();
        if (nextStatusIds.isEmpty()) {
            hideApproveSpinnerOnEmptyData.setValue(true);
            return;
        }

        if (!workflow.isLoggedIsApprover()) {
            hideApproveSpinnerOnNotApprover.setValue(true);
            return;
        }

        Status status;
        String name;
        for (int i = 0; i < nextStatusIds.size(); i++) {
            status = findStatusInListBy(nextStatusIds.get(i));
            if (status == null) {
                continue;
            }
            name = status.getName();
            if (name == null) {
                continue;
            }
            nextStatusList.add(name);
        }

        updateApproveSpinner.setValue(nextStatusList);
    }

    protected void setApproveSpinnerItemSelection(@Nullable Integer approveSpinnerItemSelection) {
        this.mApproveSpinnerItemSelection = approveSpinnerItemSelection;
    }

    /**
     * Sends the parameters to modify the UI regarding the open/closed status.
     *
     * @param isOpen whether the workflow is open or closed.
     */
    protected void updateStatusUiData(boolean isOpen) {
        int stringRes = getSwitchStatusStringRes(isOpen);
        int colorRes = getSwitchStatusColorRes(isOpen);
        mStatusUiData = new StatusUiData(isOpen, stringRes, colorRes);

        setWorkflowIsOpen.setValue(mStatusUiData);
    }

    private int getSwitchStatusStringRes(boolean isOpen) {
        return isOpen ? R.string.open : R.string.closed;
    }

    private int getSwitchStatusColorRes(boolean isOpen) {
        return isOpen ? R.color.green : R.color.text_red;
    }

    private void onFailure(Throwable throwable) {
        if (throwable instanceof HttpException) {
            int httpCode = ((HttpException) throwable).code();
            if (httpCode == 404) {
                //not found is handled by Activity
                return;
            }
        }

        showInitialLoading.setValue(false);
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
        if (showToastMessage == null) {
            showToastMessage = new MutableLiveData<>();
        }
        return showToastMessage;
    }

    protected LiveData<Boolean> getObservableTieStatus() {
        if (mTieStatusLiveData == null) {
            mTieStatusLiveData = new MutableLiveData<>();
        }
        return mTieStatusLiveData;
    }

    protected LiveData<Boolean> getObservableEnableApproveRejectButtons() {
        if (mEnableApproveRejectButtonsLiveData == null) {
            mEnableApproveRejectButtonsLiveData = new MutableLiveData<>();
        }
        return mEnableApproveRejectButtonsLiveData;
    }
}
