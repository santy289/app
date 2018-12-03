package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

public class NextStatusRequirements {

    @Json(name = "status_id")
    private int statusId;
    @Json(name = "approved_count")
    private int approvedCount;
    @Json(name = "rejected_count")
    private int rejectedCount;
    @Json(name = "approve_tie")
    private boolean approveTie;

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(int approvedCount) {
        this.approvedCount = approvedCount;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(int rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public boolean isApproveTie() {
        return approveTie;
    }

    public void setApproveTie(boolean approveTie) {
        this.approveTie = approveTie;
    }

}