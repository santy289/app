package com.rootnetapp.rootnetintranet.models.createworkflow.form;

public abstract class BaseFormItem {

    private String title;
    private int tag;
    private boolean isRequired;
    private boolean isEscaped;
    private @FormItemViewType int viewType;

    public abstract boolean isValid();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public boolean isEscaped() {
        return isEscaped;
    }

    public void setEscaped(boolean escaped) {
        isEscaped = escaped;
    }
}
