package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ManagerDialogItemBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 20/04/18.
 */

public class ManagerDialogAdapter extends RecyclerView.Adapter<ManagerDialogViewholder> {

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> testWorkflows;
    private List<WorkflowDb> workflows;
    private ManagerInterface anInterface;
    private Context context;

    public ManagerDialogAdapter(List<WorkflowDb> workflows, ManagerInterface anInterface) {
        if (workflows != null) {
            this.workflows = workflows;
        } else {
            testWorkflows = new ArrayList<>();
            int i = 0;
            while (i < 8) {
                testWorkflows.add(1);
                i++;
            }
        }
    }

    @Override
    public ManagerDialogViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ManagerDialogItemBinding itemBinding =
                ManagerDialogItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ManagerDialogViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ManagerDialogViewholder holder, int i) {

        if (workflows != null) {
            WorkflowDb item = workflows.get(i);
            holder.binding.tvWorkflowid.setText(String.valueOf(item.getId()));
            holder.binding.tvActualstate.setText(item.getCurrentStatusName());
            holder.binding.tvWorkflowtype.setText(item.getWorkflowType().getName());
            holder.binding.lytHeader.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                WorkflowDb selectedItem = getItem(position);
                anInterface.showWorkflow(new WorkflowListItem(selectedItem));
            });
        }

    }

    private WorkflowDb getItem(int position){
        return workflows.get(position);
    }

    @Override
    public int getItemCount() {
        if (workflows != null) {
            return workflows.size();
        }
        return testWorkflows.size();
    }
}
