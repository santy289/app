package com.rootnetapp.rootnetintranet.models.responses;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Product {

    @Json(name = "name")
    private String name;
    @Json(name = "description")
    private String description;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "max_users")
    private int maxUsers;
    @Json(name = "modules")
    private List<Module_> modules = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public List<Module_> getModules() {
        return modules;
    }

    public void setModules(List<Module_> modules) {
        this.modules = modules;
    }

}