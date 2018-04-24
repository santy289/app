package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.rootnetapp.rootnetintranet.models.responses.workflows.Pager;
import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 12/04/18.
 */

public class SubCommentsResponse {

        @Json(name = "list")
        private List<Comment> list = null;
        @Json(name = "pager")
        private Pager pager;

        public List<Comment> getList() {
            return list;
        }

    public void setList(java.util.List<Comment> list) {
        this.list = list;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

}
