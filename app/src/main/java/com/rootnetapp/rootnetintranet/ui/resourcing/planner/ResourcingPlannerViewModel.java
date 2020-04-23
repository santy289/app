package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.project.Project;
import com.rootnetapp.rootnetintranet.models.responses.project.ProjectResponse;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingType;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingWrapper;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.BookingsResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.Role;
import com.rootnetapp.rootnetintranet.models.responses.role.RoleResponse;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.models.PersonBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ResourcingPlannerViewModel extends ViewModel {

    public static final int WEEK_DAYS_AMOUNT = 7;

    private static final String TAG = "ResourcingPlannerVM";

    private ResourcingPlannerRepository mRepository;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<Map<PersonBooking, List<Booking>>> mBookingMapLiveData;
    private MutableLiveData<String> mCurrentDateFilterLiveData;
    private MutableLiveData<Boolean> mClearBookingsLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private String mToken;
    private Date mCurrentStartDate;
    private HashMap<PersonBooking, List<Booking>> mPersonBookingListMap;

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
        getBookingsForWeek(now);
    }

    protected void fetchNextWeek() {
        fetchWeek(WEEK_DAYS_AMOUNT);
    }

    protected void fetchPreviousWeek() {
        fetchWeek(-WEEK_DAYS_AMOUNT);
    }

    private void fetchWeek(int daysDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentStartDate);
        calendar.add(Calendar.DAY_OF_YEAR, daysDiff);

        getBookingsForWeek(calendar);

        mClearBookingsLiveData.setValue(true);
    }

    private void getBookingsForWeek(Calendar calendar) {
        Date startDate = Utils.getWeekStartDate(calendar);
        Date endDate = Utils.getWeekEndDate(calendar);

        getBookings(startDate, endDate);
    }

    private void getBookings(Date startDate, Date endDate) {
        mCurrentStartDate = startDate;

        mCurrentDateFilterLiveData.setValue(getDateFilterText(startDate, endDate));

        String startDateString = Utils.getFormattedDate(startDate, Utils.SERVER_DATE_FORMAT_SHORT);
        String endDateString = Utils.getFormattedDate(endDate, Utils.SERVER_DATE_FORMAT_SHORT);

        mShowLoadingLiveData.setValue(true);
        Disposable disposable = mRepository
                .getBookings(mToken, startDateString, endDateString)
                .subscribe(this::onBookingsSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onBookingsSuccess(BookingsResponse bookingsResponse) {
        if (bookingsResponse.getResponse().isEmpty()) {
            mShowLoadingLiveData.setValue(false);
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

            personBookingMap
                    .put(personBooking, new ArrayList<>(Collections.singletonList(booking)));
        }

        mPersonBookingListMap = personBookingMap;

        getRoles();
    }

    protected Date getCurrentStartDate() {
        return mCurrentStartDate;
    }

    private void getRoles() {
        Disposable disposable = mRepository
                .getRoles(mToken)
                .subscribe(this::onRolesCompleted, this::onRolesFailure);
        mDisposables.add(disposable);
    }

    private void onRolesCompleted(RoleResponse roleResponse) {
        if (roleResponse.getList().isEmpty()) {
            getProjects();
            return;
        }

        List<Role> roleList = roleResponse.getList();

        mPersonBookingListMap.values().forEach(bookingList -> {
            bookingList.forEach(booking -> {
                if (booking.getBookingType().equals(BookingType.ROLE)) {
                    roleList.stream()
                            .filter(role -> role.getId() == booking.getRecord())
                            .findFirst().ifPresent(role -> booking.setTitle(role.getName()));
                }
            });
        });

        getProjects();
    }

    private void onRolesFailure(Throwable throwable) {
        getProjects();
    }

    private void getProjects() {
        Disposable disposable = mRepository
                .getProjects(mToken)
                .subscribe(this::onProjectsCompleted, this::onProjectsFailure);
        mDisposables.add(disposable);
    }

    private void onProjectsCompleted(ProjectResponse projectResponse) {
        mShowLoadingLiveData.setValue(false);

        if (projectResponse.getProjects().isEmpty()) {
            mBookingMapLiveData.setValue(mPersonBookingListMap);
            return;
        }

        List<Project> projectList = projectResponse.getProjects();

        mPersonBookingListMap.values().forEach(bookingList -> {
            bookingList.forEach(booking -> {
                if (booking.getBookingType().equals(BookingType.PROJECT)) {
                    projectList.stream()
                            .filter(project -> project.getId().equals(booking.getRecord()))
                            .findFirst().ifPresent(project -> booking.setTitle(project.getTitle()));
                }
            });
        });

        mBookingMapLiveData.setValue(mPersonBookingListMap);
    }

    private void onProjectsFailure(Throwable throwable) {
        mBookingMapLiveData.setValue(mPersonBookingListMap);
    }

    private String getDateFilterText(Date startDate, Date endDate) {
        String startDateString = Utils.getFormattedDate(startDate, Utils.SHORT_DATE_NO_YEAR_FORMAT);
        String endDateString = Utils.getFormattedDate(endDate, Utils.SHORT_DATE_NO_YEAR_FORMAT);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        int startYear = startCalendar.get(Calendar.YEAR);
        int endYear = endCalendar.get(Calendar.YEAR);

        if (startYear == endYear) {
            return String.format(Locale.getDefault(), "%s - %s %d", startDateString, endDateString,
                    endYear);
        } else {
            return String.format(Locale.getDefault(), "%s %d - %s %d", startDateString, startYear,
                    endDateString, endYear);
        }
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

    protected LiveData<String> getObservableCurrentDateFilter() {
        if (mCurrentDateFilterLiveData == null) {
            mCurrentDateFilterLiveData = new MutableLiveData<>();
        }
        return mCurrentDateFilterLiveData;
    }

    protected LiveData<Boolean> getObservableClearBookings() {
        if (mClearBookingsLiveData == null) {
            mClearBookingsLiveData = new MutableLiveData<>();
        }
        return mClearBookingsLiveData;
    }
}