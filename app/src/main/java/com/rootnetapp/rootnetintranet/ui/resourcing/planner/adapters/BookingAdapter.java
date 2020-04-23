package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ItemBookingBinding;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookingAdapter extends RecyclerView.Adapter<BookingViewHolder> {

    private static final int WEEK_DAYS_AMOUNT = 7;

    private List<Booking> bookingList;
    private Date weekStartDate;

    public BookingAdapter(List<Booking> bookingList, Date weekStartDate) {
        this.bookingList = bookingList;
        this.weekStartDate = weekStartDate;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());

        return new BookingViewHolder(ItemBookingBinding.inflate(layoutInflater, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = getItem(position);

        Context context = holder.getBinding().getRoot().getContext();

        int bookingHours = Utils.secondsToHours(booking.getEstimatedTime());
        String title = booking.getTitle() == null ? "" : " - " + booking.getTitle();
        holder.getBinding().tvTitle.setText(
                context.getString(R.string.resourcing_planner_booking_title,
                        booking.getBookingType(), title, bookingHours));

        holder.getBinding().tvDescription.setText(booking.getDescription());

        if (booking.getRegisteredTime() != null && booking.getRegisteredTime() > 0) {
            holder.getBinding().viewIndicator
                    .setBackgroundResource(R.drawable.border_bg_booking_indicator_on);
        } else {
            holder.getBinding().viewIndicator
                    .setBackgroundResource(R.drawable.border_bg_booking_indicator_off);
        }

        String startDateString = booking.getInitialDate().replaceAll("T.*", "");
        String endDateString = booking.getEndDate().replaceAll("T.*", "");

        Date startDate = Utils.getDateFromString(startDateString, Utils.SERVER_DATE_FORMAT_SHORT);
        Date endDate = Utils.getDateFromString(endDateString, Utils.SERVER_DATE_FORMAT_SHORT);

        if (startDate != null && endDate != null) {
            long startDiff = Math.abs(TimeUnit.DAYS
                    .convert(weekStartDate.getTime() - startDate.getTime(), TimeUnit.MILLISECONDS));
            long durationDays = Math.abs(TimeUnit.DAYS
                    .convert(startDate.getTime() - endDate.getTime(), TimeUnit.MILLISECONDS));

            float dayBoxSize = context.getResources().getDimension(R.dimen.resourcing_day_width);
            int marginStart = (int) (dayBoxSize * startDiff);
            int marginEnd = (int) ((dayBoxSize * WEEK_DAYS_AMOUNT) - (marginStart + (dayBoxSize * (durationDays + 1))));

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder
                    .getBinding().lytRoot
                    .getLayoutParams();
            params.setMargins(marginStart, 0, marginEnd, 0);
            holder.getBinding().lytRoot.setLayoutParams(params);
        }

        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private Booking getItem(int position) {
        return bookingList.get(position);
    }
}
