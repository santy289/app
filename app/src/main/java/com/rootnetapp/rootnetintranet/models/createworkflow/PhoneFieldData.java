package com.rootnetapp.rootnetintranet.models.createworkflow;

import androidx.room.ColumnInfo;

public class PhoneFieldData {
    @ColumnInfo(name = "country_id")
    public int countryId;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "phone_code")
    public String phoneCode;
}
