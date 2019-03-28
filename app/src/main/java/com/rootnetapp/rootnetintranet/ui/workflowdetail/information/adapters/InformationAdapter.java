package com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.InformationItemBinding;
import com.rootnetapp.rootnetintranet.databinding.InformationItemGeolocationBinding;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.InformationFragmentInterface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.InformationAdapter.ViewType.GEOLOCATION;
import static com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.InformationAdapter.ViewType.NORMAL;

public class InformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Information> contents;
    private InformationFragmentInterface mFragmentInterface;

    public InformationAdapter(InformationFragmentInterface fragmentInterface, List<Information> contents) {
        this.contents = contents;
        mFragmentInterface = fragmentInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {

            case ViewType.NORMAL:
                return new InformationViewholder(InformationItemBinding
                        .inflate(layoutInflater, viewGroup, false));

            case ViewType.GEOLOCATION:
                return new InformationGeolocationViewholder(InformationItemGeolocationBinding
                        .inflate(layoutInflater, viewGroup, false));

            default:
                throw new IllegalStateException("Invalid ViewType");
        }

    }

    @Override
    public @ViewType
    int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemCount() < 1) {
            return;
        }

        switch (getItemViewType(position)) {
            case ViewType.NORMAL:
                populateNormalItem((InformationViewholder) holder, position);
                break;

            case ViewType.GEOLOCATION:
                populateGeolocationItem((InformationGeolocationViewholder) holder, position);
                break;

            default:
                throw new IllegalStateException("Invalid ViewType");
        }

    }

    private void populateNormalItem(InformationViewholder holder, int position) {
        Information item = getItem(position);

        if (TextUtils.isEmpty(item.getTitle())) {
            Context context = holder.binding.tvTitle.getContext();
            holder.binding.tvTitle.setText(context.getString(item.getResTitle()));
        } else {
            holder.binding.tvTitle.setText(item.getTitle());
        }

        if (!TextUtils.isEmpty(item.getDisplayValue())) {
            holder.binding.tvContent.setText(item.getDisplayValue());

        } else if (item.getResDisplayValue() < 1) {
            holder.binding.tvContent.setText("");

        } else {
            Context context = holder.binding.tvContent.getContext();
            holder.binding.tvContent.setText(context.getString(item.getResDisplayValue()));
        }

        hideItemIfEmpty(holder);
    }

    private void populateGeolocationItem(InformationGeolocationViewholder holder, int position) {
        Information item = getItem(position);

        if (TextUtils.isEmpty(item.getTitle())) {
            Context context = holder.binding.tvTitle.getContext();
            holder.binding.tvTitle.setText(context.getString(item.getResTitle()));
        } else {
            holder.binding.tvTitle.setText(item.getTitle());
        }

        if (!TextUtils.isEmpty(item.getDisplayValue())) {
            holder.binding.chip.setText(item.getDisplayValue());

        } else if (item.getResDisplayValue() < 1) {
            holder.binding.chip.setText("");

        } else {
            Context context = holder.binding.chip.getContext();
            holder.binding.chip.setText(context.getString(item.getResDisplayValue()));
        }

        holder.binding.chip.setOnClickListener(
                v -> mFragmentInterface.showLocation(item.getSelectedLocation()));

        hideItemIfEmpty(holder);
    }

    private void hideItemIfEmpty(InformationViewholder holder) {
        //hide item if there is no description (content)
        boolean isVisible = holder.binding.tvContent.getText().length() > 0;
        holder.binding.tvTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.binding.tvContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void hideItemIfEmpty(InformationGeolocationViewholder holder) {
        //hide item if there is no description (content)
        boolean isVisible = holder.binding.chip.getText().length() > 0;
        holder.binding.tvTitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.binding.chip.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private Information getItem(int position){
        return contents.get(position);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NORMAL,
            GEOLOCATION
    })
    public @interface ViewType {

        int NORMAL = 0;
        int GEOLOCATION = 1;
    }

}
