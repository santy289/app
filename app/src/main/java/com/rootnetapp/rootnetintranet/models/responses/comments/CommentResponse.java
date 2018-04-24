package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.squareup.moshi.Json;

/**
 * Created by root on 05/04/18.
 */

public class CommentResponse {

    @Json(name = "comment_info")
    private Comment commentInfo = null;

    public Comment getResponse() {
        return commentInfo;
    }

    public void setResponse(Comment commentInfo) {
        this.commentInfo = commentInfo;
    }

}
