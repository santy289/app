package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentResponse;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentsResponse;
import com.rootnetapp.rootnetintranet.models.responses.downloadfile.DownloadFileResponse;

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
    private MutableLiveData<Boolean> mClearAttachments;
    private MutableLiveData<AttachmentUiData> mOpenDownloadedAttachmentLiveData;

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
     * be used in {@link #postComment(String)}.
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

                        File file = new File(uri.getPath());
                        byte[] bytes = Utils.fileToByte(file);

                        //fixme this actually encodes the file path and not the file content
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

    protected void removeCommentAttachment(CommentFile commentFile) {
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

        mCommentFiles.clear();
        mClearAttachments.setValue(true);

        showLoading.setValue(false);
        mEnableCommentButton.setValue(true);
    }

    private void getComments(String auth, int workflowId) {
        Disposable disposable = mRepository
                .getComments(auth, workflowId)
                .subscribe(this::onCommentsSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    protected void postComment(String comment) {
        mEnableCommentButton.setValue(false);
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .postComment(
                        mToken,
                        mWorkflowListItem.getWorkflowId(),
                        comment,
                        isPrivateComment,
                        mCommentFiles)
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

    /**
     * Prepares a request to the endpoint for the desired file.
     *
     * @param commentFileResponse the file object to download.
     */
    protected void downloadAttachment(CommentFileResponse commentFileResponse) {
        showLoading.setValue(true);
        Disposable disposable = mRepository
                .downloadAttachment(mToken, CommentFileResponse.FILE_ENTITY, commentFileResponse.getId())
                .subscribe(this::onDownloadSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    /**
     * Callback for the success file download. Converts and saves it to a local file and sends it
     * back to the UI for displaying purposes.
     *
     * @param downloadFileResponse the downloaded file response.
     */
    private void onDownloadSuccess(DownloadFileResponse downloadFileResponse) {
        showLoading.setValue(false);

        // the API will return a base64 string representing the file

        String base64 = downloadFileResponse.getFile().getContent();
        if (base64 == null || base64.isEmpty()) {
            mToastMessageLiveData.setValue(R.string.error);
            return;
        }

        String fileName = downloadFileResponse.getFile().getFilename();
        try {
            AttachmentUiData attachmentUiData = new AttachmentUiData(
                    Utils.decodeFileFromBase64Binary(base64, fileName),
                    downloadFileResponse.getFile().getMime());
            mOpenDownloadedAttachmentLiveData.setValue(attachmentUiData);

        } catch (IOException e) {
            Log.e(TAG, "downloadFile: ", e);
            mToastMessageLiveData.setValue(R.string.error);
        }
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

    protected LiveData<Boolean> getObservableClearAttachments() {
        if (mClearAttachments == null) {
            mClearAttachments = new MutableLiveData<>();
        }
        return mClearAttachments;
    }

    protected LiveData<AttachmentUiData> getObservableOpenDownloadedAttachment() {
        if (mOpenDownloadedAttachmentLiveData == null) {
            mOpenDownloadedAttachmentLiveData = new MutableLiveData<>();
        }
        return mOpenDownloadedAttachmentLiveData;
    }
}
