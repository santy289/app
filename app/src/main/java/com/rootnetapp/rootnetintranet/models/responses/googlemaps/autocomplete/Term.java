
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete;

import com.squareup.moshi.Json;

public class Term {

    @Json(name = "offset")
    private Integer offset;
    @Json(name = "value")
    private String value;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
