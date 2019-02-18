
package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

import java.util.List;

public class TimelineResponse {

    @Json(name = "items")
    private List<TimelineItem> list = null;
    @Json(name = "pager")
    private TimelinePager pager;

    public List<TimelineItem> getList() {
        return list;
    }

    public void setList(List<TimelineItem> list) {
        this.list = list;
    }

    public TimelinePager getPager() {
        return pager;
    }

    public void setPager(TimelinePager pager) {
        this.pager = pager;
    }
}
