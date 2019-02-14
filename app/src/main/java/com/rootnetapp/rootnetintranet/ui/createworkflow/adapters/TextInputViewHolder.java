package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemTextInputBinding;

import androidx.recyclerview.widget.RecyclerView;

class TextInputViewHolder extends RecyclerView.ViewHolder {

    private final FormItemTextInputBinding mBinding;
    private final RecyclerView.LayoutParams mRootParams;

    public TextInputViewHolder(FormItemTextInputBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemTextInputBinding getBinding() {
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
