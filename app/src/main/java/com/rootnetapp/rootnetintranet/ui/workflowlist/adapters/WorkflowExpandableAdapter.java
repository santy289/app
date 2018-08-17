package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;

import java.util.ArrayList;
import java.util.List;

public class WorkflowExpandableAdapter extends RecyclerView.Adapter<WorkflowViewholder> {

    private List<Workflow> workflows;
    private List<Boolean> isChecked;
    private List<Boolean> isExpanded;
    private ViewGroup recycler;
    private WorkflowFragmentInterface anInterface;
    private Context context;

    public WorkflowExpandableAdapter(WorkflowFragmentInterface anInterface) {
        this.anInterface = anInterface;
    }

    @Override
    public WorkflowViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        WorkflowItemBinding itemBinding =
                WorkflowItemBinding.inflate(layoutInflater, viewGroup, false);
        recycler = viewGroup;
        return new WorkflowViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(WorkflowViewholder holder, int i) {
        if (workflows == null || workflows.size() < 1) {
            //TODO handle empty list
            return;
        }

        Workflow item = workflows.get(i);
        holder.binding.tvTitle.setText(item.getTitle() + " - " + item.getWorkflowTypeKey());
        holder.binding.tvWorkflowtype.setText(item.getWorkflowType().getName());
        //todo remover validaciones, solo testing!
        //Esto deberia venir siempre de la consulta al servicio.
        if (item.getAuthor() != null) {
            String fullName = item.getAuthor().getFullName();
            if (!TextUtils.isEmpty(fullName)) {
                holder.binding.tvOwner.setText(fullName);
            }
            String picture = item.getAuthor().getPicture();
            if (!TextUtils.isEmpty(picture)) {
                String path = Utils.imgDomain + picture.trim();
                Glide.with(context).load(path).into(holder.binding.imgProfile);
            }
        }
        if(item.getWorkflowStateInfo() != null){
            if(item.getWorkflowStateInfo().getVirtualColumns() != null){
                holder.binding.tvActualstate.setText(item.getWorkflowStateInfo().getVirtualColumns().getName());
            }
        }
        //todo fin de solo testing!
        if (item.isStatus()) {
            holder.binding.tvStatus.setText(recycler.getContext().getString(R.string.active));
        } else {
            holder.binding.tvStatus.setText(recycler.getContext().getString(R.string.inactive));
        }

        String date = item.getStart().split("T")[0];
        String hour = (item.getStart().split("T")[1]).split("-")[0];
        holder.binding.tvCreatedat.setText(date + " - " + hour);
        //todo updated!
        holder.binding.chbxSelected.setOnCheckedChangeListener(null);
        redrawCheckbox(holder, i);
        redrawExpansion(holder, i);
        holder.binding.btnArrow.setOnClickListener(view -> {
            isExpanded.set(i, !isExpanded.get(i));
            redrawExpansion(holder, i);
            //todo con doble click la app crashea por la transicion
            //TransitionManager.beginDelayedTransition(recycler);
        });
        holder.binding.chbxSelected.setOnCheckedChangeListener((compoundButton, b) ->
                isChecked.set(i, b));

        holder.binding.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anInterface.showDetail(item);
            }
        });

    }

    private void redrawCheckbox(WorkflowViewholder holder, int i) {
        holder.binding.chbxSelected.setChecked(false);
        holder.binding.chbxSelected.setChecked(isChecked.get(i));
    }

    private void redrawExpansion(WorkflowViewholder holder, int i) {
        if (isExpanded.get(i)) {
            holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.arrow),
                    android.graphics.PorterDuff.Mode.SRC_IN);
                    holder.binding.layoutDetails.setVisibility(View.VISIBLE);
        } else {
            holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            holder.binding.layoutDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (workflows == null) {
            return 0;
        }
        return workflows.size();
    }

    public void setWorkflows(List<Workflow> workflows) {
        boolean firstLoad = false;
        if (this.workflows == null) {
            firstLoad = true;
        }
        this.workflows = workflows;
        if (firstLoad){
            resetChecksAndExpands();
        }
        notifyDataSetChanged();
    }

    private void resetChecksAndExpands() {
        isChecked = new ArrayList<>();
        isExpanded = new ArrayList<>();
        for (Workflow item : workflows) {
            isChecked.add(false);
            isExpanded.add(false);
        }
    }

}
