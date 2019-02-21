package com.rootnetapp.rootnetintranet.models.requests.comment;

import com.squareup.moshi.Json;

public class EditCommentRequest {

    @Json(name = "commentId")
    private Integer commentId;
    @Json(name = "description")
    private String description;

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}