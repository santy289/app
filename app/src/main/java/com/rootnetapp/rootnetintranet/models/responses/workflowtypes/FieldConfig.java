package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

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
    @Json(name = "base")
    private Boolean base;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "entity_base_fields")
    private String entityBaseFields;
    @Json(name = "precalculated")
    private boolean precalculated;

    public String getEntityBaseFields() {
        return entityBaseFields;
    }

    public void setEntityBaseFields(String entityBaseFields) {
        this.entityBaseFields = entityBaseFields;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Boolean getBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

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

    public boolean isPrecalculated() {
        return precalculated;
    }

    public void setPrecalculated(boolean precalculated) {
        this.precalculated = precalculated;
    }
}
