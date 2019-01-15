package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormItemFileBinding;

import androidx.recyclerview.widget.RecyclerView;

public class FileViewHolder extends RecyclerView.ViewHolder {

    private final FormItemFileBinding mBinding;
    private RecyclerView.LayoutParams mRootParams;

    public FileViewHolder(FormItemFileBinding binding) {
        super(binding.getRoot());

        mBinding = binding;

        mRootParams = (RecyclerView.LayoutParams) mBinding.root.getLayoutParams();
    }

    protected FormItemFileBinding getBinding() {
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