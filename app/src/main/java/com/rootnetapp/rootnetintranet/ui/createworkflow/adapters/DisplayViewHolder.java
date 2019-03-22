package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemDisplayBinding;

import androidx.recyclerview.widget.RecyclerView;

class DisplayViewHolder extends RecyclerView.ViewHolder {

    private final FormItemDisplayBinding mBinding;
    private final RecyclerView.LayoutParams mRootParams;

    public DisplayViewHolder(FormItemDisplayBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemDisplayBinding getBinding() {
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
