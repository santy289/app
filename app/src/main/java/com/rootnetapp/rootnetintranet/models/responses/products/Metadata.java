package com.rootnetapp.rootnetintranet.models.responses.products;

import com.squareup.moshi.Json;

/**
 * Created by root on 26/03/18.
 */

public class Metadata {
    @Json(name = "contact_type_product_field_id")
    private Integer contactTypeProductFieldId;
    @Json(name = "contact_type_product_id")
    private Integer contactTypeProductId;
    @Json(name = "value")
    private String value;

    public Integer getContactTypeProductFieldId() {
        return contactTypeProductFieldId;
    }

    public void setContactTypeProductFieldId(Integer contactTypeProductFieldId) {
        this.contactTypeProductFieldId = contactTypeProductFieldId;
    }

    public Integer getContactTypeProductId() {
        return contactTypeProductId;
    }

    public void setContactTypeProductId(Integer contactTypeProductId) {
        this.contactTypeProductId = contactTypeProductId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
