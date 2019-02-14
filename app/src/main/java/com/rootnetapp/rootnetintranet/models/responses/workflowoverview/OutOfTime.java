
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class OutOfTime {

    @Json(name = "count")
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
