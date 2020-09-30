package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import androidx.recyclerview.widget.RecyclerView;
import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldItemBinding;

public class SignatureCustomFieldsViewHolder extends RecyclerView.ViewHolder {

    final SignatureCustomFieldItemBinding binding;

    public SignatureCustomFieldsViewHolder(SignatureCustomFieldItemBinding itemView) {
        super(itemView.getRoot());
        this.binding = itemView;
    }
}
