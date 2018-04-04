package com.rootnetapp.rootnetintranet.models.responses.templates;

import com.squareup.moshi.Json;

/**
 * Created by root on 04/04/18.
 */

public class UpdatedAt {

    @Json(name = "date")
    private String date;
    @Json(name = "timezone_type")
    private int timezoneType;
    @Json(name = "timezone")
    private String timezone;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTimezoneType() {
        return timezoneType;
    }

    public void setTimezoneType(int timezoneType) {
        this.timezoneType = timezoneType;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}