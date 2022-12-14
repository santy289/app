package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments;

import com.rootnetapp.rootnetintranet.models.requests.comment.CommentFile;
import com.rootnetapp.rootnetintranet.models.responses.comments.Comment;
import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;

public interface CommentsFragmentInterface {

    void removeAttachment(CommentFile commentFile);

    void downloadCommentAttachment(CommentFileResponse commentFileResponse);

    void editComment(Comment comment);

    void deleteComment(Comment comment);
}
