package com.rootnetapp.rootnetintranet.ui.timeline.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.TimelineCommentItemBinding;

/**
 * Created by root on 10/04/18.
 */

public class TimelineCommentViewholder extends RecyclerView.ViewHolder {

    final TimelineCommentItemBinding binding;

    public TimelineCommentViewholder(TimelineCommentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
