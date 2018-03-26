package com.rootnetapp.rootnetintranet.models.responses.services;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 26/03/18.
 */

public class ServicesResponse {

    @Json(name = "code")
    private int code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<Service> list = null;
    @Json(name = "pager")
    private List<Object> pager = null;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Service> getList() {
        return list;
    }

    public void setList(List<Service> list) {
        this.list = list;
    }

    public List<Object> getPager() {
        return pager;
    }

    public void setPager(List<Object> pager) {
        this.pager = pager;
    }

}