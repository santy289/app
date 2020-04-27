package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import com.rootnetapp.rootnetintranet.databinding.ItemBookingBinding;

import androidx.recyclerview.widget.RecyclerView;

public class BookingViewHolder extends RecyclerView.ViewHolder {

    private final ItemBookingBinding binding;

    public BookingViewHolder(ItemBookingBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemBookingBinding getBinding() {
        return binding;
    }
}
