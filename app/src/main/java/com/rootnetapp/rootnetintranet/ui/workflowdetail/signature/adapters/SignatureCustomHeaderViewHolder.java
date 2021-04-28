package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.R;

public class SignatureCustomHeaderViewHolder extends RecyclerView.ViewHolder {

    public final TextView headerTitle;

    public SignatureCustomHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        headerTitle = (TextView) itemView.findViewById(R.id.signature_custom_section_header_title);
    }
}
