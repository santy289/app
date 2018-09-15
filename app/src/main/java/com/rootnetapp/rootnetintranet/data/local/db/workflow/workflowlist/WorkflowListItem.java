package com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
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
    @Ignore
    public boolean selected = false;
    @Ignore
    public String formattedCreatedAt;
    @Ignore
    public String formattedUpdatedAt;

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
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
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssZ",
                    Locale.getDefault());
            try {
                Date convertedDate = dateFormat.parse(createdAt);

                SimpleDateFormat finalFormat = new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                );
                formattedCreatedAt = finalFormat.format(convertedDate);
            } catch (ParseException e) {
                Log.d("WorkflowItem", "StringDateToTimestamp: e = " + e.getMessage());
                formattedCreatedAt = createdAt;
            }
        }
        return formattedCreatedAt;
    }

    public String getUpdatedAtFormatted() {
        if (TextUtils.isEmpty(formattedUpdatedAt)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssZ",
                    Locale.getDefault());
            try {
                Date convertedDate = dateFormat.parse(updatedAt);

                SimpleDateFormat finalFormat = new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                );
                formattedUpdatedAt = finalFormat.format(convertedDate);
            } catch (ParseException e) {
                Log.d("WorkflowItem", "StringDateToTimestamp: e = " + e.getMessage());
                formattedUpdatedAt = updatedAt;
            }
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
