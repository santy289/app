package com.rootnetapp.rootnetintranet.models.workflowlist;

import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;

public class WorkflowTypeMenu {
    private String label;
    private String subTitle;
    private int rowType;
    private int workflowTypeId;

    public WorkflowTypeMenu(String label, int rowType, int workflowTypeId) {
        this.label = label;
        this.rowType = rowType;
        this.workflowTypeId = workflowTypeId;
    }

    public WorkflowTypeMenu(String label, String subTitle, int rowType, int workflowTypeId) {
        this.label = label;
        this.rowType = rowType;
        this.subTitle = subTitle;
        this.workflowTypeId = workflowTypeId;
    }

    public WorkflowTypeMenu(String label, int rowType) {
        this(label, rowType, 0);
    }

    public WorkflowTypeMenu() {
        this.label = "";
        this.subTitle = "";
        this.rowType = RightDrawerFiltersAdapter.TYPE;
        this.workflowTypeId = 0;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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
