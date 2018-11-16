package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.ManagerDialogItemBinding;

/**
 * Created by root on 20/04/18.
 */

public class ManagerDialogViewholder extends RecyclerView.ViewHolder {

    final ManagerDialogItemBinding binding;

    public ManagerDialogViewholder(ManagerDialogItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
