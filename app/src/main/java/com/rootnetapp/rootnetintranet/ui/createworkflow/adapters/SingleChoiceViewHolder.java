package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemSingleChoiceBinding;

import androidx.recyclerview.widget.RecyclerView;

public class SingleChoiceViewHolder extends RecyclerView.ViewHolder {

    private final FormItemSingleChoiceBinding mBinding;
    private RecyclerView.LayoutParams mRootParams;

    public SingleChoiceViewHolder(FormItemSingleChoiceBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemSingleChoiceBinding getBinding() {
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