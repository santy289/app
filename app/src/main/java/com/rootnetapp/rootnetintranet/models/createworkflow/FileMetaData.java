package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

public class FileMetaData {
    @Json(name = "value")
    public int value;

    @Json(name = "name")
    public String name;
}
