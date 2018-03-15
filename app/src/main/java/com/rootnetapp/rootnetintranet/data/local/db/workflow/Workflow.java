package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import com.rootnetapp.rootnetintranet.models.responses.workflows.Assignee;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Author;
import com.rootnetapp.rootnetintranet.models.responses.workflows.CalculatedField;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowStateInfo;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowType;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Propietario on 15/03/2018.
 */

public class Workflow {

    @Json(name = "id")
    private int id;
    @Json(name = "title")
    private String title;
    @Json(name = "workflow_type_key")
    private String workflowTypeKey;
    @Json(name = "description")
    private String description;
    @Json(name = "start")
    private String start;
    @Json(name = "end")
    private String end;
    @Json(name = "status")
    private boolean status;
    @Json(name = "open")
    private boolean open;
    @Json(name = "author")
    private Author author;
    @Json(name = "workflow_state")
    private int workflowState;
    @Json(name = "workflow_state_info")
    private WorkflowStateInfo workflowStateInfo;
    @Json(name = "workflow_type")
    private WorkflowType workflowType;
    @Json(name = "assignees")
    private List<Assignee> assignees = null;
    @Json(name = "responsible")
    private List<Object> responsible = null;
    @Json(name = "calculated_fields")
    private List<CalculatedField> calculatedFields = null;
    @Json(name = "metas")
    private List<Object> metas = null;
    @Json(name = "presets")
    private List<Preset> presets = null;

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
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

    public List<Assignee> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Assignee> assignees) {
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

    public List<Object> getMetas() {
        return metas;
    }

    public void setMetas(List<Object> metas) {
        this.metas = metas;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }

}
