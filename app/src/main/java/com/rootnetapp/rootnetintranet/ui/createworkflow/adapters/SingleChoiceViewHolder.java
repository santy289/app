package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;

import androidx.recyclerview.widget.RecyclerView;

public class SingleChoiceViewHolder extends RecyclerView.ViewHolder {

    private final FormItemSingleChoiceBinding mBinding;

    public SingleChoiceViewHolder(FormItemSingleChoiceBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    protected FormItemSingleChoiceBinding getBinding() {
        return mBinding;
    }
}