package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;

public interface CommentsFragmentInterface {

    void removeAttachment(CommentFile commentFile);

    void downloadCommentAttachment(CommentFileResponse commentFileResponse);
}
