package com.rootnetapp.rootnetintranet.models.responses.manager;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 25/04/18.
 */

public class PersonsInvolved {

    @Json(name = "list")
    private List<Object> list = null;
    @Json(name = "count")
    private int count;

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}