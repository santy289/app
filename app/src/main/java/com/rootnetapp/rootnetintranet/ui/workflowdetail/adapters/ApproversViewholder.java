package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.ApproversItemBinding;

/**
 * Created by root on 02/04/18.
 */

public class ApproversViewholder extends RecyclerView.ViewHolder {

    final ApproversItemBinding binding;

    public ApproversViewholder(ApproversItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
