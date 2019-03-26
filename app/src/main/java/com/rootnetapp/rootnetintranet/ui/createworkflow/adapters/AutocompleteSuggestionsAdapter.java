package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.FormAutocompleteSuggestionItemBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 04/04/18.
 */

public class AutocompleteSuggestionsAdapter extends
        RecyclerView.Adapter<AutocompleteSuggestionsViewHolder> {

    private List<Option> mDataset;
    private OnSuggestionSelectedListener onSuggestionSelectedListener;

    public AutocompleteSuggestionsAdapter(List<Option> predictions,
                                          OnSuggestionSelectedListener onSuggestionSelectedListener) {
        this.mDataset = predictions;
        this.onSuggestionSelectedListener = onSuggestionSelectedListener;
    }

    public void setData(List<Option> data) {
        mDataset = data;
        notifyDataSetChanged();
        getItemCount();
    }

    @NonNull
    @Override
    public AutocompleteSuggestionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                                int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        FormAutocompleteSuggestionItemBinding itemBinding =
                FormAutocompleteSuggestionItemBinding.inflate(layoutInflater, viewGroup, false);
        return new AutocompleteSuggestionsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteSuggestionsViewHolder holder, int position) {
        Option item = getItem(position);

        holder.getBinding().tvTitle.setText(item.getName());

        holder.getBinding().tvTitle.setOnClickListener(v -> {
            if (onSuggestionSelectedListener != null) {
                onSuggestionSelectedListener.onSuggestionSelected(item);
            }
        });
    }

    public Option getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnSuggestionSelectedListener {

        void onSuggestionSelected(Option option);
    }
}
