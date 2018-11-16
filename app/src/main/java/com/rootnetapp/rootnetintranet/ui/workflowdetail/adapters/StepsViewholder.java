package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.StepsItemBinding;

/**
 * Created by root on 02/04/18.
 */

public class StepsViewholder extends RecyclerView.ViewHolder {

    final StepsItemBinding binding;

    public StepsViewholder(StepsItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
