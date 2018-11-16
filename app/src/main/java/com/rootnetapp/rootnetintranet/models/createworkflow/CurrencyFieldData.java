package com.rootnetapp.rootnetintranet.models.createworkflow;

import androidx.room.ColumnInfo;

public class CurrencyFieldData {
    @ColumnInfo(name = "country_id")
    public int countryId;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "currency")
    public String currency;
}
