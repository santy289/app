
package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

public class Arguments {

    @Json(name = "current_status")
    private CurrentStatus currentStatus;
    @Json(name = "remaining_time")
    private Integer remainingTime;
    @Json(name = "workflow_type_id")
    private Integer workflowTypeId;
    @Json(name = "start")
    private String start;
    @Json(name = "description")
    private String description;
    @Json(name = "title")
    private String title;
    @Json(name = "key")
    private String key;
    @Json(name = "workflow_type")
    private WorkflowType workflowType;
    @Json(name = "name")
    private String name;
    @Json(name = "approver")
    private Approver approver;
    @Json(name = "next_status")
    private NextStatus nextStatus;
    @Json(name = "approved")
    private Boolean approved;
    @Json(name = "workflow_id")
    private Integer workflowId;

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(Integer workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Approver getApprover() {
        return approver;
    }

    public void setApprover(Approver approver) {
        this.approver = approver;
    }

    public NextStatus getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(NextStatus nextStatus) {
        this.nextStatus = nextStatus;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }
}
