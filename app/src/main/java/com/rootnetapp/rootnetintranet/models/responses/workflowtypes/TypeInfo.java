package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

/**
 * Created by root on 23/03/18.
 */

public class TypeInfo {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "type")
    private String type;
    @Json(name = "value_type")
    private String valueType;
    @Json(name = "global")
    private Boolean global;

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

}
