package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import com.rootnetapp.rootnetintranet.databinding.CommentsItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class CommentsViewholder extends RecyclerView.ViewHolder {

    final CommentsItemBinding binding;

    public CommentsViewholder(CommentsItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
