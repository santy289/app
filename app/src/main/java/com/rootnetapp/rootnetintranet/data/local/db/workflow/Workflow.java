package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflows.CalculatedField;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowStateInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.squareup.moshi.Json;

import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Workflow {

//---------entity fields---------

    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    @Json(name = "title")
    private String title;

    @ColumnInfo(name = "workflow_type_key")
    @Json(name = "workflow_type_key")
    private String workflowTypeKey;

    @ColumnInfo(name = "description")
    @Json(name = "description")
    private String description;

    @ColumnInfo(name = "start")
    @Json(name = "start")
    private String start;

    @ColumnInfo(name = "end")
    @Json(name = "end")
    private String end;

    @ColumnInfo(name = "status")
    @Json(name = "status")
    private boolean status;

    @ColumnInfo(name = "open")
    @Json(name = "open")
    private boolean open;

    @ColumnInfo(name = "created_at")
    @Json(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "updated_at")
    @Json(name = "updated_at")
    private String updatedAt;

    /*@ColumnInfo(name = "workflow_state_id")
    private int workflowStateId;*/

//----------------------
//---------Relations----

    @Embedded
    @Json(name = "author")
    private Person author;

    @Embedded(prefix = "type_")
    @Json(name = "workflow_type")
    private WorkflowType workflowType;

    @Embedded(prefix = "state_")
    @Json(name = "workflow_state_info")
    private WorkflowStateInfo workflowStateInfo;

    @Ignore
    @Json(name = "metas")
    private List<Meta> metas = null;

//-----------------


    @ColumnInfo(name = "workflow_state")
    @Json(name = "workflow_state")
    private int workflowState;

    @Ignore
    @Json(name = "assignees")
    private List<Person> assignees = null;

    @Ignore
    @Json(name = "responsible")
    private List<Object> responsible = null;

    @Ignore
    @Json(name = "calculated_fields")
    private List<CalculatedField> calculatedFields = null;

    @Ignore
    @Json(name = "presets")
    private List<Preset> presets = null;

    @Ignore
    @Json(name = "current_status_relations")
    private List<Integer> currentStatusRelations;


    public List<Integer> getCurrentStatusRelations() {
        return currentStatusRelations;
    }

    public void setCurrentStatusRelations(List<Integer> currentStatusRelations) {
        this.currentStatusRelations = currentStatusRelations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkflowTypeKey() {
        return workflowTypeKey;
    }

    public void setWorkflowTypeKey(String workflowTypeKey) {
        this.workflowTypeKey = workflowTypeKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public int getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(int workflowState) {
        this.workflowState = workflowState;
    }

    public WorkflowStateInfo getWorkflowStateInfo() {
        return workflowStateInfo;
    }

    public void setWorkflowStateInfo(WorkflowStateInfo workflowStateInfo) {
        this.workflowStateInfo = workflowStateInfo;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public List<Person> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Person> assignees) {
        this.assignees = assignees;
    }

    public List<Object> getResponsible() {
        return responsible;
    }

    public void setResponsible(List<Object> responsible) {
        this.responsible = responsible;
    }

    public List<CalculatedField> getCalculatedFields() {
        return calculatedFields;
    }

    public void setCalculatedFields(List<CalculatedField> calculatedFields) {
        this.calculatedFields = calculatedFields;
    }

    public List<Meta> getMetas() {
        return metas;
    }

    public void setMetas(List<Meta> metas) {
        this.metas = metas;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
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

    /*public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getWorkflowStateId() {
        return workflowStateId;
    }

    public void setWorkflowStateId(int workflowStateId) {
        this.workflowStateId = workflowStateId;
    }

   public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }*/
}
