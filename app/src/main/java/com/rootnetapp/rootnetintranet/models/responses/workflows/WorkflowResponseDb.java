package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class WorkflowResponseDb {
    @Json(name = "list")
    private List<WorkflowDb> list = new ArrayList<>();
    @Json(name = "pager")
    private Pager pager;
    @Json(name = "stateCounter")
    private StateCounter stateCounter;

    public List<WorkflowDb> getList() {
        return list;
    }

    public void setList(List<WorkflowDb> list) {
        this.list = list;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public StateCounter getStateCounter() {
        return stateCounter;
    }

    public void setStateCounter(StateCounter stateCounter) {
        this.stateCounter = stateCounter;
    }
}
