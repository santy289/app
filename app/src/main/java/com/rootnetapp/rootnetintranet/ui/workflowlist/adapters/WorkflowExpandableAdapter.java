package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;

import java.util.ArrayList;
import java.util.List;

public class WorkflowExpandableAdapter extends ListAdapter<WorkflowListItem, WorkflowViewholder> {

    private List<Boolean> isChecked;
    private List<Boolean> isExpanded;
    private WorkflowFragmentInterface anInterface;
    private boolean firstLoad;

    public WorkflowExpandableAdapter(WorkflowFragmentInterface anInterface) {
        super(WorkflowExpandableAdapter.DIFF_CALLBACK);
        this.anInterface = anInterface;
        this.firstLoad = true;
    }

    private static final DiffUtil.ItemCallback<WorkflowListItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkflowListItem>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WorkflowListItem oldUser, @NonNull WorkflowListItem newUser) {
                    return oldUser.getWorkflowId() == newUser.getWorkflowId();
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull WorkflowListItem oldUser, @NonNull WorkflowListItem newUser) {
                    return oldUser.equals(newUser);
                }
            };

    @Override
    public WorkflowViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        WorkflowItemBinding itemBinding =
                WorkflowItemBinding.inflate(layoutInflater, viewGroup, false);
        return new WorkflowViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(WorkflowViewholder holder, int i) {
        if (getItemCount() < 1) {
            //TODO handle empty list
            return;
        }

        /*
        MAIN TITLE = WorkflowDB.title + WorkflowDB.workflowTypeKey
        typ of workflow / tipo de workflow = getWorkflowType().getName() / WorkflowTypeDb.name
        Owner Dueño =  WorkflowDB.author.fullName
        Status/ Estatus = Active Inactive  WorkflowDB.status (true/false)
        Actual state = Estado actual ? WorkflowDb.currentStatusName ("levantamiento")
        creado en /created on = WorflowDb.createdAt
        actualizado en = WorkflowDb.updatedAt


        item.getAuthor().getPicture();

         */

        WorkflowListItem item = getItem(i);
        String mainTitle = item.getTitle() + " - " + item.getWorkflowTypeKey();
        holder.binding.tvTitle.setText(mainTitle);

        // TODO need this
        //holder.binding.tvWorkflowtype.setText(item.getWorkflowType().getName());


        String fullName = item.getFullName();
        if (!TextUtils.isEmpty(fullName)) {
            holder.binding.tvOwner.setText(fullName);
        }

            // TODO put this back for now no author picture, we need to take the workflowId and
            // TODO find it in the table of profiles(future version) or users (currently called this way)
            // TODO need this
            //String picture = item.getAuthor().getPicture(); //TODO not able to get picture just yet, we need profile table
// TODO need this
//            if (!TextUtils.isEmpty(picture)) {
//                String path = Utils.imgDomain + picture.trim();
//                Glide.with(context).load(path).into(holder.binding.imgProfile);
//            }


        if(item.getCurrentStatusName() != null){
            holder.binding.tvActualstate.setText(item.getCurrentStatusName());
        }

        Context context = holder.binding.tvStatus.getContext();
        //todo fin de solo testing!
        if (item.isStatus()) {
            holder.binding.tvStatus.setText(context.getString(R.string.active));
        } else {
            holder.binding.tvStatus.setText(context.getString(R.string.inactive));
        }

        String date = item.getStart().split("T")[0];
        String hour = (item.getStart().split("T")[1]).split("-")[0];
        String dateText = date + " - " + hour;
        holder.binding.tvCreatedat.setText(dateText);
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
                // TODO put back because interface was removed
                //anInterface.showDetail(item);
            }
        });

        holder.binding.executePendingBindings();
    }

    private void redrawCheckbox(WorkflowViewholder holder, int i) {
        holder.binding.chbxSelected.setChecked(false);
        holder.binding.chbxSelected.setChecked(isChecked.get(i));
    }

    private void redrawExpansion(WorkflowViewholder holder, int i) {
        Context context = holder.binding.btnArrow.getContext();
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
    public void submitList(@Nullable List<WorkflowListItem> list) {
        super.submitList(list);
        setWorkflows(list);
    }

    public void setWorkflows(List<WorkflowListItem> workflows) {
        if (!firstLoad) {
            return;
        }
        // First time loading items
        resetChecksAndExpands();
        firstLoad = false;
    }

    private void resetChecksAndExpands() {
        isChecked = new ArrayList<>();
        isExpanded = new ArrayList<>();
        int i = 0;
        while (i < getItemCount()) {
            isChecked.add(false);
            isExpanded.add(false);
            ++i;
        }
    }

}
