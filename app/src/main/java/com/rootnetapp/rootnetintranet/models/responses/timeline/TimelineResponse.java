package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.rootnetapp.rootnetintranet.models.responses.workflows.Pager;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 11/04/18.
 */

public class TimelineResponse {

    @Json(name = "items")
    private List<TimelineItem> items = null;
    @Json(name = "pager")
    private Pager pager;

    public List<TimelineItem> getItems() {
        return items;
    }

    public void setItems(List<TimelineItem> items) {
        this.items = items;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

}