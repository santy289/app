
package com.rootnetapp.rootnetintranet.models.responses.googlemaps;

import com.squareup.moshi.Json;

public class Geometry {

    @Json(name = "location")
    private Location location;
    @Json(name = "viewport")
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
