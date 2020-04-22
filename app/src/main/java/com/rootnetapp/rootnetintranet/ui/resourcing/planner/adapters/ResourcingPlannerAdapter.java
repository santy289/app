package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ItemResourcingBinding;
import com.rootnetapp.rootnetintranet.databinding.ItemResourcingHeaderBinding;
import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.models.PersonBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ResourcingPlannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String DATE_FORMAT = "MMM dd";

    private Map<PersonBooking, List<Booking>> personBookingListMap;
    private List<PersonBooking> personBookingList;
    private Date mondayDate;

    public ResourcingPlannerAdapter(Map<PersonBooking, List<Booking>> personBookingListMap,
                                    Date mondayDate) {
        this.personBookingListMap = personBookingListMap;
        this.mondayDate = mondayDate;

        personBookingList = new ArrayList<>(personBookingListMap.keySet());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ResourcingViewType.HEADER;
        }

        return ResourcingViewType.CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ResourcingViewType.HEADER:
                return new ResourcingPlannerHeaderViewHolder(
                        ItemResourcingHeaderBinding.inflate(layoutInflater, viewGroup, false));
            case ResourcingViewType.CONTENT:
            default:
                return new ResourcingPlannerViewHolder(
                        ItemResourcingBinding.inflate(layoutInflater, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ResourcingViewType.HEADER:
                ResourcingPlannerHeaderViewHolder headerViewHolder = (ResourcingPlannerHeaderViewHolder) holder;

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mondayDate);

                TextView[] dayOfWeekTextViews = {
                        headerViewHolder.getBinding().tvMondayDate,
                        headerViewHolder.getBinding().tvTuesdayDate,
                        headerViewHolder.getBinding().tvWednesdayDate,
                        headerViewHolder.getBinding().tvThursdayDate,
                        headerViewHolder.getBinding().tvFridayDate,
                        headerViewHolder.getBinding().tvSaturdayDate,
                        headerViewHolder.getBinding().tvSundayDate
                };

                for (TextView textView : dayOfWeekTextViews) {
                    String dateString = Utils.getFormattedDate(calendar.getTime(), DATE_FORMAT);
                    textView.setText(dateString);

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                headerViewHolder.getBinding().executePendingBindings();
                break;

            case ResourcingViewType.CONTENT:
                int index = position - 1; //because of header

                ResourcingPlannerViewHolder contentViewHolder = (ResourcingPlannerViewHolder) holder;

                Context context = contentViewHolder.getBinding().getRoot().getContext();

                PersonBooking personBooking = getPersonBooking(index);
                contentViewHolder.getBinding().tvPersonName.setText(personBooking.getPersonName());

                List<Booking> bookingList = getBookingList(index);
                long totalEstimatedTime = bookingList.stream().mapToLong(Booking::getEstimatedTime)
                        .sum();
                int numberOfHours = (int) ((totalEstimatedTime % 86400) / 3600);
                contentViewHolder.getBinding().tvTotalTime.setText(
                        context.getResources().getQuantityString(R.plurals.resourcing_planner_total_hours, numberOfHours, numberOfHours));

                contentViewHolder.getBinding().executePendingBindings();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return personBookingList.size() + 1; //because of header view
    }

    private PersonBooking getPersonBooking(int position) {
        return personBookingList.get(position);
    }

    private List<Booking> getBookingList(int position) {
        return personBookingListMap.get(getPersonBooking(position));
    }
}
