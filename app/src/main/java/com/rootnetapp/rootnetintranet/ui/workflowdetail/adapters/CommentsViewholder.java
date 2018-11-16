package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;

public class CommentsViewholder extends RecyclerView.ViewHolder {

    final CommentsItemBinding binding;

    public CommentsViewholder(CommentsItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
