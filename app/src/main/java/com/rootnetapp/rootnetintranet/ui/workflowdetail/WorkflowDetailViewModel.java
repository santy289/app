package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
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
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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

    private final CompositeDisposable disposables = new CompositeDisposable();

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
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.token = token;
        this.workflowListItem = workflow;
        getWorkflow(this.token, this.workflowListItem.getWorkflowId());
        getWorkflowType(this.token, this.workflowListItem.getWorkflowTypeId());
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

    private Status findStatusInListBy(int statusId) {
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

    private void setImportantInfoSection(Status currentStatus) {
        if (currentStatus == null || currentStatus.getSteps() == null) {
            // TODO make important info section gone
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

    private void populateNextApprovalSection() {
        if (workflow == null || workflowType == null) {
            return;
        }


    }

    private List<Status> statusList;
    WorkflowType workflowType;
    private void onTypeSuccess(WorkflowTypeResponse response) {
        workflowType = response.getWorkflowType();
        if (workflowType == null) {
            return;
        }

        this.statusList = workflowType.getStatus();
        Status currentStatus = findStatusInListBy(workflowListItem.getCurrentStatus());
        setImportantInfoSection(currentStatus);
        getTemplateBy(workflowType.getTemplateId());
        this.presets = workflowType.getPresets();

        // TODO now we have type and workflow so we can populate those people there.







    }


    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        workflow = workflowResponse.getWorkflow();
        mWorkflowLiveData.setValue(workflowResponse.getWorkflow());

        List<Integer> nextStatuses = workflow.getCurrentStatusRelations();
        // TODO populate Next status Section with select box to choose the next status based on the info in nextStatuses.
        // TODO look into WorkflowType and into the list of Status and find this ID and then populate with name this select box.
















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
            return;
        }
        commentsCounter = comments.size();
        updateCommentCounterHeader(comments.size());
        mCommentsLiveData.setValue(commentsResponse.getResponse());
    }

    private void onPostCommentSuccess(CommentResponse commentResponse) {
        showLoading.setValue(false);
        commentsCounter += 1;
        updateCommentCounterHeader(commentsCounter);
        mCommentLiveData.setValue(commentResponse.getResponse());
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
