package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.converters.StringDateConverter;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.models.createworkflow.SpecificApprovers;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Meta;
import com.rootnetapp.rootnetintranet.models.responses.workflows.NextStatusRequirements;
import com.rootnetapp.rootnetintranet.models.responses.workflows.PersonRelated;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.squareup.moshi.Json;

import java.text.Normalizer;
import java.util.List;

@Entity(foreignKeys = @ForeignKey(entity = WorkflowTypeDb.class,
                                            parentColumns = "id",
                                            childColumns = "workflow_type_id",
                                            onDelete = ForeignKey.CASCADE),
        indices = {@Index("workflow_type_id")})
public class WorkflowDb {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @Json(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    @Json(name = "title")
    private String title;

    @ColumnInfo(name = "title_normalized")
    private String titleNormalized;

    @ColumnInfo(name = "workflow_type_key")
    @Json(name = "workflow_type_key")
    private String workflowTypeKey;

    @ColumnInfo(name = "description")
    @Json(name = "description")
    private String description;

    @ColumnInfo(name = "description_normalized")
    private String descriptionNormalized;

    @ColumnInfo(name = "start")
    @Json(name = "start")
    private String start;

    @ColumnInfo(name = "end")
    @Json(name = "end")
    private String end;

    @ColumnInfo(name = "remaining_time")
    @Json(name = "remaining_time")
    private long remainingTime;

    @ColumnInfo(name = "status")
    @Json(name = "status")
    private boolean status;

    @ColumnInfo(name = "current_status")
    @Json(name = "current_status")
    private int currentStatus;

    @ColumnInfo(name = "current_status_name")
    @Json(name = "current_status_name")
    private String currentStatusName;

    @ColumnInfo(name = "open")
    @Json(name = "open")
    private boolean open;

    @TypeConverters(StringDateConverter.class)
    @ColumnInfo(name = "created_at")
    @Json(name = "created_at")
    private String createdAt;

    @TypeConverters(StringDateConverter.class)
    @ColumnInfo(name = "updated_at")
    @Json(name = "updated_at")
    private String updatedAt;

    //----------------------
//---------Relations----

    // TODO change this Person instead to Profile (or maybe User but we already
    // have Profile which already have the data from User (user has minimum amount of data)
    @Embedded
    @Json(name = "author")
    private WorkflowUser author;


    @ColumnInfo(name = "workflow_type_id")
    @Json(name = "workflow_type_id")
    private int workflowTypeId;

    @ColumnInfo(name = "workflow_type_original_id")
    @Json(name = "workflow_type_original_id")
    private int workflowTypeOriginalId;

//-----------------

    @Ignore
    @Json(name = "workflow_type")
    private WorkflowTypeDb workflowType;

    @Ignore
    @Json(name = "assignees")
    private List<Person> assignees = null;

    @Ignore
    @Json(name = "responsible")
    private List<Object> responsible = null;

    @Ignore
    @Json(name = "presets")
    private List<Preset> presets = null;

    @Ignore
    @Json(name = "current_status_relations")
    private List<Integer> currentStatusRelations;

    @Ignore
    @Json(name = "metas")
    private List<Meta> metas = null;

    @Ignore
    @Json(name = "profilesInvolved")
    private List<Integer> profilesInvolved;

    @Ignore
    @Json(name = "specific_approvers")
    private SpecificApprovers specificApprovers;

    @Ignore
    @Json(name = "current_specific_approvers")
    private SpecificApprovers currentSpecificApprovers;

    @Ignore
    @Json(name = "logged_is_approver")
    private boolean loggedIsApprover;

    @Ignore
    @Json(name = "workflow_approval_history")
    private List<ApproverHistory> workflowApprovalHistory;

    @Ignore
    @Json(name = "pending_approval")
    private List<Integer> pendingApproval;

    @Ignore
    @Json(name = "next_status_requirements")
    private NextStatusRequirements nextStatusRequirements;

    @Ignore
    @Json(name = "peopleRelated")
    private List<PersonRelated> peopleRelated = null;

    public void normalizeColumns() {
        this.titleNormalized = getNormalizedString(getTitle());
        this.descriptionNormalized = getNormalizedString(getDescription());
    }

    static public String getNormalizedString(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public String getTitleNormalized() {
        return titleNormalized;
    }

    public void setTitleNormalized(String titleNormalized) {
        this.titleNormalized = titleNormalized;
    }

    public String getDescriptionNormalized() {
        return descriptionNormalized;
    }

    public void setDescriptionNormalized(String descriptionNormalized) {
        this.descriptionNormalized = descriptionNormalized;
    }

    public WorkflowTypeDb getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowTypeDb workflowType) {
        this.workflowType = workflowType;
    }

    public int getWorkflowTypeOriginalId() {
        return workflowTypeOriginalId;
    }

    public void setWorkflowTypeOriginalId(int workflowTypeOriginalId) {
        this.workflowTypeOriginalId = workflowTypeOriginalId;
    }

    public List<Integer> getPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(List<Integer> pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public List<ApproverHistory> getWorkflowApprovalHistory() {
        ApproverHistory.sortList(workflowApprovalHistory);
        return workflowApprovalHistory;
    }

    public void setWorkflowApprovalHistory(List<ApproverHistory> workflowApprovalHistory) {
        this.workflowApprovalHistory = workflowApprovalHistory;
    }

    public boolean isLoggedIsApprover() {
        return loggedIsApprover;
    }

    public void setLoggedIsApprover(boolean loggedIsApprover) {
        this.loggedIsApprover = loggedIsApprover;
    }

    public SpecificApprovers getCurrentSpecificApprovers() {
        return currentSpecificApprovers;
    }

    public void setCurrentSpecificApprovers(SpecificApprovers currentSpecificApprovers) {
        this.currentSpecificApprovers = currentSpecificApprovers;
    }

    public SpecificApprovers getSpecificApprovers() {
        return specificApprovers;
    }

    public void setSpecificApprovers(SpecificApprovers specificApprovers) {
        this.specificApprovers = specificApprovers;
    }

    public List<Meta> getMetas() {
        return metas;
    }

    public void setMetas(List<Meta> metas) {
        this.metas = metas;
    }

    public List<Integer> getProfilesInvolved() {
        return profilesInvolved;
    }

    public void setProfilesInvolved(List<Integer> profilesInvolved) {
        this.profilesInvolved = profilesInvolved;
    }

    public List<Integer> getCurrentStatusRelations() {
        return currentStatusRelations;
    }

    public void setCurrentStatusRelations(List<Integer> currentStatusRelations) {
        this.currentStatusRelations = currentStatusRelations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public WorkflowUser getAuthor() {
        return author;
    }

    public void setAuthor(WorkflowUser author) {
        this.author = author;
    }

    public List<Person> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Person> assignees) {
        this.assignees = assignees;
    }

    public List<Object> getResponsible() {
        return responsible;
    }

    public void setResponsible(List<Object> responsible) {
        this.responsible = responsible;
    }

    public List<Preset> getPresets() {
        return presets;
    }

    public void setPresets(List<Preset> presets) {
        this.presets = presets;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public void setWorkflowTypeId(int workflowTypeId) {
        this.workflowTypeId = workflowTypeId;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatusName() {
        return currentStatusName;
    }

    public void setCurrentStatusName(String currentStatusName) {
        this.currentStatusName = currentStatusName;
    }

    public NextStatusRequirements getNextStatusRequirements() {
        return nextStatusRequirements;
    }

    public void setNextStatusRequirements(NextStatusRequirements nextStatusRequirements) {
        this.nextStatusRequirements = nextStatusRequirements;
    }

    public List<PersonRelated> getPeopleRelated() {
        return peopleRelated;
    }

    public void setPeopleRelated(List<PersonRelated> peopleRelated) {
        this.peopleRelated = peopleRelated;
    }

    public boolean isStatusPendingForApproval(int statusId){
        for (Integer id : getPendingApproval()) {
            if (statusId == id){
                return true;
            }
        }

        return false;
    }
}
