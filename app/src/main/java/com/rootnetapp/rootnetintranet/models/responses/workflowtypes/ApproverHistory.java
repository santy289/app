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

    /**
     * Applies a descending sorting algorithm by {@link ApproverHistory#createdAt}. Sorts the list
     * from the newest to the oldest approval.
     *
     * @param list original list.
     */
    public static void sortList(List<ApproverHistory> list) {
        Collections.sort(list, (o1, o2) -> {
            Date date1 = Utils.getDateFromString(o1.createdAt, Utils.SERVER_DATE_FORMAT);
            Date date2 = Utils.getDateFromString(o2.createdAt, Utils.SERVER_DATE_FORMAT);

            if (date1 == null) return -1;
            if (date2 == null) return 1;

            return date2.compareTo(date1);
        });
    }

    /**
     * Checks the current approval state for a specific status by a specific approver.
     *
     * @param list       sorted list, use {@link #sortList(List)} first.
     * @param statusId   specific status ID to verify.
     * @param approverId specific approver ID to verify.
     *
     * @return the first match for the status and approver specified, true for approved, false for
     * rejected.
     */
    public static Boolean getApprovalStateForStatusAndApprover(List<ApproverHistory> list,
                                                               int statusId, int approverId) {
        for (ApproverHistory obj : list) {
            if (obj.status.getId() == statusId && obj.approverId == approverId) {
                return obj.approved;
            }
        }

        return null;
    }
}
