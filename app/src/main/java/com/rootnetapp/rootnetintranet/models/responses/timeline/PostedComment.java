package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 12/04/18.
 */

public class PostedComment {

    @Json(name = "id")
    private int id;
    @Json(name = "interactionId")
    private int interactionId;
    @Json(name = "description")
    private String description;
    @Json(name = "author")
    private int author;
    @Json(name = "level")
    private int level;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;
    @Json(name = "count")
    private int count;
    @Json(name = "comments")
    private List<PostedComment> comments = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(int interactionId) {
        this.interactionId = interactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PostedComment> getComments() {
        return comments;
    }

    public void setComments(List<PostedComment> comments) {
        this.comments = comments;
    }

}
