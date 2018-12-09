package com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.adapters;

import com.rootnetapp.rootnetintranet.databinding.AttachmentItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class AttachmentsViewHolder extends RecyclerView.ViewHolder {

    final AttachmentItemBinding binding;

    public AttachmentsViewHolder(AttachmentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
