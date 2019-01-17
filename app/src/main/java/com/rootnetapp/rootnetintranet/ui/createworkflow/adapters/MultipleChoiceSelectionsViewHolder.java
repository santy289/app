package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormMultipleChoiceSelectionItemBinding;

import androidx.recyclerview.widget.RecyclerView;

public class MultipleChoiceSelectionsViewHolder extends RecyclerView.ViewHolder {

    final FormMultipleChoiceSelectionItemBinding binding;

    public MultipleChoiceSelectionsViewHolder(FormMultipleChoiceSelectionItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
