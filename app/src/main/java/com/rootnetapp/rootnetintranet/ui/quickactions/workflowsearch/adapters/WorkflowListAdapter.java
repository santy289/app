package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowSearchItemBinding;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.WorkflowSearchFragmentInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

public class WorkflowListAdapter extends PagedListAdapter<WorkflowListItem, WorkflowListViewHolder> {
    private final WorkflowSearchFragmentInterface mInterface;

    public WorkflowListAdapter(WorkflowSearchFragmentInterface anInterface) {
        super(WorkflowListAdapter.DIFF_CALLBACK);
        this.mInterface = anInterface;
    }

    /**
     * This DiffUtil implementation is the responsible to check if we already have the upcoming
     * item in our data set or not. If it is the same the UI wont change but if we have new data
     * then it will update the recycler view with the appropriate animation and data.
     */
    private static final DiffUtil.ItemCallback<WorkflowListItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkflowListItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WorkflowListItem oldItem, @NonNull WorkflowListItem newItem) {
                    return oldItem.getWorkflowId() == newItem.getWorkflowId();
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull WorkflowListItem oldItem, @NonNull WorkflowListItem newItem) {
                    return oldItem.equals(newItem);
                }
            };

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
        int count = getItemCount();
        if (count < 1) {
            //TODO handle empty list
            return;
        }
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
        holder.binding.executePendingBindings();
    }

    @Override
    public void submitList(@Nullable PagedList<WorkflowListItem> pagedList) {
        super.submitList(pagedList);
    }
}
