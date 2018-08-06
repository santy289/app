package com.rootnetapp.rootnetintranet.models.responses.manager;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 25/04/18.
 */

public class ManagerItem {

    @Json(name = "list")
    private List<Workflow> list = null;
    @Json(name = "count")
    private int count;

    public List<Workflow> getList() {
        return list;
    }

    public void setList(List<Workflow> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
