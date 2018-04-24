package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DocumentsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.workflows.Preset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 04/04/18.
 */

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsViewholder>{

    public List<Preset> totalDocuments;
    List<DocumentsFile> files;
    private Context context;
    public List<Boolean> isSelected;

    public DocumentsAdapter(List<Preset> totalDocuments, List<DocumentsFile> files) {
        this.totalDocuments = totalDocuments;
        this.files = files;
        this.isSelected = new ArrayList<>();
        for (Preset item : totalDocuments) {
            this.isSelected.add(false);
        }
    }

    @Override
    public DocumentsViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        DocumentsItemBinding itemBinding =
                DocumentsItemBinding.inflate(layoutInflater, viewGroup, false);
        return new DocumentsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(DocumentsViewholder holder, int i) {
        Preset item = totalDocuments.get(i);
        DocumentsFile file = null;
        for (DocumentsFile aux : files) {
            if(item.getId() == aux.getPresetId()){
                file = aux;
                break;
            }
        }
        holder.binding.tvName.setText(item.getName());
        holder.binding.chbxItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isSelected.set(i, b);
            }
        });
        if(file!=null){
            holder.binding.imgUploaded.setImageResource(R.drawable.ic_check_black_24dp);
            holder.binding.imgUploaded.setColorFilter(ContextCompat.getColor(context, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            holder.binding.tvDescription.setText(file.getName());
            String date = file.getCreatedAt().split(" ")[0];
            String hour = file.getCreatedAt().split(" ")[1];
            holder.binding.tvDate.setText(date + " - " + hour);
        }else{
            holder.binding.imgUploaded.setImageResource(R.drawable.ic_close_black_24dp);
            holder.binding.imgUploaded.setColorFilter(ContextCompat.getColor(context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if(i %2 ==0){
            holder.binding.lytHeader.setBackgroundColor(context.getResources().getColor(R.color.workflowListBg));
        }
        holder.binding.lytHeader.setOnClickListener(view -> {
            if (holder.binding.lytDetails.getVisibility() == View.GONE) {
                holder.binding.imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                holder.binding.imgArrow.setColorFilter(ContextCompat.getColor(context, R.color.arrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetails.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                holder.binding.imgArrow.setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetails.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return totalDocuments.size();
    }
}
