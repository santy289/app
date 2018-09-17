package com.rootnetapp.rootnetintranet.ui.workflowlist;

public class FilterBoxSettings {
    private boolean isCheckedMyPending;
    private boolean isCheckedStatus;
    private int typeIdPositionInArray;
    private int workflowTypeId;

    public FilterBoxSettings() {
        this.isCheckedMyPending = false;
        this.isCheckedStatus = true;
        this.typeIdPositionInArray = 0;
        this.workflowTypeId = 0;
    }

    public FilterBoxSettings(boolean isCheckedMyPending, boolean isCheckedStatus, int typeIdPositionInArray, int workflowTypeId) {
        this.isCheckedMyPending = isCheckedMyPending;
        this.isCheckedStatus = isCheckedStatus;
        this.typeIdPositionInArray = typeIdPositionInArray;
        this.workflowTypeId = workflowTypeId;
    }

    public boolean isCheckedMyPending() {
        return isCheckedMyPending;
    }

    public void setCheckedMyPending(boolean checkedMyPending) {
        isCheckedMyPending = checkedMyPending;
    }

    public boolean isCheckedStatus() {
        return isCheckedStatus;
    }

    public void setCheckedStatus(boolean checkedStatus) {
        isCheckedStatus = checkedStatus;
    }

    public int getTypeIdPositionInArray() {
        return typeIdPositionInArray;
    }

    public void setTypeIdPositionInArray(int typeIdPositionInArray) {
        this.typeIdPositionInArray = typeIdPositionInArray;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }
}
