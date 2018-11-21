package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormItemCurrencyBinding;

import androidx.recyclerview.widget.RecyclerView;

public class CurrencyViewHolder extends RecyclerView.ViewHolder {

    private final FormItemCurrencyBinding mBinding;

    public CurrencyViewHolder(FormItemCurrencyBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    protected FormItemCurrencyBinding getBinding(){
        return mBinding;
    }
}