package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.support.v7.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;


/**
 * Created by root on 19/03/18.
 */

public class WorkflowViewholder extends RecyclerView.ViewHolder {

    final WorkflowItemBinding binding;

    /*public void bind(Item item) {
        binding.setItem(item);
        binding.executePendingBindings();
    }*/

    public WorkflowViewholder(WorkflowItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
