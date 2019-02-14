
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

import java.util.List;

public class Comment {

    @Json(name = "id")
    private Integer id;
    @Json(name = "interactionId")
    private Integer interactionId;
    @Json(name = "associate")
    private Integer associate;
    @Json(name = "description")
    private String description;
    @Json(name = "author")
    private Integer author;
    @Json(name = "level")
    private Integer level;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "count")
    private Integer count;
    @Json(name = "thumbUp")
    private Integer thumbUp;
    @Json(name = "thumbDown")
    private Integer thumbDown;
    @Json(name = "comments")
    private List<Object> comments = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Integer interactionId) {
        this.interactionId = interactionId;
    }

    public Integer getAssociate() {
        return associate;
    }

    public void setAssociate(Integer associate) {
        this.associate = associate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(Integer thumbUp) {
        this.thumbUp = thumbUp;
    }

    public Integer getThumbDown() {
        return thumbDown;
    }

    public void setThumbDown(Integer thumbDown) {
        this.thumbDown = thumbDown;
    }

    public List<Object> getComments() {
        return comments;
    }

    public void setComments(java.util.List<Object> comments) {
        this.comments = comments;
    }

}
