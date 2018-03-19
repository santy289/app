package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/03/18.
 */

public class WorkflowExpandableAdapter extends RecyclerView.Adapter<WorkflowViewholder> {

    private List<Workflow> workflows;
    private List<Boolean> isChecked;
    private List<Boolean> isExpanded;
    private ViewGroup recycler;

    public WorkflowExpandableAdapter(List<Workflow> workflows) {
        this.workflows = workflows;
        isChecked = new ArrayList<>();
        isExpanded = new ArrayList<>();
        for (Workflow item : workflows) {
            isChecked.add(false);
            isExpanded.add(false);
        }
    }

    @Override
    public WorkflowViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        WorkflowItemBinding itemBinding =
                WorkflowItemBinding.inflate(layoutInflater, viewGroup, false);
        recycler = viewGroup;
        return new WorkflowViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(WorkflowViewholder holder, int i) {
        Workflow item = workflows.get(i);
        holder.binding.tvTitle.setText(item.getTitle() + " - " + item.getWorkflowTypeKey());
        redrawCheckbox(holder, i);
        redrawExpansion(holder, i);
        holder.binding.btnArrow.setOnClickListener(view -> {
            isExpanded.set(i, !isExpanded.get(i));
            redrawExpansion(holder, i);
        });
        holder.binding.chbxSelected.setOnCheckedChangeListener((compoundButton, b) ->
                isChecked.set(i, b));
    }

    private void redrawCheckbox(WorkflowViewholder holder, int i) {
        holder.binding.chbxSelected.setSelected(false);
        holder.binding.chbxSelected.setSelected(isChecked.get(i));
    }

    private void redrawExpansion(WorkflowViewholder holder, int i) {
        if (isExpanded.get(i)) {
            holder.binding.layoutDetails.setVisibility(View.VISIBLE);
        } else {
            holder.binding.layoutDetails.setVisibility(View.GONE);
        }
        //TransitionManager.beginDelayedTransition(recycler);
    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }

}
