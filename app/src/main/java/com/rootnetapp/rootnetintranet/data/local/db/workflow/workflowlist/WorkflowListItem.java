package com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WorkflowListItem {
    public int workflowId;
    public int workflowTypeId;
    public long remainingTime;
    public String workflowTypeName;
    public String title;
    @ColumnInfo(name = "workflow_type_key")
    public String workflowTypeKey;
    @ColumnInfo(name = "full_name")
    public String fullName;
    @ColumnInfo(name = "current_status_name")
    public String currentStatusName;
    @ColumnInfo(name = "created_at")
    public String createdAt;
    @ColumnInfo(name = "updated_at")
    public String updatedAt;
    @ColumnInfo(name = "start")
    public String start;
    @ColumnInfo(name = "end")
    public String end;
    @ColumnInfo(name = "status")
    public boolean status;
    @ColumnInfo(name = "current_status")
    public int currentStatus;
    @Ignore
    public boolean selected = false;
    @Ignore
    boolean isChecked = false;
    @Ignore
    private String formattedCreatedAt;
    @Ignore
    private String formattedUpdatedAt;

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkflowTypeKey() {
        return workflowTypeKey;
    }

    public void setWorkflowTypeKey(String workflowTypeKey) {
        this.workflowTypeKey = workflowTypeKey;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCurrentStatusName() {
        return currentStatusName;
    }

    public void setCurrentStatusName(String currentStatusName) {
        this.currentStatusName = currentStatusName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtFormatted() {
        if (TextUtils.isEmpty(formattedCreatedAt)) {
            SimpleDateFormat finalFormat = new SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
            );

            Date convertedDate = createDateObj(createdAt);
            if (convertedDate == null) {
                formattedCreatedAt = createdAt;
                return formattedCreatedAt;
            }
            formattedCreatedAt = finalFormat.format(convertedDate);
        }
        return formattedCreatedAt;
    }

    public String getUpdatedAtFormatted() {
        if (TextUtils.isEmpty(formattedUpdatedAt)) {
            SimpleDateFormat finalFormat = new SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
            );
            Date convertedDate = createDateObj(updatedAt);
            if (convertedDate == null) {
                formattedUpdatedAt = updatedAt;
                return formattedUpdatedAt;
            }
            formattedUpdatedAt = finalFormat.format(convertedDate);
        }
        return formattedUpdatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public String getWorkflowTypeName() {
        return workflowTypeName;
    }

    public void setWorkflowTypeName(String workflowTypeName) {
        this.workflowTypeName = workflowTypeName;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private Date createDateObj(String createUpdateDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssZ",
                Locale.getDefault());
        try {
            return dateFormat.parse(createUpdateDate);
        } catch (ParseException e) {
            Log.d("WorkflowItem", "createDateObj: e = " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkflowListItem that = (WorkflowListItem) o;
        return getWorkflowId() == that.getWorkflowId() &&
                getWorkflowTypeId() == that.getWorkflowTypeId() &&
                isStatus() == that.isStatus() &&
                Objects.equals(getWorkflowTypeName(), that.getWorkflowTypeName()) &&
                Objects.equals(getTitle(), that.getTitle()) &&
                Objects.equals(getWorkflowTypeKey(), that.getWorkflowTypeKey()) &&
                Objects.equals(getFullName(), that.getFullName()) &&
                Objects.equals(getCurrentStatusName(), that.getCurrentStatusName()) &&
                Objects.equals(getCreatedAt(), that.getCreatedAt()) &&
                Objects.equals(getUpdatedAt(), that.getUpdatedAt()) &&
                Objects.equals(getStart(), that.getStart()) &&
                Objects.equals(getEnd(), that.getEnd());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getWorkflowId(), getWorkflowTypeId(), getWorkflowTypeName(), getTitle(), getWorkflowTypeKey(), getFullName(), getCurrentStatusName(), getCreatedAt(), getUpdatedAt(), getStart(), getEnd(), isStatus());
    }
}
