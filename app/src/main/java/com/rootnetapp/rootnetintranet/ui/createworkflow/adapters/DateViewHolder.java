package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import com.rootnetapp.rootnetintranet.databinding.FormItemDateBinding;

import androidx.recyclerview.widget.RecyclerView;

public class DateViewHolder extends RecyclerView.ViewHolder {

    private final FormItemDateBinding mBinding;

    public DateViewHolder(FormItemDateBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
    }

    protected FormItemDateBinding getBinding(){
        return mBinding;
    }
}