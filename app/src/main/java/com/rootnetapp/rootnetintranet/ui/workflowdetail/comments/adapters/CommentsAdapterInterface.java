package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import com.rootnetapp.rootnetintranet.models.responses.comments.CommentFileResponse;

public interface CommentsAdapterInterface {

    void downloadAttachment(CommentFileResponse commentFileResponse);
}
