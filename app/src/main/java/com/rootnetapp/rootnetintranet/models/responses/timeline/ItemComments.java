package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.rootnetapp.rootnetintranet.models.responses.workflows.Pager;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 12/04/18.
 */

public class ItemComments {

    @Json(name = "id")
    private int id;
    @Json(name = "entity")
    private int entity;
    @Json(name = "entityType")
    private String entityType;
    @Json(name = "entityTypeId")
    private int entityTypeId;
    @Json(name = "authorCreated")
    private int authorCreated;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "commentsCount")
    private int commentsCount;
    @Json(name = "thumbsCount")
    private int thumbsCount;
    @Json(name = "commentsPager")
    private Pager commentsPager;
    @Json(name = "comments")
    private List<Comment> comments = null;
    @Json(name = "thumbUp")
    private int thumbUp;
    @Json(name = "thumbDown")
    private int thumbDown;
    @Json(name = "authorUpdated")
    private int authorUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntity() {
        return entity;
    }

    public void setEntity(int entity) {
        this.entity = entity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getEntityTypeId() {
        return entityTypeId;
    }

    public void setEntityTypeId(int entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public int getAuthorCreated() {
        return authorCreated;
    }

    public void setAuthorCreated(int authorCreated) {
        this.authorCreated = authorCreated;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getThumbsCount() {
        return thumbsCount;
    }

    public void setThumbsCount(int thumbsCount) {
        this.thumbsCount = thumbsCount;
    }

    public Pager getCommentsPager() {
        return commentsPager;
    }

    public void setCommentsPager(Pager commentsPager) {
        this.commentsPager = commentsPager;
    }

    public java.util.List<Comment> getComments() {
        return comments;
    }

    public void setComments(java.util.List<Comment> comments) {
        this.comments = comments;
    }

    public int getThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(int thumbUp) {
        this.thumbUp = thumbUp;
    }

    public int getThumbDown() {
        return thumbDown;
    }

    public void setThumbDown(int thumbDown) {
        this.thumbDown = thumbDown;
    }

    public int getAuthorUpdated() {
        return authorUpdated;
    }

    public void setAuthorUpdated(int authorUpdated) {
        this.authorUpdated = authorUpdated;
    }

}