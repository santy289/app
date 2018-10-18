package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.squareup.moshi.Json;

import java.util.List;

@Entity(indices = {@Index("id")})
public class WorkflowTypeDb {
    @PrimaryKey
    @Json(name = "id")
    private int id;

    @Json(name = "name")
    private String name;

    @Json(name = "key")
    private String key;

    @Json(name = "initial")
    private int initial;

    @ColumnInfo(name = "workflow_count")
    @Json(name = "workflow_count")
    private int workflowCount;

    @Json(name = "active")
    private boolean active;

    @ColumnInfo(name = "template_id")
    @Json(name = "template_id")
    private int templateId;

    @ColumnInfo(name = "category")
    @Json(name = "category")
    private int category;

    @Ignore
    @Json(name = "status")
    private List<Status> status = null;

    @Ignore
    @Json(name = "fields")
    private List<Field> fields = null;

    @Ignore
    @Json(name = "presets")
    private List<Preset> presets = null;

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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getInitial() {
        return initial;
    }

    public void setInitial(int initial) {
        this.initial = initial;
    }

    public int getWorkflowCount() {
        return workflowCount;
    }

    public void setWorkflowCount(int workflowCount) {
        this.workflowCount = workflowCount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }
}
