package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.annotation.NonNull;

import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.CustomFieldUserSection;

public interface ViewTextUpdate {
    void onItemUpdate(@NonNull final CustomFieldUserSection section, final int itemAdapterPosition, final String newValue);
}
