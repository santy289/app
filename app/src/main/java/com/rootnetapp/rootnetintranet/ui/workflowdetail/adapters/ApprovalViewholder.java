package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;

public class ApprovalViewholder extends RecyclerView.ViewHolder {

    public final ApprovalHistoryItemBinding binding;

    public ApprovalViewholder(ApprovalHistoryItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
