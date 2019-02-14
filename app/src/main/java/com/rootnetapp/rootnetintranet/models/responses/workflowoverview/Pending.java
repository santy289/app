
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class Pending {

    @Json(name = "count")
    private String count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
