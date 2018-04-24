package com.rootnetapp.rootnetintranet.models.responses.workflows;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Propietario on 15/03/2018.
 */

public class WorkflowStateInfo {
    @Json(name = "_new")
    private boolean _new;
    @Json(name = "_deleted")
    private boolean deleted;

    @Ignore
    @Json(name = "modified_columns")
    private List<Object> modifiedColumns = null;

    @Embedded(prefix = "col_")
    @Json(name = "virtual_columns")
    private VirtualColumns virtualColumns;
    @Json(name = "start_copy")
    private boolean startCopy;
    @Json(name = "id")
    private int id;
    @Json(name = "workflow_type_id")
    private int workflowTypeId;
    @Json(name = "name")
    private String name;
    @Json(name = "configuration")
    private String configuration;
    @Json(name = "machine_name")
    private String machineName;
    @Json(name = "base")
    private boolean base;
    @Json(name = "order")
    private int order;
    @Json(name = "is_active")
    private boolean isActive;
    @Json(name = "is_require")
    private boolean isRequire;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "already_in_save")
    private boolean alreadyInSave;
    @Json(name = "already_in_validation")
    private boolean alreadyInValidation;
    @Json(name = "already_in_clear_all_references_deep")
    private boolean alreadyInClearAllReferencesDeep;

    @Ignore
    @Json(name = "sortable_queries")
    private List<Object> sortableQueries = null;
    @Ignore
    @Json(name = "validation_failures")
    private List<Object> validationFailures = null;

    public boolean isNew() {
        return _new;
    }

    public void setNew(boolean _new) {
        this._new = _new;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Object> getModifiedColumns() {
        return modifiedColumns;
    }

    public void setModifiedColumns(List<Object> modifiedColumns) {
        this.modifiedColumns = modifiedColumns;
    }

    public VirtualColumns getVirtualColumns() {
        return virtualColumns;
    }

    public void setVirtualColumns(VirtualColumns virtualColumns) {
        this.virtualColumns = virtualColumns;
    }

    public boolean isStartCopy() {
        return startCopy;
    }

    public void setStartCopy(boolean startCopy) {
        this.startCopy = startCopy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsRequire() {
        return isRequire;
    }

    public void setIsRequire(boolean isRequire) {
        this.isRequire = isRequire;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAlreadyInSave() {
        return alreadyInSave;
    }

    public void setAlreadyInSave(boolean alreadyInSave) {
        this.alreadyInSave = alreadyInSave;
    }

    public boolean isAlreadyInValidation() {
        return alreadyInValidation;
    }

    public void setAlreadyInValidation(boolean alreadyInValidation) {
        this.alreadyInValidation = alreadyInValidation;
    }

    public boolean isAlreadyInClearAllReferencesDeep() {
        return alreadyInClearAllReferencesDeep;
    }

    public void setAlreadyInClearAllReferencesDeep(boolean alreadyInClearAllReferencesDeep) {
        this.alreadyInClearAllReferencesDeep = alreadyInClearAllReferencesDeep;
    }

    public List<Object> getSortableQueries() {
        return sortableQueries;
    }

    public void setSortableQueries(List<Object> sortableQueries) {
        this.sortableQueries = sortableQueries;
    }

    public List<Object> getValidationFailures() {
        return validationFailures;
    }

    public void setValidationFailures(List<Object> validationFailures) {
        this.validationFailures = validationFailures;
    }

}
