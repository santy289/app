package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

public class PostLikeDislike {

    @Json(name = "interactionId")
    private Integer interactionId;
    @Json(name = "entity")
    private Integer entity;
    @Json(name = "entityType")
    private String entityType;
    @Json(name = "author")
    private Integer author;
    @Json(name = "thumb")
    private String thumb;

    public Integer getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Integer interactionId) {
        this.interactionId = interactionId;
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

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

}