package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldFormState;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.ViewTextUpdate;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class CustomFieldUserSection extends Section {

    SignatureCustomFieldFormState state;
    ViewTextUpdate view;

    public CustomFieldUserSection(SignatureCustomFieldFormState state, ViewTextUpdate view) {
        super(SectionParameters.builder()
        .itemResourceId(R.layout.signature_custom_field_item)
        .headerResourceId(R.layout.signature_custom_header_item)
        .build());
        this.state = state;
        this.view = view;
    }

    @Override
    public int getContentItemsTotal() {
        return state.getFieldCustomList().size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new SignatureCustomNormalViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new SignatureCustomHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder vh) {
        SignatureCustomHeaderViewHolder holder = (SignatureCustomHeaderViewHolder) vh;
        holder.headerTitle.setText(state.getTitle());
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder vh, int position) {
        SignatureCustomNormalViewHolder holder = (SignatureCustomNormalViewHolder) vh;

        if (getContentItemsTotal() < 1) {
            return;
        }

        Context context = holder.itemView.getContext();

        FieldCustom field = getItem(position);
        holder.customFieldTextLayout.setHint(field.getDisplayName());
        if (holder.customFieldTextLayout.getEditText() != null) {
            holder.customFieldTextLayout.getEditText().setText(field.getCustomValue());
        }

        String helperText;
        switch (field.getSubtitle()) {
            case "sub_signature_name":
                helperText = context.getString(R.string.sub_signature_name);
                break;
            case "sub_signature_phone":
                helperText = context.getString(R.string.sub_signature_phone);
                break;
            case "sub_signature_email":
                helperText = context.getString(R.string.sub_signature_email);
                break;
            case "sub_signature_identifier_type":
                helperText = context.getString(R.string.sub_signature_identifier_type);
                break;
            case "sub_signature_identifier":
                helperText = context.getString(R.string.sub_signature_identifier);
                break;
            case "sub_signature_subject":
                helperText = context.getString(R.string.sub_signature_subject);
                break;
            default:
                helperText = "";
                break;
        }
        holder.customFieldTextLayout.setHelperText(helperText);

        if (!field.isValid()) {
            holder.customFieldTextLayout.setError(context.getString(R.string.required_fields));
        } else {
            holder.customFieldTextLayout.setError(null);
        }

        holder.customFieldTextLayout.getEditText().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int position = holder.getAdapterPosition();
                view.onItemUpdate(CustomFieldUserSection.this, position, charSequence.toString());
            }
        });
    }

    public FieldCustom getItem(int position) {
        if (position >= state.getFieldCustomList().size()) {
            return null;
        }
        return state.getFieldCustomList().get(position);
    }
}
