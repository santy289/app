package com.rootnetapp.rootnetintranet.models.responses.domain;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Module {

    @Json(name = "name")
    private String name;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "enabled")
    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}