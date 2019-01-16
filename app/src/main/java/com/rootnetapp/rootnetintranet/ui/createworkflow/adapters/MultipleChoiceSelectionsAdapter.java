package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormMultipleChoiceSelectionItemBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MultipleChoiceSelectionsAdapter extends
        RecyclerView.Adapter<MultipleChoiceSelectionsViewHolder> {

    private final List<Option> selectedValues;

    MultipleChoiceSelectionsAdapter(List<Option> selectedValues) {
        this.selectedValues = selectedValues;
    }

    /**
     * Adds an item to the adapter's dataset while notifying for the animation changes. Also, checks
     * for dupes before adding the item.
     *
     * @param value item to add
     *
     * @return whether the item was added.
     */
    boolean addItem(Option value) {
        for (Option selectedValue : selectedValues) {
            if (value == selectedValue) return false;
        }

        selectedValues.add(value);
        notifyItemInserted(selectedValues.size() - 1);
        getItemCount();
        return true;
    }

    private void removeItem(Option value) {
        int position = selectedValues.indexOf(value);
        selectedValues.remove(value);
        notifyItemRemoved(position);
        getItemCount();
    }

    @NonNull
    @Override
    public MultipleChoiceSelectionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                 int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        FormMultipleChoiceSelectionItemBinding itemBinding =
                FormMultipleChoiceSelectionItemBinding.inflate(layoutInflater, viewGroup, false);
        return new MultipleChoiceSelectionsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleChoiceSelectionsViewHolder holder, int i) {
        Option item = getItem(i);

        holder.binding.chip.setText(item.getName());
        holder.binding.chip.setOnCloseIconClickListener(view -> {
            // Handle the click on the close icon.
//            onRemovedListener.onRemoved(item);
            removeItem(item);
        });
    }

    @Override
    public int getItemCount() {
        return selectedValues.size();
    }

    private Option getItem(int position) {
        return selectedValues.get(position);
    }
}
