package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormAutocompleteSuggestionItemBinding;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 04/04/18.
 */

public class AutocompleteSuggestionsViewHolder extends RecyclerView.ViewHolder {

    private final FormAutocompleteSuggestionItemBinding binding;

    public AutocompleteSuggestionsViewHolder(FormAutocompleteSuggestionItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public FormAutocompleteSuggestionItemBinding getBinding() {
        return binding;
    }
}
