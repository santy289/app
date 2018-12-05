package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.TypeInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public abstract class BaseFormItem {

    private @Nullable String title; //this has priority, if null, must set a titleRes
    private @StringRes int titleRes;
    private int tag;
    private boolean isRequired;
    private boolean isEscaped;
    private boolean isEnabled = true;
    private boolean isVisible = true;
    private @FormItemViewType int viewType;
    private TypeInfo typeInfo; //server type
    private String machineName;

    public abstract boolean isValid();
    public abstract String getStringValue();

    public @Nullable
    String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isEscaped() {
        return isEscaped;
    }

    public void setEscaped(boolean escaped) {
        isEscaped = escaped;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
