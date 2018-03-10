package com.rootnetapp.rootnetintranet.models.responses.domain;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Module_ {

    @Json(name = "name")
    private String name;
    @Json(name = "description")
    private String description;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "enabled")
    private boolean enabled;
    @Json(name = "max_users")
    private int maxUsers;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

}