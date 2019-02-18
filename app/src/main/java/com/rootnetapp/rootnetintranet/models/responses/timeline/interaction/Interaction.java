
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

import java.util.List;

public class Interaction {

    @Json(name = "id")
    private Integer id;
    @Json(name = "entity")
    private Integer entity;
    @Json(name = "entityType")
    private String entityType;
    @Json(name = "entityTypeId")
    private Integer entityTypeId;
    @Json(name = "authorCreated")
    private Integer authorCreated;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "commentsCount")
    private Integer commentsCount;
    @Json(name = "thumbsCount")
    private Integer thumbsCount;
    @Json(name = "thumbUp")
    private Integer thumbsUp;
    @Json(name = "thumbDown")
    private Integer thumbsDown;
    @Json(name = "commentsPager")
    private CommentsPager commentsPager;
    @Json(name = "comments")
    private List<Comment> comments = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntity() {
        return entity;
    }

    public void setEntity(Integer entity) {
        this.entity = entity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityTypeId() {
        return entityTypeId;
    }

    public void setEntityTypeId(Integer entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public Integer getAuthorCreated() {
        return authorCreated;
    }

    public void setAuthorCreated(Integer authorCreated) {
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

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getThumbsCount() {
        return thumbsCount;
    }

    public void setThumbsCount(Integer thumbsCount) {
        this.thumbsCount = thumbsCount;
    }

    public Integer getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(Integer thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public Integer getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(Integer thumbsDown) {
        this.thumbsDown = thumbsDown;
    }

    public CommentsPager getCommentsPager() {
        return commentsPager;
    }

    public void setCommentsPager(CommentsPager commentsPager) {
        this.commentsPager = commentsPager;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interaction that = (Interaction) o;

        if (!id.equals(that.id)) return false;
        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) {
            return false;
        }
        if (entityTypeId != null ? !entityTypeId
                .equals(that.entityTypeId) : that.entityTypeId != null) {
            return false;
        }
        if (authorCreated != null ? !authorCreated
                .equals(that.authorCreated) : that.authorCreated != null) {
            return false;
        }
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityTypeId != null ? entityTypeId.hashCode() : 0);
        result = 31 * result + (authorCreated != null ? authorCreated.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }
}
