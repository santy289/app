package com.rootnetapp.rootnetintranet.data.local.db.test;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.rootnetapp.rootnetintranet.data.local.db.test2.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Person;
import com.rootnetapp.rootnetintranet.models.responses.workflows.CalculatedField;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowStateInfo;
import com.squareup.moshi.Json;

import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity = WorkflowTypeDb.class,
                                            parentColumns = "id",
                                            childColumns = "workflow_type_id",
                                            onDelete = ForeignKey.SET_NULL))
        //indices = {@Index("workflow_type_id")})
public class WorkflowDb {

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

    //----------------------
//---------Relations----

    // TODO change this Person instead to Profile (or maybe User but we already
    // have Profile which already have the data from User (user has minimum amount of data)
    @Embedded
    @Json(name = "author")
    private WorkflowUser author;

    // TODO ignoring this Type because we are going to save its id into WorflowTypeID
    @Ignore
    @Json(name = "workflow_type")
    private WorkflowTypeDb workflowType;

    @ColumnInfo(name = "workflow_type_id") //TODO save id from WorkflowType
    private Integer workflowTypeId;

    //TODO try to see how to save this Object.
    @Ignore
    @Json(name = "workflow_state_info")
    private WorkflowStateInfo workflowStateInfo;

    // TODO see how to save Meta and if it is important to save as seperate objects, or just as a json representation.
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

//    @Ignore
//    public WorkflowDb(int id, String title, String workflowTypeKey, String description, String start, String end, boolean status, boolean open, String createdAt, String updatedAt, WorkflowUser author, WorkflowType workflowType, WorkflowStateInfo workflowStateInfo, List<Meta> metas, int workflowState, List<Person> assignees, List<Object> responsible, List<CalculatedField> calculatedFields, List<Preset> presets) {
//        this.id = id;
//        this.title = title;
//        this.workflowTypeKey = workflowTypeKey;
//        this.description = description;
//        this.start = start;
//        this.end = end;
//        this.status = status;
//        this.open = open;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//        this.author = author;
//        this.workflowType = workflowType;
//        this.workflowStateInfo = workflowStateInfo;
//        this.metas = metas;
//        this.workflowState = workflowState;
//        this.assignees = assignees;
//        this.responsible = responsible;
//        this.calculatedFields = calculatedFields;
//        this.presets = presets;
//    }

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

    public WorkflowUser getAuthor() {
        return author;
    }

    public void setAuthor(WorkflowUser author) {
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

    public WorkflowTypeDb getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowTypeDb workflowType) {
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

    public Integer getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(Integer workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }
}
