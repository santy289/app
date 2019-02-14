package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemDoubleMultipleChoiceBinding;

import androidx.recyclerview.widget.RecyclerView;

class DoubleMultipleChoiceViewHolder extends RecyclerView.ViewHolder {

    private final FormItemDoubleMultipleChoiceBinding mBinding;
    private final RecyclerView.LayoutParams mRootParams;

    public DoubleMultipleChoiceViewHolder(FormItemDoubleMultipleChoiceBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemDoubleMultipleChoiceBinding getBinding() {
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