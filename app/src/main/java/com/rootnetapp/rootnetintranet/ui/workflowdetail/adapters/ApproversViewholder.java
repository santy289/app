package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.ApproversItemBinding;

public class ApproversViewholder extends RecyclerView.ViewHolder {

    final ApproversItemBinding binding;

    public ApproversViewholder(ApproversItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
