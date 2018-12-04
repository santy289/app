package com.rootnetapp.rootnetintranet.models.responses.workflows.presets;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class Preset {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "url")
    private String url;
    @Json(name = "order")
    private int order;
    @Json(name = "default_file_id")
    private String defaultFileId;
    @Json(name = "preset_file")
    private PresetFile presetFile;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "updatedAt")
    private String updatedAt;

    private boolean isSelected;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDefaultFileId() {
        return defaultFileId;
    }

    public void setDefaultFileId(String defaultFileId) {
        this.defaultFileId = defaultFileId;
    }

    public PresetFile getPresetFile() {
        return presetFile;
    }

    public void setPresetFile(PresetFile presetFile) {
        this.presetFile = presetFile;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}