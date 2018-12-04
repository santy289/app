package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.adapters;

import com.rootnetapp.rootnetintranet.databinding.WorkflowSearchItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class WorkflowListViewHolder extends RecyclerView.ViewHolder {

    final WorkflowSearchItemBinding binding;

    public WorkflowListViewHolder(WorkflowSearchItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
