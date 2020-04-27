package com.rootnetapp.rootnetintranet.models.responses.resourcing;

import com.squareup.moshi.Json;

public class BookingWrapper {

    @Json(name = "booking")
    private Booking booking = null;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
