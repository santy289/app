package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

import android.util.Log;

import com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_ALL;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_MY_OWN;
import static com.rootnetapp.rootnetintranet.commons.RootnetPermissionsUtils.WORKFLOW_EDIT_OWN;

public class PeopleInvolvedViewModel extends ViewModel {

    private static final String TAG = "PeopleInvolvedViewModel";

    private PeopleInvolvedRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> showToastMessage;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<List<ProfileInvolved>> updateProfilesInvolved;
    protected MutableLiveData<Boolean> hideProfilesInvolvedList;
    protected MutableLiveData<Boolean> showEditButtonLiveData;
    private WorkflowListItem mWorkflowListItem;

    protected PeopleInvolvedViewModel(PeopleInvolvedRepository peopleInvolvedRepository) {
        this.mRepository = peopleInvolvedRepository;
        this.showLoading = new MutableLiveData<>();
        this.updateProfilesInvolved = new MutableLiveData<>();
        this.hideProfilesInvolvedList = new MutableLiveData<>();
        this.showEditButtonLiveData = new MutableLiveData<>();
    }

    protected void initDetails(String token, WorkflowListItem workflow, String userId, String userPermissions) {
        // in DB but has limited data about the workflow.
        mWorkflowListItem = workflow;
        getWorkflow(token, workflow.getWorkflowId());
        checkEditPermissions(userId == null ? 0 : Integer.parseInt(userId), userPermissions);
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
    }

    private void checkEditPermissions(int userId, String permissionsString) {
        List<String> permissionsToCheck = new ArrayList<>();

        if (mWorkflowListItem.getOwnerId() == userId) {
            permissionsToCheck.add(WORKFLOW_EDIT_MY_OWN);
            permissionsToCheck.add(WORKFLOW_EDIT_OWN);
        } else {
            permissionsToCheck.add(WORKFLOW_EDIT_ALL);
        }

        permissionsString = "";

        RootnetPermissionsUtils permissionsUtils = new RootnetPermissionsUtils(permissionsString);
        boolean hasEditPermissions = permissionsUtils.hasPermissions(permissionsToCheck);
        showEditButtonLiveData.setValue(hasEditPermissions);
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
        // Not in DB and more complete response from network.
        WorkflowDb mWorkflow = workflowResponse.getWorkflow();
        updateUIWithWorkflow(mWorkflow);
    }

    /**
     * Update the activation status (Open/Closed) and the tie state of the current status.
     *
     * @param workflow current workflow.
     */
    private void updateUIWithWorkflow(WorkflowDb workflow) {
        updateProfilesInvolvedUi(workflow.getProfilesInvolved());
    }

    /**
     * Given some profile ids it will look in the profiles tables in the local database for matching
     * Profiles, and return a ProfileInvolved object with limited profile information for the UI. It
     * will look for those profiles in the background thread.
     *
     * @param profilesId List of profiles to look in the database.
     */
    private void updateProfilesInvolvedUi(List<Integer> profilesId) {
        if (profilesId == null || profilesId.size() < 1) {
            hideProfilesInvolvedList.setValue(true);
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            List<ProfileInvolved> profilesList = new ArrayList<>();
            ProfileInvolved profileInvolved;
            for (int i = 0; i < profilesId.size(); i++) {
                profileInvolved = mRepository.getProfileBy(profilesId.get(i));
                if (profileInvolved == null) {
                    continue;
                }
                profilesList.add(profileInvolved);
            }
            return profilesList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setProfilesInvovledOnUi, throwable -> Log
                        .d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable
                                .getMessage()));
        mDisposables.add(disposable);
    }

    /**
     * Sends back to the View a list of profiles that are involved to the current workflow.
     *
     * @param profiles Profiles to be used for UI list.
     */
    private void setProfilesInvovledOnUi(List<ProfileInvolved> profiles) {
        if (profiles == null || profiles.size() < 1) {
            hideProfilesInvolvedList.setValue(true);
            return;
        }
        updateProfilesInvolved.setValue(profiles);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        showToastMessage.setValue(Utils.getOnFailureStringRes(throwable));
    }

    protected LiveData<Integer> getObservableShowToastMessage() {
        if (showToastMessage == null) {
            showToastMessage = new MutableLiveData<>();
        }
        return showToastMessage;
    }
}
