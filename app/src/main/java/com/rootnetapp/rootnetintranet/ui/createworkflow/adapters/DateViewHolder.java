package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemDateBinding;

import androidx.recyclerview.widget.RecyclerView;

class DateViewHolder extends RecyclerView.ViewHolder {

    private final FormItemDateBinding mBinding;
    private final RecyclerView.LayoutParams mRootParams;

    public DateViewHolder(FormItemDateBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemDateBinding getBinding(){
        return mBinding;
    }

    protected void show() {
        mRootParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mBinding.root.setLayoutParams(mRootParams);
    }

    protected void hide() {
        mRootParams.height = 0;
        mBinding.root.setLayoutParams(mRootParams);
    }
}