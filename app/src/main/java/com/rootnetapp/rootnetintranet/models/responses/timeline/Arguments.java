package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

/**
 * Created by root on 11/04/18.
 */

public class Arguments {

    @Json(name = "companyName")
    private String companyName;
    @Json(name = "contactName")
    private String contactName;
    @Json(name = "accountId")
    private int accountId;
    @Json(name = "Done")
    private boolean done;
    @Json(name = "Description")
    private String description;
    @Json(name = "Datest")
    private String datest;
    @Json(name = "statusName")
    private String statusName;
    @Json(name = "guidelineId")
    private int guidelineId;
    @Json(name = "guidelineContactTypeId")
    private int guidelineContactTypeId;
    @Json(name = "guidelineContactTypeName")
    private String guidelineContactTypeName;
    @Json(name = "status")
    private boolean status;
    @Json(name = "previousStatus")
    private int previousStatus;
    @Json(name = "prevStatusName")
    private String prevStatusName;
    @Json(name = "prevGuidelineId")
    private int prevGuidelineId;
    @Json(name = "prevGuidelineContactTypeId")
    private int prevGuidelineContactTypeId;
    @Json(name = "prevGuidelineContactTypeName")
    private String prevGuidelineContactTypeName;
    @Json(name = "sprintName")
    private String sprintName;
    @Json(name = "goalName")
    private String goalName;
    @Json(name = "Contact")
    private Contact contact;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatest() {
        return datest;
    }

    public void setDatest(String datest) {
        this.datest = datest;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getGuidelineId() {
        return guidelineId;
    }

    public void setGuidelineId(int guidelineId) {
        this.guidelineId = guidelineId;
    }

    public int getGuidelineContactTypeId() {
        return guidelineContactTypeId;
    }

    public void setGuidelineContactTypeId(int guidelineContactTypeId) {
        this.guidelineContactTypeId = guidelineContactTypeId;
    }

    public String getGuidelineContactTypeName() {
        return guidelineContactTypeName;
    }

    public void setGuidelineContactTypeName(String guidelineContactTypeName) {
        this.guidelineContactTypeName = guidelineContactTypeName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(int previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getPrevStatusName() {
        return prevStatusName;
    }

    public void setPrevStatusName(String prevStatusName) {
        this.prevStatusName = prevStatusName;
    }

    public int getPrevGuidelineId() {
        return prevGuidelineId;
    }

    public void setPrevGuidelineId(int prevGuidelineId) {
        this.prevGuidelineId = prevGuidelineId;
    }

    public int getPrevGuidelineContactTypeId() {
        return prevGuidelineContactTypeId;
    }

    public void setPrevGuidelineContactTypeId(int prevGuidelineContactTypeId) {
        this.prevGuidelineContactTypeId = prevGuidelineContactTypeId;
    }

    public String getPrevGuidelineContactTypeName() {
        return prevGuidelineContactTypeName;
    }

    public void setPrevGuidelineContactTypeName(String prevGuidelineContactTypeName) {
        this.prevGuidelineContactTypeName = prevGuidelineContactTypeName;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
