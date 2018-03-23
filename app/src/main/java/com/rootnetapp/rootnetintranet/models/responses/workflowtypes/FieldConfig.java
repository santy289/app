package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 23/03/18.
 */

public class FieldConfig {

    @Json(name = "associated_workflow_field_id")
    private String associatedWorkflowFieldId;
    @Json(name = "is_associated")
    private Boolean isAssociated;
    @Json(name = "list_info")
    private ListInfo listInfo;
    @Json(name = "multiple")
    private Boolean multiple;
    @Json(name = "required")
    private Boolean required;
    @Json(name = "show_in")
    private List<Object> showIn = null;
    @Json(name = "type_info")
    private TypeInfo typeInfo;
    @Json(name = "right")
    private Boolean right;
    @Json(name = "show")
    private Boolean show;
    @Json(name = "formShow")
    private Boolean formShow;
    @Json(name = "filterShow")
    private Boolean filterShow;

    public String getAssociatedWorkflowFieldId() {
        return associatedWorkflowFieldId;
    }

    public void setAssociatedWorkflowFieldId(String associatedWorkflowFieldId) {
        this.associatedWorkflowFieldId = associatedWorkflowFieldId;
    }

    public Boolean getIsAssociated() {
        return isAssociated;
    }

    public void setIsAssociated(Boolean isAssociated) {
        this.isAssociated = isAssociated;
    }

    public ListInfo getListInfo() {
        return listInfo;
    }

    public void setListInfo(ListInfo listInfo) {
        this.listInfo = listInfo;
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<Object> getShowIn() {
        return showIn;
    }

    public void setShowIn(List<Object> showIn) {
        this.showIn = showIn;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public Boolean getRight() {
        return right;
    }

    public void setRight(Boolean right) {
        this.right = right;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public Boolean getFormShow() {
        return formShow;
    }

    public void setFormShow(Boolean formShow) {
        this.formShow = formShow;
    }

    public Boolean getFilterShow() {
        return filterShow;
    }

    public void setFilterShow(Boolean filterShow) {
        this.filterShow = filterShow;
    }


}
