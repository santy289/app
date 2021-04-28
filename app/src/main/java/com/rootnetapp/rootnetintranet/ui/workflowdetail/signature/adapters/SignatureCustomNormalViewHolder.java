package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.rootnetapp.rootnetintranet.R;

public class SignatureCustomNormalViewHolder extends RecyclerView.ViewHolder {

    public final TextInputLayout customFieldTextLayout;

    public SignatureCustomNormalViewHolder(@NonNull View itemView) {
        super(itemView);
        customFieldTextLayout = itemView.findViewById(R.id.custom_field_text_layout);
    }
}
