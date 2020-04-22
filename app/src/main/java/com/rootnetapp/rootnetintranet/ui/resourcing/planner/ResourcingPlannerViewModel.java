package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingWrapper;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingsResponse;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.models.PersonBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ResourcingPlannerViewModel extends ViewModel {

    private static final String TAG = "ResourcingPlannerVM";

    private ResourcingPlannerRepository mRepository;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<Map<PersonBooking, List<Booking>>> mBookingMapLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private Date mCurrentStartDate;

    public ResourcingPlannerViewModel(ResourcingPlannerRepository resourcingPlannerRepository) {
        this.mRepository = resourcingPlannerRepository;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    public void init(String token) {
        mToken = token;

        Calendar now = Calendar.getInstance();
        Date startDate = Utils.getWeekStartDate(now);
        Date endDate = Utils.getWeekEndDate(now);

        getBookings(startDate, endDate);
    }

    public Date getCurrentStartDate() {
        return mCurrentStartDate;
    }

    private void getBookings(Date startDate, Date endDate) {
        mCurrentStartDate = startDate;

        String startDateString = Utils.getFormattedDate(startDate, Utils.SERVER_DATE_FORMAT_SHORT);
        String endDateString = Utils.getFormattedDate(endDate, Utils.SERVER_DATE_FORMAT_SHORT);

        mShowLoadingLiveData.setValue(true);
        Disposable disposable = mRepository
                .getBookings(mToken, startDateString, endDateString)
                .subscribe(this::onBookingsSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onBookingsSuccess(BookingsResponse bookingsResponse) {
        mShowLoadingLiveData.setValue(false);

        if (bookingsResponse.getResponse().isEmpty()) {
            //todo error
            return;
        }

        HashMap<PersonBooking, List<Booking>> personBookingMap = new HashMap<>();

        for (BookingWrapper bookingWrapper : bookingsResponse.getResponse()) {
            Booking booking = bookingWrapper.getBooking();

            PersonBooking personBooking = new PersonBooking(
                    booking.getPersonId(),
                    booking.getPersonName());

            if (personBookingMap.containsKey(personBooking)) {
                //if the key exists, the list is initialized
                personBookingMap.get(personBooking).add(booking);
                continue;
            }

            personBookingMap.put(personBooking, new ArrayList<>(Collections.singletonList(booking)));
        }

        mBookingMapLiveData.setValue(personBookingMap);
    }

    private void onFailure(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);
        mShowToastMessage.setValue(Utils.getOnFailureStringRes(throwable));
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (mShowLoadingLiveData == null) {
            mShowLoadingLiveData = new MutableLiveData<>();
        }
        return mShowLoadingLiveData;
    }

    protected LiveData<Integer> getObservableShowToastMessage() {
        if (mShowToastMessage == null) {
            mShowToastMessage = new MutableLiveData<>();
        }
        return mShowToastMessage;
    }

    protected LiveData<Map<PersonBooking, List<Booking>>> getObservableBookingMap() {
        if (mBookingMapLiveData == null) {
            mBookingMapLiveData = new MutableLiveData<>();
        }
        return mBookingMapLiveData;
    }
}