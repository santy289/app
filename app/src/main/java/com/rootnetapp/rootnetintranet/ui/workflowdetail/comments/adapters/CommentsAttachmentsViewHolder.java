package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import com.rootnetapp.rootnetintranet.databinding.CommentsAttachmentItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class CommentsAttachmentsViewHolder extends RecyclerView.ViewHolder {

    final CommentsAttachmentItemBinding binding;

    public CommentsAttachmentsViewHolder(CommentsAttachmentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
