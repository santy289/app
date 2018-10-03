package com.rootnetapp.rootnetintranet.models.workflowlist;

public class SpinnerWorkflowTypeMenu {
    private String label;
    private int rowType;
    private int workflowTypeId;

    public SpinnerWorkflowTypeMenu(String label, int rowType, int workflowTypeId) {
        this.label = label;
        this.rowType = rowType;
        this.workflowTypeId = workflowTypeId;
    }

    public SpinnerWorkflowTypeMenu(String label, int rowType) {
        this(label, rowType, 0);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }
}
