package com.rootnetapp.rootnetintranet.models.responses.manager;

import com.squareup.moshi.Json;

/**
 * Created by root on 25/04/18.
 */

public class CompanyWorkflows {

    @Json(name = "pending")
    private ManagerItem pending;
    @Json(name = "open")
    private ManagerItem open;
    @Json(name = "closed")
    private ManagerItem closed;
    @Json(name = "out_of_time")
    private ManagerItem outOfTime;
    @Json(name = "updated")
    private ManagerItem updated;
    @Json(name = "persons_involved")
    private PersonsInvolved personsInvolved;

    public ManagerItem getPending() {
        return pending;
    }

    public void setPending(ManagerItem pending) {
        this.pending = pending;
    }

    public ManagerItem getOpen() {
        return open;
    }

    public void setOpen(ManagerItem open) {
        this.open = open;
    }

    public ManagerItem getClosed() {
        return closed;
    }

    public void setClosed(ManagerItem closed) {
        this.closed = closed;
    }

    public ManagerItem getOutOfTime() {
        return outOfTime;
    }

    public void setOutOfTime(ManagerItem outOfTime) {
        this.outOfTime = outOfTime;
    }

    public ManagerItem getUpdated() {
        return updated;
    }

    public void setUpdated(ManagerItem updated) {
        this.updated = updated;
    }

    public PersonsInvolved getPersonsInvolved() {
        return personsInvolved;
    }

    public void setPersonsInvolved(PersonsInvolved personsInvolved) {
        this.personsInvolved = personsInvolved;
    }

}