package com.rootnetapp.rootnetintranet.ui.resourcing.planner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityResourcingPlannerBinding;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.resourcing.booking.BookingDetailActivity;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters.ResourcingPlannerAdapter;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.models.PersonBooking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ResourcingPlannerActivity extends AppCompatActivity implements
        ResourcingPlannerActivityInterface {

    private static final String TAG = "ResourcingPlannerActivity";

    @Inject
    ResourcingPlannerViewModelFactory mViewModelFactory;
    private ResourcingPlannerViewModel mViewModel;
    private ActivityResourcingPlannerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_resourcing_planner);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        mViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(ResourcingPlannerViewModel.class);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");

        setActionBar();
        setOnClickListeners();
        setSearchBarListener();
        subscribe();

        mViewModel.init(token);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTitle());
    }

    private void setOnClickListeners() {
        mBinding.btnPrevious.setOnClickListener(v -> mViewModel.fetchPreviousWeek());
        mBinding.btnNext.setOnClickListener(v -> mViewModel.fetchNextWeek());
    }

    private void setSearchBarListener() {
        mBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mViewModel.filterPeople(s.toString());
            }
        });
    }

    private void subscribe() {
        mViewModel.getObservableShowLoading().observe(this, this::showLoading);
        mViewModel.getObservableShowToastMessage().observe(this, this::showToastMessage);
        mViewModel.getObservableBookingMap().observe(this, this::populateBookings);
        mViewModel.getObservableCurrentDateFilter().observe(this, this::setDateFilterText);
        mViewModel.getObservableClearBookings().observe(this, this::clearBookings);
        mViewModel.getObservableShowSearchPeopleFilter()
                .observe(this, this::showSearchPeopleFilter);
    }

    @UiThread
    private void populateBookings(Map<PersonBooking, List<Booking>> personBookingListMap) {
        mBinding.rvResourcing.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvResourcing.setAdapter(new ResourcingPlannerAdapter(personBookingListMap,
                mViewModel.getCurrentStartDate(), this));
        mBinding.rvResourcing.setNestedScrollingEnabled(false);
        mBinding.hsv.smoothScrollTo(0, 0);
//        mBinding.hsv.scrollTo(0, 0);
    }

    @UiThread
    private void clearBookings(Boolean clear) {
        if (!clear) {
            return;
        }

        mBinding.rvResourcing.setAdapter(
                new ResourcingPlannerAdapter(new HashMap<>(), mViewModel.getCurrentStartDate(),
                        this));
    }

    @UiThread
    private void setDateFilterText(String dateFilterText) {
        mBinding.tvDateFilter.setText(dateFilterText);
    }

    @UiThread
    private void showSearchPeopleFilter(Boolean show) {
        mBinding.inputSearch.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showBookingDetail(Booking booking) {
        Intent intent = new Intent(this, BookingDetailActivity.class);
        intent.putExtra(BookingDetailActivity.EXTRA_BOOKING_ID, booking.getId());
        intent.putExtra(BookingDetailActivity.EXTRA_BOOKING_RECORD, booking.getTitle());
        startActivity(intent);
    }

}