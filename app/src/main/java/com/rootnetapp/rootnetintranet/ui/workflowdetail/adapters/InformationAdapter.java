package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.InformationItemBinding;

import java.util.List;

public class InformationAdapter extends RecyclerView.Adapter<InformationViewholder>{

    private List<Information> contents;

    public InformationAdapter(List<Information> contents) {
        this.contents = contents;
    }

    @NonNull
    @Override
    public InformationViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        InformationItemBinding itemBinding =
                InformationItemBinding.inflate(layoutInflater, viewGroup, false);
        return new InformationViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationViewholder holder, int i) {
        Information item = contents.get(i);

        if (TextUtils.isEmpty(item.getTitle())) {
            Context context = holder.binding.tvTitle.getContext();
            holder.binding.tvTitle.setText(context.getString(item.getResTitle()));
        } else {
            holder.binding.tvTitle.setText(item.getTitle());
        }

//        if (item.isMultiple()) {
//            return;
//        }
        if (!TextUtils.isEmpty(item.getDisplayValue())) {
            holder.binding.tvContent.setText(item.getDisplayValue());
            return;
        }

        if (item.getResDisplayValue() < 1) {
            holder.binding.tvContent.setText("");
            return;
        }

        Context context = holder.binding.tvContent.getContext();
        holder.binding.tvContent.setText(context.getString(item.getResDisplayValue()));
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

}
