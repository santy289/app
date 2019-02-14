
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

import java.util.List;

public class InteractionResponse {

    @Json(name = "list")
    private List<Interaction> list = null;

    public List<Interaction> getList() {
        return list;
    }

    public void setList(java.util.List<Interaction> list) {
        this.list = list;
    }
}
