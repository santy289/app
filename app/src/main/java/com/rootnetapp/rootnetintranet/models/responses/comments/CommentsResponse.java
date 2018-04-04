package com.rootnetapp.rootnetintranet.models.responses.comments;

import com.rootnetapp.rootnetintranet.models.responses.workflows.Pager;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class CommentsResponse {

    @Json(name = "response")
    private List<Comment> response = null;
    @Json(name = "pager")
    private Pager pager;

    public List<Comment> getResponse() {
        return response;
    }

    public void setResponse(List<Comment> response) {
        this.response = response;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

}