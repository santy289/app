package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowManagerItemBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

import java.util.List;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by root on 18/04/18.
 */

public class PendingWorkflowsAdapter  extends RecyclerView.Adapter<PendingWorkflowsViewholder>{

    private List<WorkflowDb> workflows;
    private Context context;
    private ManagerInterface anInterface;

    public PendingWorkflowsAdapter(List<WorkflowDb> workflows, ManagerInterface anInterface) {
        this.workflows = workflows;
        this.anInterface = anInterface;
    }

    public void setData(List<WorkflowDb> list){
        this.workflows = list;
        notifyDataSetChanged();
        getItemCount();
    }

    public void addData(List<WorkflowDb> list){
        int positionStart = this.workflows.size();

        this.workflows.addAll(list);

        int positionEnd = this.workflows.size() - 1;

        notifyItemRangeInserted(positionStart, positionEnd);
        getItemCount();
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
    public void onBindViewHolder(@NonNull PendingWorkflowsViewholder holder, int i) {

        WorkflowDb item = getItem(i);
        holder.binding.tvHeadername.setText(item.getWorkflowTypeKey());
        holder.binding.tvHeaderTitle.setText(item.getTitle());

        // Clock
        @ColorRes
        int color = item.getRemainingTime() <= 0 ? R.color.red : R.color.green;
        holder.binding.ivClock.setColorFilter(ContextCompat.getColor(
                holder.binding.ivClock.getContext(),
                color
        ));

        holder.binding.tvWorkflowType.setText(item.getWorkflowType().getName());

        String startDate = item.getStart();
        String formattedDate = Utils.getFormattedDate(
                startDate,
                Utils.SERVER_DATE_FORMAT,
                Utils.STANDARD_DATE_DISPLAY_FORMAT
        );
        holder.binding.tvDate.setText(formattedDate);
        holder.binding.tvAuthor.setText(item.getAuthor().getFullName());
        holder.binding.tvCurrentStatus.setText(item.getCurrentStatusName());

        holder.binding.lytHeader.setOnClickListener(view -> {
            if (holder.binding.lytDetail.getVisibility() == View.GONE) {
                holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.arrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetail.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                holder.binding.lytDetail.setVisibility(View.GONE);
            }
        });

        holder.binding.btnDetail.setOnClickListener(view -> {
            int position = holder.getAdapterPosition();
            WorkflowDb selectedItem = getItem(position);
            anInterface.showWorkflow(new WorkflowListItem(selectedItem));
        });

    }

    private WorkflowDb getItem(int position){
        return workflows.get(position);
    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }
}
