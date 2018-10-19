package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

public class ApproverHistory {
    @Json(name = "id")
    public int id;

    @Json(name = "approver_id")
    public int approverId;

    @Json(name = "approver_name")
    public String approverName;

    @Json(name = "approved")
    public boolean approved;

    @Json(name = "changed_status")
    public boolean changedStatus;

    @Json(name = "created_at")
    public String createdAt;

    @Json(name = "status")
    public Status status;

    @Json(name = "next_status")
    public Status nextStatus;

    public String avatarPicture;
}
