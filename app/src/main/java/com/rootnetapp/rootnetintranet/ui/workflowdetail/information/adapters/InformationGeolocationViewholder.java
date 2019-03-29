package com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters;

import com.rootnetapp.rootnetintranet.databinding.InformationItemGeolocationBinding;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 03/04/18.
 */

public class InformationGeolocationViewholder extends RecyclerView.ViewHolder {

    final InformationItemGeolocationBinding binding;

    public InformationGeolocationViewholder(InformationItemGeolocationBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
