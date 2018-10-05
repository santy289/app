package com.rootnetapp.rootnetintranet.models.responses.role;

import com.squareup.moshi.Json;

import java.util.List;

public class RoleResponse {
    @Json(name = "code")
    private int code;

    @Json(name = "list")
    private List<Role> list;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Role> getList() {
        return list;
    }

    public void setList(List<Role> list) {
        this.list = list;
    }
}
