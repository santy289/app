package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.WorkflowManagerItemBinding;
import com.rootnetapp.rootnetintranet.ui.manager.ManagerInterface;

import java.util.List;

/**
 * Created by root on 18/04/18.
 */

public class PendingWorkflowsAdapter  extends RecyclerView.Adapter<PendingWorkflowsViewholder>{

    private List<Workflow> workflows;
    private Context context;
    private ManagerInterface anInterface;

    public PendingWorkflowsAdapter(List<Workflow> workflows, ManagerInterface anInterface) {
        this.workflows = workflows;
        this.anInterface = anInterface;
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

        Workflow item = workflows.get(i);
        holder.binding.tvHeadername.setText(item.getWorkflowTypeKey());
        holder.binding.tvHeaderowner.setText(item.getAuthor().getFullName());

        String date = item.getStart().split("T")[0];
        String hour = (item.getStart().split("T")[1]).split("-")[0];
        holder.binding.tvDate.setText(date + " - " + hour);
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvAuthor.setText(item.getAuthor().getFullName());

        /*if(i%2==0){
            holder.binding.tvHeaderdate.setTextColor(ContextCompat.getColor(context, R.color.red));
        }*/

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
