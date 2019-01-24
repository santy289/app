package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(name = "version")
    @Json(name = "version")
    private int version;

    @ColumnInfo(name = "define_roles")
    @Json(name = "define_roles")
    private boolean defineRoles;

    @Ignore
    @Json(name = "status")
    private List<Status> status = null;

    @Ignore
    @Json(name = "fields")
    private List<Field> fields = null;

    @Ignore
    @Json(name = "presets")
    private List<Preset> presets = null;

    @Ignore
    @Json(name = "role_approvers")
    private List<Integer> roleApprovers = null;

    @Ignore
    @Json(name = "default_role_approvers")
    private List<DefaultRoleApprover> defaultRoleApprovers = null;

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

    public boolean isDefineRoles() {
        return defineRoles;
    }

    public void setDefineRoles(boolean defineRoles) {
        this.defineRoles = defineRoles;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Integer> getRoleApprovers() {
        return roleApprovers;
    }

    public void setRoleApprovers(List<Integer> roleApprovers) {
        this.roleApprovers = roleApprovers;
    }

    public List<DefaultRoleApprover> getDefaultRoleApprovers() {
        return defaultRoleApprovers;
    }

    public void setDefaultRoleApprovers(List<DefaultRoleApprover> defaultRoleApprovers) {
        this.defaultRoleApprovers = defaultRoleApprovers;
    }

    public List<Approver> getDistinctApprovers() {
        List<Approver> list = new ArrayList<>();

        for (Status status : getStatus()) {
            for (Approver approver : status.getApproversList()) {
                if (!list.contains(approver)) list.add(approver);
            }
        }

        return list;
    }

    public List<Integer> getRoleApproverProfileIds(int roleId){
        List<Integer> profileIds = new ArrayList<>();

        for (DefaultRoleApprover roleApprover : getDefaultRoleApprovers()) {
            if (roleApprover.getRoleId() == roleId) profileIds.add(roleApprover.getProfileId());
        }

        return profileIds;
    }
}
