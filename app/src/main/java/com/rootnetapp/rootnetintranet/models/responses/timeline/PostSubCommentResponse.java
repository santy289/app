package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

/**
 * Created by root on 12/04/18.
 */

public class PostSubCommentResponse {

    @Json(name = "comment")
    private Comment comment;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
