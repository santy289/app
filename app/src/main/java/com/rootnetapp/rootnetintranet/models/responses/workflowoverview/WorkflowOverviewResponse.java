
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class WorkflowOverviewResponse {

    @Json(name = "overview")
    private Overview overview;

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

}
