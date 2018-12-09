package com.rootnetapp.rootnetintranet.models.requests.comment;

import com.squareup.moshi.Json;

import java.util.List;

public class PostCommentRequest {

    @Json(name = "is_private")
    private boolean isPrivate;
    @Json(name = "description")
    private String description;
    @Json(name = "files")
    private List<CommentFile> commentFiles = null;

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CommentFile> getCommentFiles() {
        return commentFiles;
    }

    public void setCommentFiles(
            List<CommentFile> commentFiles) {
        this.commentFiles = commentFiles;
    }
}