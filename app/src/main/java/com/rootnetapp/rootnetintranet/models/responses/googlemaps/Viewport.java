
package com.rootnetapp.rootnetintranet.models.responses.googlemaps;

import com.squareup.moshi.Json;

public class Viewport {

    @Json(name = "northeast")
    private Northeast northeast;
    @Json(name = "southwest")
    private Southwest southwest;

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

}
