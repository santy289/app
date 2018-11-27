
package com.rootnetapp.rootnetintranet.models.responses.activation;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.squareup.moshi.Json;

import java.util.List;

public class WorkflowActivationResponse {

    @Json(name = "data")
    private List<List<WorkflowDb>> data = null;

    public List<List<WorkflowDb>> getData() {
        return data;
    }

    public void setData(List<List<WorkflowDb>> data) {
        this.data = data;
    }

}
