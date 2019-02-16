package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory;

import android.util.Log;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ApprovalHistoryViewModel extends ViewModel {

    private static final String TAG = "ApprovalHistoryVM";

    private ApprovalHistoryRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mErrorLiveData;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<List<ApproverHistory>> updateApprovalHistoryList;
    protected MutableLiveData<Boolean> hideHistoryApprovalList;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.
    private WorkflowDb mWorkflow; // Not in DB and more complete response from network.

    protected ApprovalHistoryViewModel(ApprovalHistoryRepository approvalHistoryRepository) {
        this.mRepository = approvalHistoryRepository;
        this.showLoading = new MutableLiveData<>();
        this.updateApprovalHistoryList = new MutableLiveData<>();
        this.hideHistoryApprovalList = new MutableLiveData<>();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        getWorkflow(mToken, mWorkflowListItem.getWorkflowId());
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    private void updateApproverHistoryListUi(List<ApproverHistory> approverHistoryList) {
        if (approverHistoryList == null || approverHistoryList.size() < 1) {
            hideHistoryApprovalList.setValue(true);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            ApproverHistory approverHistory;
            ProfileInvolved profileInvolved;

            for (int i = 0; i < approverHistoryList.size(); i++) {
                approverHistory = approverHistoryList.get(i);
                profileInvolved = mRepository.getProfileBy(approverHistory.approverId);
                if (profileInvolved == null) {
                    continue;
                }
                approverHistory.avatarPicture = profileInvolved.picture;
            }

            ApproverHistory.sortList(approverHistoryList);

            return approverHistoryList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        approverHistories -> updateApprovalHistoryList.setValue(approverHistories),
                        throwable -> {
                            hideHistoryApprovalList.setValue(true);
                            Log.d(TAG,
                                    "updateProfilesInvolvedUi: Something went wrong - " + throwable
                                            .getMessage());
                        });

        mDisposables.add(disposable);
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

        updateApproverHistoryListUi(mWorkflow.getWorkflowApprovalHistory());
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
}
