package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemMultipleChoiceBinding;

import androidx.recyclerview.widget.RecyclerView;

public class MultipleChoiceViewHolder extends RecyclerView.ViewHolder {

    private final FormItemMultipleChoiceBinding mBinding;
    private RecyclerView.LayoutParams mRootParams;

    public MultipleChoiceViewHolder(FormItemMultipleChoiceBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemMultipleChoiceBinding getBinding() {
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