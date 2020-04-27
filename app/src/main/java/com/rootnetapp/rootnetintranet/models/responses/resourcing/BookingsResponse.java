package com.rootnetapp.rootnetintranet.models.responses.resourcing;

import com.squareup.moshi.Json;

import java.util.List;

public class BookingsResponse {

    @Json(name = "response")
    private List<BookingWrapper> response = null;

    public List<BookingWrapper> getResponse() {
        return response;
    }

    public void setResponse(List<BookingWrapper> response) {
        this.response = response;
    }

}