package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowSearchItemBinding;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.WorkflowSearchFragmentInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkflowListAdapter extends RecyclerView.Adapter<WorkflowListViewHolder> {

    //todo remove this and replace with WorkflowExpandableAdapter OR add paging

    private List<WorkflowListItem> workflows;
    private WorkflowSearchFragmentInterface mInterface;

    public WorkflowListAdapter(WorkflowSearchFragmentInterface anInterface) {
        this.mInterface = anInterface;
        workflows = new ArrayList<>();
    }

    public void addData(List<WorkflowListItem> workflows) {
        int positionStart = this.workflows.size();

        this.workflows.addAll(workflows);

        int positionEnd = this.workflows.size() - 1;

        notifyItemRangeInserted(positionStart, positionEnd);
        getItemCount();
    }

    public void clearData(){
        this.workflows = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkflowListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        WorkflowSearchItemBinding itemBinding =
                WorkflowSearchItemBinding.inflate(layoutInflater, viewGroup, false);
        return new WorkflowListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkflowListViewHolder holder, int i) {
        WorkflowListItem item = getItem(i);

        holder.binding.getRoot().setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            WorkflowListItem selectedItem = getItem(position);
            mInterface.performAction(selectedItem);
        });

        String mainTitle = item.getTitle() + " - " + item.getWorkflowTypeKey();
        holder.binding.tvTitle.setText(mainTitle);

        holder.binding.tvType.setText(item.getWorkflowTypeName());

        Context context = holder.binding.tvStatus.getContext();
        if (item.isStatus()) {
            holder.binding.tvStatus.setText(context.getString(R.string.open));
        } else {
            holder.binding.tvStatus.setText(context.getString(R.string.closed));
        }
    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }

    private WorkflowListItem getItem(int index) {
        return workflows.get(index);
    }
}
