
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete;

import com.squareup.moshi.Json;

public class MatchedSubstring {

    @Json(name = "length")
    private Integer length;
    @Json(name = "offset")
    private Integer offset;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}
