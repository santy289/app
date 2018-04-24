package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by Propietario on 15/03/2018.
 */

public class WorkflowsResponse {

    @Json(name = "list")
    private List<Workflow> list = null;
    @Json(name = "pager")
    private Pager pager;
    @Json(name = "stateCounter")
    private StateCounter stateCounter;

    public List<Workflow> getList() {
        return list;
    }

    public void setList(List<Workflow> list) {
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
