
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

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
