
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

import java.util.List;

public class SubCommentsResponse {

    @Json(name = "list")
    private List<Comment> list = null;
    @Json(name = "pager")
    private CommentsPager pager;

    public List<Comment> getList() {
        return list;
    }

    public void setList(List<Comment> list) {
        this.list = list;
    }

    public CommentsPager getPager() {
        return pager;
    }

    public void setPager(CommentsPager pager) {
        this.pager = pager;
    }

}
