package com.rootnetapp.rootnetintranet.models.responses.workflows;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 15/03/2018.
 */

public class VirtualColumns {

    @Json(name = "name")
    private String name;
    @Json(name = "order")
    private String order;
    @Json(name = "last_update")
    private String lastUpdate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}