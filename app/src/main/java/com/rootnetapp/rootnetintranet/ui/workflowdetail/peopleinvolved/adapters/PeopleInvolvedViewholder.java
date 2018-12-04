package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved.adapters;

import com.rootnetapp.rootnetintranet.databinding.PeopleInvolvedItemBinding;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 03/04/18.
 */

public class PeopleInvolvedViewholder extends RecyclerView.ViewHolder {

    final PeopleInvolvedItemBinding binding;

    public PeopleInvolvedViewholder(PeopleInvolvedItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
