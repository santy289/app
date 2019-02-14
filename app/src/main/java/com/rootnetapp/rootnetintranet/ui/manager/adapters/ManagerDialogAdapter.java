package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ManagerDialogItemBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 20/04/18.
 */

public class ManagerDialogAdapter extends RecyclerView.Adapter<ManagerDialogViewholder> {

    private List<WorkflowDb> workflows;
    private ManagerInterface anInterface;

    public ManagerDialogAdapter(@NonNull List<WorkflowDb> workflows, ManagerInterface anInterface) {
        this.workflows = workflows;
        this.anInterface = anInterface;
    }

    public void setData(List<WorkflowDb> list) {
        workflows = list;
        notifyDataSetChanged();
        getItemCount();
    }

    @NonNull
    @Override
    public ManagerDialogViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ManagerDialogItemBinding itemBinding =
                ManagerDialogItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ManagerDialogViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerDialogViewholder holder, int i) {
        WorkflowDb item = workflows.get(i);
        holder.binding.tvWorkflowTypeKey.setText(item.getWorkflowTypeKey());
        holder.binding.tvWorkflowTitle.setText(item.getTitle());

        holder.binding.lytHeader.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            WorkflowDb selectedItem = getItem(position);
            anInterface.showWorkflow(new WorkflowListItem(selectedItem));
        });
    }

    private WorkflowDb getItem(int position) {
        return workflows.get(position);
    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }
}
