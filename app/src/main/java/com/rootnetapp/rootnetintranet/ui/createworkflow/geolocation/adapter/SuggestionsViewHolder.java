package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.adapter;

import com.rootnetapp.rootnetintranet.databinding.ItemAutocompletePlacesBinding;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 04/04/18.
 */

public class SuggestionsViewHolder extends RecyclerView.ViewHolder {

    private final ItemAutocompletePlacesBinding binding;

    public SuggestionsViewHolder(ItemAutocompletePlacesBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public ItemAutocompletePlacesBinding getBinding() {
        return binding;
    }
}
