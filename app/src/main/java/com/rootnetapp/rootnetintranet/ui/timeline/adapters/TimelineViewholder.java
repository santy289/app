package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.TimelineItemBinding;

public class TimelineViewholder extends RecyclerView.ViewHolder {

    final TimelineItemBinding binding;

    public TimelineViewholder(TimelineItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
