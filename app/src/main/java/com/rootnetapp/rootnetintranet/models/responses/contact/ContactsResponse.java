package com.rootnetapp.rootnetintranet.models.responses.contact;

import com.squareup.moshi.Json;

import java.util.List;

public class ContactsResponse {

    @Json(name = "code")
    private Integer code;
    @Json(name = "status")
    private String status;
    @Json(name = "list")
    private List<Contact> contacts = null;

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

    public List<Contact> getList() {
        return contacts;
    }

    public void setList(List<Contact> list) {
        this.contacts = list;
    }

}