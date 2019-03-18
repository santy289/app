package com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.ItemAutocompletePlacesBinding;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.MainTextMatchedSubstring;
import com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete.Prediction;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationActivityInterface;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 04/04/18.
 */

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsViewHolder> {

    private GeolocationActivityInterface mActivityInterface;
    private List<Prediction> mDataset;
    private Context context;

    public SuggestionsAdapter(GeolocationActivityInterface activityInterface, List<Prediction> predictions) {
        this.mActivityInterface = activityInterface;
        this.mDataset = predictions;
    }

    public void setData(List<Prediction> predictions) {
        mDataset = predictions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        ItemAutocompletePlacesBinding itemBinding =
                ItemAutocompletePlacesBinding.inflate(layoutInflater, viewGroup, false);
        return new SuggestionsViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsViewHolder holder, int position) {
        Prediction item = getItem(position);

        SpannableString spannableString = new SpannableString(item.getStructuredFormatting().getMainText());
        for (MainTextMatchedSubstring matchedSubstring : item.getStructuredFormatting().getMainTextMatchedSubstrings()) {
            addBoldSpan(
                    spannableString, matchedSubstring.getLength(),
                    matchedSubstring.getOffset()
            );
        }

        holder.getBinding().tvTitle.setText(spannableString);
        holder.getBinding().tvSubtitle.setText(item.getStructuredFormatting().getSecondaryText());

        holder.getBinding().getRoot().setOnClickListener(
                v -> mActivityInterface.selectSuggestion(item));
    }

    private void addBoldSpan(SpannableString spannableString, int length, int offset) {
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), offset, length, 0);
    }

    public Prediction getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
