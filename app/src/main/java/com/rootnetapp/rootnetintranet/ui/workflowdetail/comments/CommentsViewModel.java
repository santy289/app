package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CommentsViewModel extends ViewModel {

    private static final String TAG = "CommentsViewModel";

    private CommentsRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private MutableLiveData<Comment> mCommentLiveData;
    private MutableLiveData<Boolean> mHideComments;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Boolean> mEnableCommentButton;

    protected MutableLiveData<Boolean> showLoading;

    private boolean isPrivateComment = false;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.

    protected CommentsViewModel(CommentsRepository commentsRepository) {
        this.mRepository = commentsRepository;
        this.showLoading = new MutableLiveData<>();
    }

    protected void initDetails(String token, WorkflowListItem workflow) {
        this.mToken = token;
        this.mWorkflowListItem = workflow;
        getComments(mToken, mWorkflowListItem.getWorkflowId());
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
        mRepository.clearDisposables();
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
        setCommentsTabCounter(comments.size());
        mCommentsLiveData.setValue(commentsResponse.getResponse());
        showLoading.setValue(false);
    }

    private void onPostCommentSuccess(CommentResponse commentResponse) {
        commentsCounter += 1;
        setCommentsTabCounter(commentsCounter);
        mCommentLiveData.setValue(commentResponse.getResponse());
        showLoading.setValue(false);
        mEnableCommentButton.setValue(true);
    }

    private void getComments(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getComments(auth, workflowId)
                .subscribe(this::onCommentsSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    protected void postComment(String comment, List<CommentFile> files) {
        mEnableCommentButton.setValue(false);
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .postComment(
                        mToken,
                        mWorkflowListItem.getWorkflowId(),
                        comment,
                        isPrivateComment,
                        files)
                .subscribe(this::onPostCommentSuccess,
                        this::onFailure);
        mDisposables.add(disposable);
    }

    protected void setPrivateComment(boolean isPrivate) {
        this.isPrivateComment = isPrivate;
    }

    private void setCommentsTabCounter(int counter) {
        mCommentsTabCounter.setValue(counter);
    }

    private void onFailure(Throwable throwable) {
        showLoading.setValue(false);
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<List<Comment>> getObservableComments() {
        if (mCommentsLiveData == null) {
            mCommentsLiveData = new MutableLiveData<>();
        }
        return mCommentsLiveData;
    }

    protected LiveData<Comment> getObservableComment() {
        if (mCommentLiveData == null) {
            mCommentLiveData = new MutableLiveData<>();
        }
        return mCommentLiveData;
    }

    protected LiveData<Boolean> getObservableHideComments() {
        if (mHideComments == null) {
            mHideComments = new MutableLiveData<>();
        }
        return mHideComments;
    }

    protected LiveData<Integer> getObservableCommentsTabCounter() {
        if (mCommentsTabCounter == null) {
            mCommentsTabCounter = new MutableLiveData<>();
        }
        return mCommentsTabCounter;
    }

    protected LiveData<Boolean> getObservableEnableCommentButton() {
        if (mEnableCommentButton == null) {
            mEnableCommentButton = new MutableLiveData<>();
        }
        return mEnableCommentButton;
    }
}
