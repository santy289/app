package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.squareup.moshi.Json;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    public static void sortList(List<ApproverHistory> list) {
        Collections.sort(list, (o1, o2) -> {
            Date date1 = Utils.getDateFromString(o1.createdAt, Utils.SERVER_DATE_FORMAT);
            Date date2 = Utils.getDateFromString(o2.createdAt, Utils.SERVER_DATE_FORMAT);

            if (date1 == null) return -1;
            if (date2 == null) return 1;

            return date2.compareTo(date1);
        });
    }
}
