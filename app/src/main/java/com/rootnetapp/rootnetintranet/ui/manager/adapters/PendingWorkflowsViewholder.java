package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.WorkflowManagerItemBinding;

/**
 * Created by root on 18/04/18.
 */

public class PendingWorkflowsViewholder extends RecyclerView.ViewHolder {

    final WorkflowManagerItemBinding binding;

    public PendingWorkflowsViewholder(WorkflowManagerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
