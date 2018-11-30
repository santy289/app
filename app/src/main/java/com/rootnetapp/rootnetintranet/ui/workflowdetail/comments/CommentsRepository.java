package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.requests.comment.PostCommentRequest;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CommentsRepository {

    private static final String TAG = "CommentsRepository";

    private MutableLiveData<Boolean> showLoading;

    private ApiInterface service;

    private final CompositeDisposable disposables = new CompositeDisposable();

    protected CommentsRepository(ApiInterface service) {
        this.service = service;
    }

    protected void clearDisposables() {
        disposables.clear();
    }

    protected Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<CommentsResponse> getComments(String auth, int workflowId) {
        return service.getComments(auth, workflowId, 10, 1).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<CommentResponse> postComment(String auth, int workflowId, String comment,
                                                       boolean isPrivate, List<CommentFile> files) {
        PostCommentRequest request = new PostCommentRequest();
        request.setDescription(comment);
        request.setIsPrivate(isPrivate);
        request.setCommentFiles(files);

        return service.postComment(
                auth,
                workflowId,
                request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
