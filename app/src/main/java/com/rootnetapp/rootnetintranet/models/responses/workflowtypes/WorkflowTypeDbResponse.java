package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.squareup.moshi.Json;

import java.util.List;

public class WorkflowTypeDbResponse {
    @Json(name = "list")
    private List<WorkflowTypeDb> list = null;

    public List<WorkflowTypeDb> getList() {
        return list;
    }

    public void setList(List<WorkflowTypeDb> list) {
        this.list = list;
    }
}
