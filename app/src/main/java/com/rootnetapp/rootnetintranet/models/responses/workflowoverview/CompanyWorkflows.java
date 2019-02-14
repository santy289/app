
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class CompanyWorkflows {

    @Json(name = "pending")
    private Pending pending;
    @Json(name = "open")
    private Open open;
    @Json(name = "closed")
    private Closed closed;
    @Json(name = "out_of_time")
    private OutOfTime outOfTime;
    @Json(name = "updated")
    private Updated updated;
    @Json(name = "persons_involved")
    private Integer personsInvolved;

    public Pending getPending() {
        return pending;
    }

    public void setPending(Pending pending) {
        this.pending = pending;
    }

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }

    public Closed getClosed() {
        return closed;
    }

    public void setClosed(Closed closed) {
        this.closed = closed;
    }

    public OutOfTime getOutOfTime() {
        return outOfTime;
    }

    public void setOutOfTime(OutOfTime outOfTime) {
        this.outOfTime = outOfTime;
    }

    public Updated getUpdated() {
        return updated;
    }

    public void setUpdated(Updated updated) {
        this.updated = updated;
    }

    public Integer getPersonsInvolved() {
        return personsInvolved;
    }

    public void setPersonsInvolved(Integer personsInvolved) {
        this.personsInvolved = personsInvolved;
    }

}
