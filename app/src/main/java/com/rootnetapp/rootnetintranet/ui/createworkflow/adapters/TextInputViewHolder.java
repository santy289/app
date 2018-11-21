package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;

import androidx.recyclerview.widget.RecyclerView;

public class TextInputViewHolder extends RecyclerView.ViewHolder {

    private FormItemTextInputBinding mBinding;

    public TextInputViewHolder(FormItemTextInputBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }
}
