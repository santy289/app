package com.rootnetapp.rootnetintranet.models.requests.approval;

import com.squareup.moshi.Json;

public class ApprovalRequest {

    @Json(name = "approved")
    private boolean approved;
    @Json(name = "next_status")
    private int nextStatus;

    /**
     * No args constructor for use in serialization
     *
     */
    public ApprovalRequest() {
    }

    public ApprovalRequest(boolean approved, int nextStatus) {
        super();
        this.approved = approved;
        this.nextStatus = nextStatus;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(int nextStatus) {
        this.nextStatus = nextStatus;
    }

}