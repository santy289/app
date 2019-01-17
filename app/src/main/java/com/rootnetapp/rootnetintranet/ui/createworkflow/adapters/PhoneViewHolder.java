package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemPhoneBinding;

import androidx.recyclerview.widget.RecyclerView;

class PhoneViewHolder extends RecyclerView.ViewHolder {

    private final FormItemPhoneBinding mBinding;
    private final RecyclerView.LayoutParams mRootParams;

    public PhoneViewHolder(FormItemPhoneBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemPhoneBinding getBinding(){
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