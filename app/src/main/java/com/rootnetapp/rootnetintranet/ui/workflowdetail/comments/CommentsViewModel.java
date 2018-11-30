package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

public class CommentsViewModel extends ViewModel {

    private static final String TAG = "CommentsViewModel";

    protected static final int REQUEST_FILE_TO_ATTACH = 555;

    private CommentsRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<List<Comment>> mCommentsLiveData;
    private MutableLiveData<Comment> mCommentLiveData;
    private MutableLiveData<Boolean> mHideComments;
    private MutableLiveData<Integer> mCommentsTabCounter;
    private MutableLiveData<Boolean> mEnableCommentButton;
    private MutableLiveData<CommentFile> mNewCommentFileLiveData;

    protected MutableLiveData<Boolean> showLoading;

    private boolean isPrivateComment = false;

    private String mToken;
    private WorkflowListItem mWorkflowListItem; // in DB but has limited data about the workflow.
    private List<CommentFile> mCommentFiles;

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

    /**
     * Handles the result of the file chooser intent. Retrieves information about the selected file
     * and sends that info to the UI. Also, adds the file to {@link #mCommentFiles} list that will
     * be used in {@link #postComment(String, List)}.
     *
     * @param context     used to retrieve the file name and size.
     * @param requestCode ActivityResult requestCode.
     * @param resultCode  ActivityResult resultCode.
     * @param data        the file URI that was selected.
     */
    protected void handleFileSelectedResult(Context context, int requestCode, int resultCode,
                                            Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_TO_ATTACH:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();

                        if (uri == null) {
                            mToastMessageLiveData.setValue(R.string.select_file);
                            return;
                        }

                        Cursor returnCursor = context.getContentResolver()
                                .query(uri, null, null, null, null);

                        if (returnCursor == null) {
                            mToastMessageLiveData.setValue(R.string.error_selecting_file);
                            return;
                        }

                        returnCursor.moveToFirst();

                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        int size = (int) returnCursor.getLong(sizeIndex);

                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        String fileName = returnCursor.getString(nameIndex);

                        returnCursor.close();

                        File file = new File(uri.toString());
                        byte[] bytes = Utils.fileToByte(file);

                        String encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
                        String fileType = Utils.getMimeType(data.getData(), context);

                        if (mCommentFiles == null) mCommentFiles = new ArrayList<>();

                        CommentFile commentFile = new CommentFile(encodedFile, fileType, fileName,
                                size);
                        mCommentFiles.add(commentFile);
                        mNewCommentFileLiveData.setValue(commentFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    protected void removeCommentAttachment(CommentFile commentFile){
        mCommentFiles.remove(commentFile);
    }

    // TODO Remove when we finally have comments List in ViewModel and NOT in Fragment.
    private int commentsCounter = 0;

    private void onCommentsSuccess(CommentsResponse commentsResponse) {
        List<Comment> comments = commentsResponse.getResponse();

        if (comments == null) {
            showLoading.setValue(false);
            mHideComments.setValue(true);
            return;
        }
        commentsCounter = comments.size();
        setCommentsTabCounter(comments.size());
        mCommentsLiveData.setValue(commentsResponse.getResponse());
        showLoading.setValue(false);
        mHideComments.setValue(false);
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
        mToastMessageLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
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

    protected LiveData<CommentFile> getObservableNewCommentFile() {
        if (mNewCommentFileLiveData == null) {
            mNewCommentFileLiveData = new MutableLiveData<>();
        }
        return mNewCommentFileLiveData;
    }
}
