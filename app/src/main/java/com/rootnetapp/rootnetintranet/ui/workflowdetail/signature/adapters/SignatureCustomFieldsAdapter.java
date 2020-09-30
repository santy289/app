package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;

import java.util.List;

public class SignatureCustomFieldsAdapter extends RecyclerView.Adapter<SignatureCustomFieldsViewHolder> {

    List<FieldCustom> customFields;

    public SignatureCustomFieldsAdapter(List<FieldCustom> customFields) {
        this.customFields = customFields;
    }

    @NonNull
    @Override
    public SignatureCustomFieldsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        SignatureCustomFieldItemBinding itemBinding =
                SignatureCustomFieldItemBinding.inflate(layoutInflater, parent, false);
        return new SignatureCustomFieldsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SignatureCustomFieldsViewHolder holder, int position) {
        if (getItemCount() < 1) {
            return;
        }

        FieldCustom field = getItem(position);
        holder.binding.customFieldTextLayout.setHint(field.getDisplayName());
        if (holder.binding.customFieldTextLayout.getEditText() != null) {
            holder.binding.customFieldTextLayout.getEditText().setText(field.getCustomValue());
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
