package com.rootnetapp.rootnetintranet.ui.resourcing.booking;

import android.text.TextUtils;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingResponse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BookingDetailViewModel extends ViewModel {

    public static final int WEEK_DAYS_AMOUNT = 7;

    private static final String TAG = "BookingDetailVM";

    private BookingDetailRepository mRepository;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<Booking> mBookingLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private int mBookingId;
    private String mBookingRecord;

    public BookingDetailViewModel(BookingDetailRepository bookingDetailRepository) {
        this.mRepository = bookingDetailRepository;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    public void init(String token, int bookingId, String bookingRecord) {
        mToken = token;
        mBookingId = bookingId;
        mBookingRecord = bookingRecord;

        getBooking(bookingId);
    }

    private void getBooking(int bookingId) {
        mShowLoadingLiveData.setValue(true);
        Disposable disposable = mRepository
                .getBooking(mToken, bookingId)
                .subscribe(this::onBookingSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onBookingSuccess(BookingResponse bookingResponse) {
        mShowLoadingLiveData.setValue(false);

        Booking booking = bookingResponse.getBooking();

        if (bookingResponse.getBooking() == null) {
            return;
        }

        booking.setTitle(mBookingRecord);

        booking.setInitialDate(Utils.serverFormatToFormat(booking.getInitialDate(),
                Utils.SHORT_DATE_DISPLAY_FORMAT));
        booking.setEndDate(
                Utils.serverFormatToFormat(booking.getEndDate(), Utils.SHORT_DATE_DISPLAY_FORMAT));

        if (booking.getEstimatedTime() == null) {
            booking.setEstimatedTimeHours(0);
        } else {
            booking.setEstimatedTimeHours(Utils.secondsToHours(booking.getEstimatedTime()));
        }

        if (booking.getRegisteredTime() == null) {
            booking.setRegisteredTime(0L);
        } else {
            booking.setRegisteredTimeHours(Utils.secondsToHours(booking.getRegisteredTime()));
        }

        if (!TextUtils.isEmpty(booking.getPersonAvatar())) {
            booking.setPersonAvatarBytes(Utils.decodeImageUri(booking.getPersonAvatar()));
        }

        mBookingLiveData.setValue(bookingResponse.getBooking());
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

    protected LiveData<Booking> getObservableBooking() {
        if (mBookingLiveData == null) {
            mBookingLiveData = new MutableLiveData<>();
        }
        return mBookingLiveData;
    }
}