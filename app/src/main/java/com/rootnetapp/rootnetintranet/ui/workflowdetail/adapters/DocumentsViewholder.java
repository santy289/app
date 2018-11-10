package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.DocumentsItemBinding;

/**
 * Created by root on 04/04/18.
 */

public class DocumentsViewholder extends RecyclerView.ViewHolder {

    final DocumentsItemBinding binding;

    public DocumentsViewholder(DocumentsItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
