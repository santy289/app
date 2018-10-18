package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.Templates;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WorkflowDetailViewModel extends ViewModel {
    private MutableLiveData<WorkflowDb> mWorkflowLiveData;
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private MutableLiveData<Comment> mCommentLiveData;
    private MutableLiveData<Boolean> mAttachLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowDetailRepository repository;

    protected MutableLiveData<Boolean> showLoading;
    protected MutableLiveData<String> setCommentHeaderCounter;
    protected MutableLiveData<Boolean> showImportantInfoSection;
    protected MutableLiveData<List<Step>> loadImportantInfoSection;
    protected MutableLiveData<Boolean> showTemplateDocumentsUi;
    protected MutableLiveData<String> setTemplateTitleWith;
    protected MutableLiveData<List<DocumentsFile>> setDocumentsView;
    protected MutableLiveData<List<Approver>> updateCurrentApproversList;
    protected MutableLiveData<List<String>> updateApproveSpinner;
    protected MutableLiveData<Boolean> hideApproveSpinnerOnEmptyData;
    protected MutableLiveData<Boolean> hideApproverListOnEmptyData;


    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "DetailViewModel";

    private boolean isPrivateComment = false;
    private String token;
    private WorkflowListItem workflowListItem; // in DB but has limited data about the workflow.
    private WorkflowDb workflow; // Not in DB and more complete response from network.

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.repository = workflowDetailRepository;
        this.showLoading = new MutableLiveData<>();
        this.setCommentHeaderCounter = new MutableLiveData<>();
        this.showImportantInfoSection = new MutableLiveData<>();
        this.loadImportantInfoSection = new MutableLiveData<>();
        this.showTemplateDocumentsUi = new MutableLiveData<>();
        this.setTemplateTitleWith = new MutableLiveData<>();
        this.setDocumentsView = new MutableLiveData<>();
        this.updateCurrentApproversList = new MutableLiveData<>();
        this.updateApproveSpinner = new MutableLiveData<>();
        this.hideApproveSpinnerOnEmptyData = new MutableLiveData<>();
        this.hideApproverListOnEmptyData = new MutableLiveData<>();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.token = token;
        this.workflowListItem = workflow;
        getWorkflow(this.token, this.workflowListItem.getWorkflowId());

        getComments(this.token, this.workflowListItem.getWorkflowId());
    }

    protected void commentIsPrivate(boolean isPrivate) {
        this.isPrivateComment = isPrivate;
    }

    protected List<Preset> getPresets() {
        return presets;
    }


    private void getWorkflowType(String auth, int typeId) {
        Disposable disposable = repository
                .getWorkflowType(auth, typeId)
                .subscribe(this::onTypeSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void getWorkflow(String auth, int workflowId) {
        Disposable disposable = repository
                .getWorkflow(auth, workflowId)
                .subscribe(this::onWorkflowSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    private void getTemplate(String auth, int templateId) {
        Disposable disposable = repository
                .getTemplate(auth, templateId)
                .subscribe(this::onTemplateSuccess, this::onFailure);
        disposables.add(disposable);
    }

    protected void getFiles(String auth, int workflowId) {
        Disposable disposable = repository
                .getFiles(auth, workflowId)
                .subscribe(this::onFilesSuccess, this::onFailure);
        disposables.add(disposable);
    }

    private void getComments(String auth, int workflowId) {
        Disposable disposable = repository
                .getComments(auth, workflowId)
                .subscribe(this::onCommentsSuccess, throwable -> {
                    onFailure(throwable);
                });
        disposables.add(disposable);
    }

    public void postComment(String comment, List<CommentFile> files) {
        showLoading.setValue(true);
        Disposable disposable = repository
                .postComment(
                        token,
                        workflowListItem.getWorkflowId(),
                        comment,
                        isPrivateComment,
                        files)
                .subscribe(this::onPostCommentSuccess,
                        this::onFailure);
        disposables.add(disposable);
    }

    public void attachFile(String auth, List<WorkflowPresetsRequest> request, CommentFile fileRequest) {
        Disposable disposable = repository
                .attachFile(auth, request, fileRequest)
                .subscribe(this::onAttachSuccess, this::onFailure);
        disposables.add(disposable);
    }

    private void updateCommentCounterHeader(int counter) {
        setCommentHeaderCounter.setValue("(" + String.valueOf(counter) + ")");
    }

    /**
     * Finds the a Status from the Status List in the current WorkflowType object.
     *
     * @param statusId Status id to find.
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
     * Populates section regarding important information about a workflow.
     *
     * @param currentStatus Status used to populate the information on the UI.
     */
    private void setImportantInfoSection(Status currentStatus) {
        if (currentStatus == null || currentStatus.getSteps() == null) {
            showImportantInfoSection.setValue(false);
            return;
        }

        Collections.sort(
                currentStatus.getSteps(),
                (s1, s2) -> s1.getOrder() - s2.getOrder()
        );

        List<Step> steps = currentStatus.getSteps();
        loadImportantInfoSection.setValue(steps);
        showImportantInfoSection.setValue(true);
    }

    private List<Preset> presets;
    private void getTemplateBy(int templateId) {
        if (templateId < 1) {
            showTemplateDocumentsUi.setValue(false);
            return;
        }
        getTemplate(token, templateId);
    }

    private void setNextApprovalSection() {
        if (workflow == null || currentWorkflowType == null) {
            return;
        }


    }

    private WorkflowTypeDb currentWorkflowType;
    private Status currentStatus;

    private void onTypeSuccess(WorkflowTypeResponse response) {
        currentWorkflowType = response.getWorkflowType();
        if (currentWorkflowType == null) {
            return;
        }

        currentStatus = findStatusInListBy(workflowListItem.getCurrentStatus());
        setImportantInfoSection(currentStatus);
        getTemplateBy(currentWorkflowType.getTemplateId());
        this.presets = currentWorkflowType.getPresets();


        // Update current approvers list on UI.
        List<Approver> currentApprovers = currentStatus.getApproversList();
        updateCurrentApproverUi(currentApprovers);

        // Update approval spinner.
        List<Integer> nextStatuIds = workflow.getCurrentStatusRelations();
        updateApproveSpinnerUi(nextStatuIds);

    }

    /**
     * Updates UI section for current approvers. If this list is empty it will hide its recycler view.
     *
     * @param currentApprovers List of current approvers.
     */
    private void updateCurrentApproverUi(List<Approver> currentApprovers) {
        if (currentApprovers.size() < 1) {
            hideApproverListOnEmptyData.setValue(true);
            return;
        }
        updateCurrentApproversList.setValue(currentApprovers);
    }

    /**
     * Update the spinner in the Next Approvers section. This spinner is used to approve or reject
     * a status. It may send an empty list or all the necessary names for the spinner.
     *
     * @param nextStatusIds List of ids specified by a Workflow in order to look in a WorkflowType.
     */
    private void updateApproveSpinnerUi(List<Integer> nextStatusIds) {
        List<String> nextStatusList = new ArrayList<>();
        if (nextStatusIds.size() < 1) {
            hideApproveSpinnerOnEmptyData.setValue(true);
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


    /**
     * Handles success when requesting for a workflow by id to the endpoint.
     *
     * @param workflowResponse Network response with workflow data.
     */
    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {

        getWorkflowType(this.token, this.workflowListItem.getWorkflowTypeId());


        workflow = workflowResponse.getWorkflow();
        mWorkflowLiveData.setValue(workflowResponse.getWorkflow());

        //List<Integer> nextStatuses = workflow.getCurrentStatusRelations();

        updateProfilesInvolvedUi(workflow.getProfilesInvolved());


    }

    /**
     * Given some profile ids it will look in the profiles tables in the local database for matching
     * Profiles, and return a ProfileInvolved object with limited profile information for the UI.
     *
     * @param profilesId List of profiles to look in the database.
     */
    private void updateProfilesInvolvedUi(List<Integer> profilesId) {
        if (profilesId == null || profilesId.size() < 1) {
            return;
        }

        Disposable disposable = Observable.fromCallable(() -> {
            List<ProfileInvolved> profilesList = new ArrayList<>();
            ProfileInvolved profileInvolved;
            for (int i = 0; i < profilesId.size(); i++) {
                profileInvolved = repository.getProfileBy(profilesId.get(i));
                if (profileInvolved == null) {
                    continue;
                }
                profilesList.add(profileInvolved);
            }
            return profilesList;
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profileInvolvedList -> {
                    int test = 1;
                }, throwable -> {
                    Log.d(TAG, "updateProfilesInvolvedUi: Something went wrong - " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    private void onTemplateSuccess(TemplatesResponse templatesResponse) {
        Templates templates = templatesResponse.getTemplates();
        if (templates == null) {
            return;
        }

        setTemplateTitleWith.setValue(templates.getName());
        getFiles(token, this.workflowListItem.getWorkflowId());
    }

    private void onFilesSuccess(FilesResponse filesResponse) {
        List<DocumentsFile> documents = filesResponse.getList();
        if (documents == null) {
            return;
        }
        setDocumentsView.setValue(documents);
    }

    // TODO Remove when we finally have comments List in ViewModel and NOT in Fragment.
    private int commentsCounter = 0;

    private void onCommentsSuccess(CommentsResponse commentsResponse) {
        List<Comment> comments = commentsResponse.getResponse();

        if (comments == null) {
            showLoading.setValue(false);
            return;
        }
        commentsCounter = comments.size();
        updateCommentCounterHeader(comments.size());
        mCommentsLiveData.setValue(commentsResponse.getResponse());
        showLoading.setValue(false);
    }

    private void onPostCommentSuccess(CommentResponse commentResponse) {
        commentsCounter += 1;
        updateCommentCounterHeader(commentsCounter);
        mCommentLiveData.setValue(commentResponse.getResponse());
        showLoading.setValue(false);
    }

    private void onAttachSuccess(AttachResponse attachResponse) {
        mAttachLiveData.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<WorkflowDb> getObservableWorkflow() {
        if (mWorkflowLiveData == null) {
            mWorkflowLiveData = new MutableLiveData<>();
        }
        return mWorkflowLiveData;
    }

    public LiveData<List<Comment>> getObservableComments() {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
        }
        return mCommentsLiveData;
    }

    public LiveData<Comment> getObservableComment() {
        if (mCommentLiveData == null) {
            mCommentLiveData = new MutableLiveData<>();
        }
        return mCommentLiveData;
    }

    public LiveData<Boolean> getObservableAttach() {
        if (mAttachLiveData == null) {
            mAttachLiveData = new MutableLiveData<>();
        }
        return mAttachLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
