package com.rootnetapp.rootnetintranet.models.workflowlist;

import com.rootnetapp.rootnetintranet.models.createworkflow.ListFieldItemMeta;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;

import androidx.annotation.StringRes;

public class WorkflowTypeMenu extends ListFieldItemMeta {
    private int id;
    private String label;
    private int resLabel;
    private String subTitle;
    private int rowType;
    private int workflowTypeId;
    private boolean selected;
    private Integer workflowCount;

    public WorkflowTypeMenu(int id, String label, int rowType, int workflowTypeId) {
        this.id = id;
        this.label = label;
        this.rowType = rowType;
        this.workflowTypeId = workflowTypeId;
        this.selected = false;
    }

    public WorkflowTypeMenu(int id, @StringRes int resString, int rowType, int workflowTypeId) {
        this.id = id;
        this.resLabel = resString;
        this.rowType = rowType;
        this.workflowTypeId = workflowTypeId;
        this.selected = false;
    }

    public WorkflowTypeMenu(int id, String label, String subTitle, int rowType, int workflowTypeId) {
        this.id = id;
        this.label = label;
        this.rowType = rowType;
        this.subTitle = subTitle;
        this.workflowTypeId = workflowTypeId;
        this.selected = false;
    }

    public WorkflowTypeMenu(int id, String label, int rowType) {
        this(id, label, rowType, 0);
    }

    public WorkflowTypeMenu() {
        this.id = 0;
        this.label = "";
        this.subTitle = "";
        this.rowType = RightDrawerFiltersAdapter.TYPE;
        this.workflowTypeId = 0;
        this.selected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResLabel() {
        return resLabel;
    }

    public void setResLabel(int resLabel) {
        this.resLabel = resLabel;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getWorkflowCount() {
        return workflowCount;
    }

    public void setWorkflowCount(Integer workflowCount) {
        this.workflowCount = workflowCount;
    }
}
