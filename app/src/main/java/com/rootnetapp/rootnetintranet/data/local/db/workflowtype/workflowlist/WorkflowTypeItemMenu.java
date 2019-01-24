package com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist;

import androidx.room.ColumnInfo;

public class WorkflowTypeItemMenu {
    public int id;
    public String name;
    public int category;

    @ColumnInfo(name = "workflow_count")
    private Integer workflowCount;

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
}
