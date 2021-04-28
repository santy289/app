package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import androidx.recyclerview.widget.RecyclerView;
import com.rootnetapp.rootnetintranet.databinding.SignatureSignerItemBinding;

public class SignersViewHolder extends RecyclerView.ViewHolder {

    final SignatureSignerItemBinding binding;

    public SignersViewHolder(SignatureSignerItemBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
