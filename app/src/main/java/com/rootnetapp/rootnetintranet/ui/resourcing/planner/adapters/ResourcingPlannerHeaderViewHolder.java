package com.rootnetapp.rootnetintranet.ui.resourcing.planner.adapters;

import com.rootnetapp.rootnetintranet.databinding.ItemResourcingHeaderBinding;

import androidx.recyclerview.widget.RecyclerView;

public class ResourcingPlannerHeaderViewHolder extends RecyclerView.ViewHolder {

    private final ItemResourcingHeaderBinding binding;

    public ResourcingPlannerHeaderViewHolder(ItemResourcingHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemResourcingHeaderBinding getBinding() {
        return binding;
    }
}
