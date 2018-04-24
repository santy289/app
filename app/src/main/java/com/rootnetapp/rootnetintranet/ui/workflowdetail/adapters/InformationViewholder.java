package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.InformationItemBinding;

/**
 * Created by root on 03/04/18.
 */

public class InformationViewholder extends RecyclerView.ViewHolder {

    final InformationItemBinding binding;

    public InformationViewholder(InformationItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
