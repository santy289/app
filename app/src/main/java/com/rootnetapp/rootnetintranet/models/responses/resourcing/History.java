package com.rootnetapp.rootnetintranet.models.responses.resourcing;

import com.squareup.moshi.Json;

public class History {

    @Json(name = "id")
    private Integer id;
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
    private Integer estimatedTime;
    @Json(name = "booking_type")
    private String bookingType;
    @Json(name = "person_id")
    private Integer personId;
    @Json(name = "person_avatar")
    private String personAvatar;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPersonAvatar() {
        return personAvatar;
    }

    public void setPersonAvatar(String personAvatar) {
        this.personAvatar = personAvatar;
    }
}