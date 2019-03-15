package com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;

import androidx.room.ColumnInfo;

public class WorkflowTypeItemMenu {

    public int id;
    public String name;
    public int category;

    @ColumnInfo(name = "workflow_count")
    private Integer workflowCount;

    @ColumnInfo(name = "original_id")
    private int originalId;

    public WorkflowTypeItemMenu() {}

    /**
     * Mapping constructor to convert a {@link WorkflowTypeDb} into a {@link WorkflowTypeItemMenu}.
     *
     * @param workflowTypeDb object to map.
     */
    public WorkflowTypeItemMenu(WorkflowTypeDb workflowTypeDb) {
        this.id = workflowTypeDb.getId();
        this.name = workflowTypeDb.getName();
        this.category = workflowTypeDb.getCategory() == null ? 0 : workflowTypeDb.getCategory();
        this.workflowCount = workflowTypeDb.getWorkflowCount();
        this.originalId = workflowTypeDb.getOriginalId();
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWorkflowCount() {
        return workflowCount;
    }

    public void setWorkflowCount(Integer workflowCount) {
        this.workflowCount = workflowCount;
    }

    public int getOriginalId() {
        return originalId;
    }

    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }
}
