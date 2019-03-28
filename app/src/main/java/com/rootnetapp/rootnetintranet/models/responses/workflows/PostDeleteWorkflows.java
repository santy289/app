package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

import java.util.List;

public class PostDeleteWorkflows {

    @Json(name = "workflows_array")
    private List<Integer> workflowsArray = null;

    public List<Integer> getWorkflowsArray() {
        return workflowsArray;
    }

    public void setWorkflowsArray(List<Integer> workflowsArray) {
        this.workflowsArray = workflowsArray;
    }

}