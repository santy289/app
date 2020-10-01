package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;

import java.util.List;

public class SignatureCustomFieldsAdapter extends RecyclerView.Adapter<SignatureCustomFieldsViewHolder> {

    List<FieldCustom> customFields;

    public SignatureCustomFieldsAdapter(List<FieldCustom> customFields) {
        this.customFields = customFields;
    }

    public void updateFields(List<FieldCustom> fields) {
        this.customFields = fields;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SignatureCustomFieldsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        SignatureCustomFieldItemBinding itemBinding =
                SignatureCustomFieldItemBinding.inflate(layoutInflater, parent, false);
        return new SignatureCustomFieldsViewHolder(itemBinding, customFields);
    }

    @Override
    public void onBindViewHolder(@NonNull SignatureCustomFieldsViewHolder holder, int position) {
        if (getItemCount() < 1) {
            return;
        }

        Context context = holder.itemView.getContext();
        FieldCustom field = getItem(position);
        holder.binding.customFieldTextLayout.setHint(field.getDisplayName());
        if (holder.binding.customFieldTextLayout.getEditText() != null) {
            holder.binding.customFieldTextLayout.getEditText().setText(field.getCustomValue());
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
        holder.binding.customFieldTextLayout.setHelperText(helperText);

        if (!field.isValid()) {
            holder.binding.customFieldTextLayout.setError(context.getString(R.string.required_fields));
        } else {
            holder.binding.customFieldTextLayout.setError(null);
        }

    }

    @Override
    public int getItemCount() {
        return customFields.size();
    }

    private FieldCustom getItem(int position) {
        return customFields.get(position);
    }
}
