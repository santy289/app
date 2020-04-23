package com.rootnetapp.rootnetintranet.models.responses.people;

import com.squareup.moshi.Json;

import java.util.List;

public class PeopleDirectoryResponse {

    @Json(name = "people")
    private List<PersonDirectory> list = null;

    public List<PersonDirectory> getList() {
        return list;
    }

    public void setList(List<PersonDirectory> list) {
        this.list = list;
    }

}