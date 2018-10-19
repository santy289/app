package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;

public class ApprovalViewholder extends RecyclerView.ViewHolder {

    public final ApprovalHistoryItemBinding binding;

    public ApprovalViewholder(ApprovalHistoryItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
