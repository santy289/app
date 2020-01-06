package com.rootnetapp.rootnetintranet.ui.massapproval.models;

import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;

public class StatusApproval {
    private Status status;
    private Status selectedStatus;
    private boolean isRejected;

    public StatusApproval(Status status) {
        this.status = status;
    }

    public StatusApproval(Status status,
                          Status selectedStatus) {
        this.status = status;
        this.selectedStatus = selectedStatus;
    }

    public StatusApproval(Status status,
                          Status selectedStatus, boolean isRejected) {
        this.status = status;
        this.selectedStatus = selectedStatus;
        this.isRejected = isRejected;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(
            Status selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }
}
