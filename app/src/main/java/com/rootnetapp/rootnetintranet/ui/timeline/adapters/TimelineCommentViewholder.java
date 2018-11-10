package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.TimelineCommentItemBinding;

public class TimelineCommentViewholder extends RecyclerView.ViewHolder {

    final TimelineCommentItemBinding binding;

    public TimelineCommentViewholder(TimelineCommentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
