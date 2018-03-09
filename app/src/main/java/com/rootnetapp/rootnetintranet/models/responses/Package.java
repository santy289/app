package com.rootnetapp.rootnetintranet.models.responses;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Propietario on 09/03/2018.
 */

public class Package {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "modules")
    private List<Module> modules = null;

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

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

}
