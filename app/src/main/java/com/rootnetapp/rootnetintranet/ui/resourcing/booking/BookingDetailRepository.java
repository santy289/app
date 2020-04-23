package com.rootnetapp.rootnetintranet.ui.resourcing.booking;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookingDetailRepository {

    private final ApiInterface service;

    private static final String TAG = "BookingDetailRepo";

    public BookingDetailRepository(ApiInterface service) {
        this.service = service;
    }

    public Observable<BookingResponse> getBooking(String auth, int bookingId) {
        return service.getBooking(auth, bookingId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}