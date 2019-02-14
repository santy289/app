
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class Open {

    @Json(name = "count")
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
