package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 11/04/18.
 */

public class TimelineItem {

    @Json(name = "id")
    private int id;
    @Json(name = "sourceUser")
    private int sourceUser;
    @Json(name = "author")
    private int author;
    @Json(name = "action")
    private String action;
    @Json(name = "entity")
    private String entity;
    @Json(name = "entityId")
    private int entityId;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "comments")
    private List<Object> comments = null;
    @Json(name = "threadType")
    private String threadType;
    @Json(name = "description")
    private Description description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(int sourceUser) {
        this.sourceUser = sourceUser;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Object> getComments() {
        return comments;
    }

    public void setComments(List<Object> comments) {
        this.comments = comments;
    }

    public String getThreadType() {
        return threadType;
    }

    public void setThreadType(String threadType) {
        this.threadType = threadType;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

}