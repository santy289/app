package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.files.WorkflowPresetsRequest;
import com.rootnetapp.rootnetintranet.models.responses.attach.AttachResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.file.FilesResponse;
import com.rootnetapp.rootnetintranet.models.responses.templates.TemplatesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomSpinner;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WorkflowDetailRepository {

    private AppDatabase database;
    private ApiInterface service;

    public WorkflowDetailRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    public Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<TemplatesResponse> getTemplate(String auth, int templateId) {
        return service.getTemplate(auth, templateId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FilesResponse> getFiles(String auth, int workflowId) {
        return service.getFiles(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentsResponse> getComments(String auth, int workflowId) {
        return service.getComments(auth, workflowId, 10, 1).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CommentResponse> postComment(String auth, int workflowId, String comment, boolean isPrivate, List<CommentFile> files) {
        return service.postComment(
                auth,
                workflowId,
                comment,
                isPrivate,
                files)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AttachResponse> attachFile(String auth, List<WorkflowPresetsRequest> request, CommentFile fileRequest) {
        return service.attachFile(auth, request, fileRequest).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
