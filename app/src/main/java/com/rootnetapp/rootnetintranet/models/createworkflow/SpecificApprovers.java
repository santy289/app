package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

import java.util.List;

public class SpecificApprovers {
    @Json(name = "global")
    public List<Integer> global;

    @Json(name = "statusSpecific")
    public List<StatusSpecific> statusSpecific;
}
