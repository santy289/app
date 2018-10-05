package com.rootnetapp.rootnetintranet.models.createworkflow;

import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.squareup.moshi.Json;

import java.util.List;

public class CreateRequest {
    @Json(name = "workflow_type_id")
    public int workflowTypeId;

    @Json(name = "title")
    public String title;

    @Json(name = "workflow_metas")
    public List<WorkflowMetas> metas;

    @Json(name = "start")
    public String start;

    @Json(name = "description")
    public String description;
}
