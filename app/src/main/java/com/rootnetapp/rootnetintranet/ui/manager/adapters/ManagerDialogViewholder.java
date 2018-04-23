package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
