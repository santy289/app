package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;

public class SpecificApprovers {

    @Json(name = "global")
    public List<Integer> global;

    @Json(name = "statusSpecific")
    public List<StatusSpecific> statusSpecific;

    @Json(name = "role")
    private Object role;

    public Map<String, Object> getRole() {
        return role instanceof Map ? (Map<String, Object>) role : null;
    }
}
