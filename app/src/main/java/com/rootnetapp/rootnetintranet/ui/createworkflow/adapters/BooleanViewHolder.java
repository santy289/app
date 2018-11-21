package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormItemBooleanBinding;

import androidx.recyclerview.widget.RecyclerView;

public class BooleanViewHolder extends RecyclerView.ViewHolder {

    private final FormItemBooleanBinding mBinding;

    public BooleanViewHolder(FormItemBooleanBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    protected FormItemBooleanBinding getBinding() {
        return mBinding;
    }
}