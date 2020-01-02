package com.rootnetapp.rootnetintranet.ui.massapproval.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.ItemMassApprovalBinding;

public class MassApprovalViewholder extends RecyclerView.ViewHolder {

    private final ItemMassApprovalBinding binding;

    public MassApprovalViewholder(ItemMassApprovalBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemMassApprovalBinding getBinding() {
        return binding;
    }
}
