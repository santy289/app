package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;

/**
 * Created by root on 03/04/18.
 */

public class ApprovalViewholder extends RecyclerView.ViewHolder {

    private final ApprovalHistoryItemBinding binding;

    public ApprovalViewholder(ApprovalHistoryItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
