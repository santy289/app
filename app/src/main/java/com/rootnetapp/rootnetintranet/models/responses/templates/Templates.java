package com.rootnetapp.rootnetintranet.models.responses.templates;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class Templates {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "label")
    private String label;
    @Json(name = "order")
    private int order;
    @Json(name = "isActive")
    private boolean isActive;
    @Json(name = "presets")
    private List<DocumentType> presets = null;
    @Json(name = "createdAt")
    private CreatedAt createdAt;
    @Json(name = "updatedAt")
    private UpdatedAt updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<DocumentType> getPresets() {
        return presets;
    }

    public void setPresets(List<DocumentType> presets) {
        this.presets = presets;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(UpdatedAt updatedAt) {
        this.updatedAt = updatedAt;
    }

}