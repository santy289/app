package com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist;

public class WorkflowTypeItemMenu {
    public int id;
    public String name;
    public int category;

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
}
