package com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.adapters;

import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class ApprovalViewholder extends RecyclerView.ViewHolder {

    public final ApprovalHistoryItemBinding binding;

    public ApprovalViewholder(ApprovalHistoryItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
