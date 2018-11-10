package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.StepsItemBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;

import java.util.List;


/**
 * Created by root on 02/04/18.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsViewholder>{

    private List<Step> steps;
    private Context context;

    public StepsAdapter(List<Step> steps) {
        this.steps = steps;
    }

    @Override
    public StepsViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        StepsItemBinding itemBinding =
                StepsItemBinding.inflate(layoutInflater, viewGroup, false);
        this.context = viewGroup.getContext();
        return new StepsViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(StepsViewholder holder, int i) {
        Step step = steps.get(i);
        holder.binding.tvSteptitle.setText(step.getTitle());
        holder.binding.tvStepcontent.setText(step.getText());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}
