package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class CalculatedField {

    @Json(name = "id")
    private int id;
    @Json(name = "value")
    private String value;
    @Json(name = "with_color")
    private boolean withColor;
    @Json(name = "field_name")
    private String fieldName;
    @Json(name = "machine_name")
    private String machineName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isWithColor() {
        return withColor;
    }

    public void setWithColor(boolean withColor) {
        this.withColor = withColor;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

}