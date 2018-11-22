package com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters;

import com.rootnetapp.rootnetintranet.databinding.InformationItemBinding;

import androidx.recyclerview.widget.RecyclerView;

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
