package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Created by root on 12/04/18.
 */

public class InteractionResponse {

    @Json(name = "list")
    private List<ItemComments> list = null;

    public List<ItemComments> getList() {
        return list;
    }

    public void setList(List<ItemComments> list) {
        this.list = list;
    }

}
