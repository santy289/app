package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import com.rootnetapp.rootnetintranet.databinding.ItemResourcingBinding;

import androidx.recyclerview.widget.RecyclerView;

public class ResourcingPlannerViewHolder extends RecyclerView.ViewHolder {

    private final ItemResourcingBinding binding;

    public ResourcingPlannerViewHolder(ItemResourcingBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemResourcingBinding getBinding() {
        return binding;
    }
}
