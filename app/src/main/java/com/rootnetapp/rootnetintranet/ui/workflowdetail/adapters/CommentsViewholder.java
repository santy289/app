package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;

public class CommentsViewholder extends RecyclerView.ViewHolder {

    final CommentsItemBinding binding;

    public CommentsViewholder(CommentsItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
