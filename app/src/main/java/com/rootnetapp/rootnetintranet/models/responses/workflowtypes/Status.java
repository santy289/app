package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 23/03/18.
 */

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
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "parentId")
    private int parentId;
    @Json(name = "steps")
    private List<Step> steps = null;

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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
