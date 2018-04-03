package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.icu.text.IDNA;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.InformationItemBinding;

import java.util.List;

/**
 * Created by root on 03/04/18.
 */

public class InformationAdapter extends RecyclerView.Adapter<InformationViewholder>{

    private List<Information> contents;

    public InformationAdapter(List<Information> contents) {
        this.contents = contents;
    }

    @Override
    public InformationViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        InformationItemBinding itemBinding =
                InformationItemBinding.inflate(layoutInflater, viewGroup, false);
        return new InformationViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(InformationViewholder holder, int i) {
        Information item = contents.get(i);
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

}
