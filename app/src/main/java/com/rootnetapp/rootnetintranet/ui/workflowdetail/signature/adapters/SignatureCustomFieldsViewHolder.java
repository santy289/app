package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.recyclerview.widget.RecyclerView;
import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;

import java.util.List;

public class SignatureCustomFieldsViewHolder extends RecyclerView.ViewHolder {

    final SignatureCustomFieldItemBinding binding;
    List<FieldCustom> cachedFields;

    public SignatureCustomFieldsViewHolder(SignatureCustomFieldItemBinding itemView, List<FieldCustom> cachedFields) {
        super(itemView.getRoot());
        this.binding = itemView;
        if (this.binding.customFieldTextLayout.getEditText() == null) {
            return;
        }
        this.binding.customFieldTextLayout.getEditText().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(binding.customFieldTextLayout.getEditText().getTag()!=null){
                    FieldCustom fieldCustom = cachedFields.get(getAdapterPosition());
                    fieldCustom.setCustomValue(charSequence.toString());
                    cachedFields.set(getAdapterPosition(), fieldCustom);
                }
            }
        });
    }
}
