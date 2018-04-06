package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
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
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.util.List;

/**
 * Created by root on 02/04/18.
 */

public class WorkflowDetailViewModel extends ViewModel {

    private MutableLiveData<WorkflowType> mTypeLiveData;
    private MutableLiveData<Workflow> mWorkflowLiveData;
    private MutableLiveData<Templates> mTemplateLiveData;
    private MutableLiveData<List<DocumentsFile>> mFilesLiveData;
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private MutableLiveData<Comment> mCommentLiveData;
    private MutableLiveData<Boolean> mAttachLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowDetailRepository repository;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+ Utils.testToken;

    public WorkflowDetailViewModel(WorkflowDetailRepository workflowDetailRepository) {
        this.repository = workflowDetailRepository;
    }

    public void getWorkflowType(String auth, int typeId) {
        repository.getWorkflowType(auth2, typeId).subscribe(this::onTypeSuccess, this::onFailure);
    }

    public void getWorkflow(String auth, int workflowId) {
        repository.getWorkflow(auth2, workflowId).subscribe(this::onWorkflowSuccess, this::onFailure);
    }

    public void getTemplate(String auth, int templateId) {
        repository.getTemplate(auth2, templateId).subscribe(this::onTemplateSuccess, this::onFailure);
    }

    public void getFiles(String auth, int workflowId) {
        repository.getFiles(auth2, workflowId).subscribe(this::onFilesSuccess, this::onFailure);
    }

    public void getComments(String auth, int workflowId) {
        repository.getComments(auth2, workflowId).subscribe(this::onCommentsSuccess, this::onFailure);
    }

    public void postComment(String auth, int workflowId, String comment, List<CommentFile> files) {
        repository.postComment(auth2, workflowId, comment, files).subscribe(this::onCommentSuccess, this::onFailure);
    }

    public void attachFile(String auth, List<WorkflowPresetsRequest> request, CommentFile fileRequest) {
        repository.attachFile(auth2, request, fileRequest).subscribe(this::onAttachSuccess, this::onFailure);
    }

    private void onTypeSuccess(WorkflowTypeResponse response) {
        mTypeLiveData.setValue(response.getWorkflowType());
    }

    private void onWorkflowSuccess(WorkflowResponse workflowResponse) {
        mWorkflowLiveData.setValue(workflowResponse.getWorkflow());
    }

    private void onTemplateSuccess(TemplatesResponse templatesResponse) {
        mTemplateLiveData.setValue(templatesResponse.getTemplates());
    }

    private void onFilesSuccess(FilesResponse filesResponse) {
        mFilesLiveData.setValue(filesResponse.getList());
    }

    private void onCommentsSuccess(CommentsResponse commentsResponse) {
        mCommentsLiveData.setValue(commentsResponse.getResponse());
    }

    private void onCommentSuccess(CommentResponse commentResponse) {
        mCommentLiveData.setValue(commentResponse.getResponse());
    }

    private void onAttachSuccess(AttachResponse attachResponse) {
        mAttachLiveData.setValue(true);
    }

    private void onFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<WorkflowType> getObservableType() {
        if (mTypeLiveData == null) {
            mTypeLiveData = new MutableLiveData<>();
        }
        return mTypeLiveData;
    }

    protected LiveData<Workflow> getObservableWorkflow() {
        if (mWorkflowLiveData == null) {
            mWorkflowLiveData = new MutableLiveData<>();
        }
        return mWorkflowLiveData;
    }

    public LiveData<Templates> getObservableTemplate() {
        if (mTemplateLiveData == null) {
            mTemplateLiveData = new MutableLiveData<>();
        }
        return mTemplateLiveData;
    }

    public LiveData<List<DocumentsFile>> getObservableFiles() {
        if (mFilesLiveData == null) {
            mFilesLiveData = new MutableLiveData<>();
        }
        return mFilesLiveData;
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
