package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 23/03/18.
 */

public class WorkflowTypesResponse {

    @Json(name = "list")
    private List<WorkflowType> list = null;

    public List<WorkflowType> getList() {
        return list;
    }

    public void setList(List<WorkflowType> list) {
        this.list = list;
    }

}
