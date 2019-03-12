
package com.rootnetapp.rootnetintranet.models.responses.googlemaps;

import com.squareup.moshi.Json;

public class OpeningHours {

    @Json(name = "open_now")
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

}
