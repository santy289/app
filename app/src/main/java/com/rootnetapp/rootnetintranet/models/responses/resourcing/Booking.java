package com.rootnetapp.rootnetintranet.models.responses.resourcing;

import com.squareup.moshi.Json;

public class Booking {

    @Json(name = "id")
    private int id;
    @Json(name = "description")
    private String description;
    @Json(name = "billable")
    private Boolean billable;
    @Json(name = "initialDate")
    private String initialDate;
    @Json(name = "end_date")
    private String endDate;
    @Json(name = "start_time")
    private String startTime;
    @Json(name = "end_time")
    private String endTime;
    @Json(name = "priority")
    private String priority;
    @Json(name = "record")
    private Integer record;
    @Json(name = "estimated_time")
    private Long estimatedTime;
    @Json(name = "registered_time")
    private Long registeredTime;
    @Json(name = "booking_type")
    private @BookingType String bookingType;
    @Json(name = "registered_confirmed")
    private Boolean registeredConfirmed;
    @Json(name = "person_id")
    private Integer personId;
    @Json(name = "person_name")
    private String personName;
    @Json(name = "person_avatar")
    private String personAvatar;
    @Json(name = "status")
    private Boolean status;
    @Json(name = "version")
    private Integer version;
    @Json(name = "version_created_at")
    private String versionCreatedAt;

    private transient String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBillable() {
        return billable;
    }

    public void setBillable(Boolean billable) {
        this.billable = billable;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getRecord() {
        return record;
    }

    public void setRecord(Integer record) {
        this.record = record;
    }

    public Long getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Long getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(Long registeredTime) {
        this.registeredTime = registeredTime;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Boolean getRegisteredConfirmed() {
        return registeredConfirmed;
    }

    public void setRegisteredConfirmed(Boolean registeredConfirmed) {
        this.registeredConfirmed = registeredConfirmed;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getVersionCreatedAt() {
        return versionCreatedAt;
    }

    public void setVersionCreatedAt(String versionCreatedAt) {
        this.versionCreatedAt = versionCreatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersonAvatar() {
        return personAvatar;
    }

    public void setPersonAvatar(String personAvatar) {
        this.personAvatar = personAvatar;
    }
}
