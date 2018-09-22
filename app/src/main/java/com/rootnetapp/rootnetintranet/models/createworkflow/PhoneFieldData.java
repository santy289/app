package com.rootnetapp.rootnetintranet.models.createworkflow;

import android.arch.persistence.room.ColumnInfo;

public class PhoneFieldData {
    @ColumnInfo(name = "country_id")
    public int countryId;

    @ColumnInfo(name = "phone_code")
    public int phoneCode;
}
