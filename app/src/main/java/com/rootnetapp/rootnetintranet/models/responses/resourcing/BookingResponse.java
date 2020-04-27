package com.rootnetapp.rootnetintranet.models.responses.resourcing;


import com.squareup.moshi.Json;

import java.util.List;

public class BookingResponse {

    @Json(name = "booking")
    private Booking booking;
    @Json(name = "history")
    private List<History> history = null;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(
            List<History> history) {
        this.history = history;
    }
}