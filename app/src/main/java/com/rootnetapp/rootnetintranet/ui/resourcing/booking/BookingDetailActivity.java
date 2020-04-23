package com.rootnetapp.rootnetintranet.ui.resourcing.booking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityBookingDetailBinding;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "Extra.BookingId";
    public static final String EXTRA_BOOKING_RECORD = "Extra.BookingRecord";

    private static final String TAG = "BookingDetailActivity";

    @Inject
    BookingDetailViewModelFactory mViewModelFactory;
    private BookingDetailViewModel mViewModel;
    private ActivityBookingDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        mViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(BookingDetailViewModel.class);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");

        setActionBar();
        subscribe();

        int bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, 0);
        String bookingRecord = getIntent().getStringExtra(EXTRA_BOOKING_RECORD);
        mViewModel.init(token, bookingId, bookingRecord);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTitle());
    }

    private void subscribe() {
        mViewModel.getObservableShowLoading().observe(this, this::showLoading);
        mViewModel.getObservableShowToastMessage().observe(this, this::showToastMessage);
        mViewModel.getObservableBooking().observe(this, this::fillBookingInformation);
    }

    @UiThread
    private void fillBookingInformation(Booking booking) {
        mBinding.tvUserValue.setText(booking.getPersonName());
        mBinding.tvAuthorValue.setText(booking.getAuthor().getName());
        mBinding.tvBookingTypeValue.setText(booking.getBookingType());
        mBinding.tvRecordValue.setText(booking.getTitle());
        mBinding.tvDescriptionValue.setText(booking.getDescription());
        mBinding.tvDayFromValue.setText(booking.getInitialDate());
        mBinding.tvDayToValue.setText(booking.getEndDate());
        mBinding.tvEstimatedTimeValue.setText(String.valueOf(booking.getEstimatedTimeHours()));
        mBinding.tvRegisteredTimeValue.setText(String.valueOf(booking.getRegisteredTimeHours()));

        if (booking.getPersonAvatarBytes() != null && booking
                .getPersonAvatarBytes().length > 0) {
            Glide.with(this)
                    .load(booking.getPersonAvatarBytes())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    )
                    .into(mBinding.ivUser);
        }
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
}