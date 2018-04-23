package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.WorkflowManagerItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 18/04/18.
 */

public class PendingWorkflowsAdapter  extends RecyclerView.Adapter<PendingWorkflowsViewholder>{

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> workflows;
    private Context context;

    public PendingWorkflowsAdapter() {
        workflows = new ArrayList<>();
        int i=0;
        while(i<4){
            workflows.add(1);
            i++;
        }
    }

    @Override
    public PendingWorkflowsViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        this.context = viewGroup.getContext();
        WorkflowManagerItemBinding itemBinding =
                WorkflowManagerItemBinding.inflate(layoutInflater, viewGroup, false);
        return new PendingWorkflowsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(PendingWorkflowsViewholder holder, int i) {

        if(i%2==0){
            holder.binding.tvHeaderdate.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.binding.lytHeader.setOnClickListener(view -> {
            if (holder.binding.lytDetail.getVisibility() == View.GONE) {
                holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.arrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unselected_filter));
                holder.binding.lytDetail.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.binding.lytDetail.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }
}
