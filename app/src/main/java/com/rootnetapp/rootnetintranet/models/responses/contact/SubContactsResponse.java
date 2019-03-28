package com.rootnetapp.rootnetintranet.models.responses.contact;

import com.squareup.moshi.Json;

import java.util.List;

public class SubContactsResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<SubContact> subContacts = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SubContact> getList() {
        return subContacts;
    }

    public void setList(List<SubContact> list) {
        this.subContacts = list;
    }

}