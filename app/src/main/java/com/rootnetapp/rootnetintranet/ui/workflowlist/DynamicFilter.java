package com.rootnetapp.rootnetintranet.ui.workflowlist;

import androidx.annotation.NonNull;

public class DynamicFilter {
    private String key;
    private Object value;

    public DynamicFilter(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ \"" + key + "\": " + value + " }";
    }
}
