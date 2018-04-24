package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.DepartmentItemBinding;

/**
 * Created by root on 28/03/18.
 */

class DepartmentViewholder extends RecyclerView.ViewHolder {

    final DepartmentItemBinding binding;

    public DepartmentViewholder(DepartmentItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

}
