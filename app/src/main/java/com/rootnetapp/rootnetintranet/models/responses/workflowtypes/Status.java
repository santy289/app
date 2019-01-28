package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class Status {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "order")
    private int order;
    @Json(name = "base")
    private boolean base;
    @Json(name = "configuration")
    private boolean configuration;
    @Json(name = "isActive")
    private boolean isActive;
    @Json(name = "isRequired")
    private boolean isRequired;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "parentId")
    private Integer parentId;
    @Json(name = "steps")
    private List<Step> steps = null;
    @Json(name = "approvers")
    private List<Approver> approversList = new ArrayList<>();
    @Json(name = "relations")
    private List<Integer> relations;

    public int getId() {
        return id;
    }

    public List<Integer> getRelations() {
        return relations;
    }

    public void setRelations(List<Integer> relations) {
        this.relations = relations;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public boolean isConfiguration() {
        return configuration;
    }

    public void setConfiguration(boolean configuration) {
        this.configuration = configuration;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Approver> getApproversList() {
        return approversList;
    }

    public void setApproversList(List<Approver> approversList) {
        this.approversList = approversList;
    }

    public static Status getStatusByIdFromList(List<Status> statuses, int statusId) {
        for (Status status : statuses) {
            if (status.getId() == statusId) return status;
        }

        return null;
    }
}
