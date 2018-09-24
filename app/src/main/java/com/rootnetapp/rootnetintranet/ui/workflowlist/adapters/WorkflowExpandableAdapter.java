package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;
import com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowListBoundaryCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkflowExpandableAdapter extends PagedListAdapter<WorkflowListItem, WorkflowViewholder> {

    private List<Boolean> isChecked;
    private List<Boolean> isExpanded;

    private ArrayMap<Integer, Boolean> expandedItems;
    private ArrayMap<Integer, Boolean> checkedItems;

    private WorkflowFragmentInterface anInterface;
    private boolean firstLoad;

    public WorkflowExpandableAdapter(WorkflowFragmentInterface anInterface) {
        super(WorkflowExpandableAdapter.DIFF_CALLBACK);
        this.anInterface = anInterface;
        this.firstLoad = true;
        this.expandedItems = new ArrayMap<>();
        this.checkedItems = new ArrayMap<>();
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
        int count = getItemCount();
        if (count < 1) {
            //TODO handle empty list
            return;
        }

        WorkflowListItem item = getItem(i);

        // Clock
        @ColorRes
        int color = item.getRemainingTime() <= 0 ? R.color.red : R.color.green;
        holder.binding.clock.setColorFilter(ContextCompat.getColor(
                holder.binding.clock.getContext(),
                color
        ));

        // Title
        String mainTitle = item.getTitle() + " - " + item.getWorkflowTypeKey();
        holder.binding.tvTitle.setText(mainTitle);
        holder.binding.tvWorkflowtype.setText(item.getWorkflowTypeName());

        //Details
        String fullName = item.getFullName();
        if (!TextUtils.isEmpty(fullName)) {
            holder.binding.tvOwner.setText(fullName);
        }
        if(item.getCurrentStatusName() != null){
            holder.binding.tvActualstate.setText(item.getCurrentStatusName());
        }
        Context context = holder.binding.tvStatus.getContext();
        if (item.isStatus()) {
            holder.binding.tvStatus.setText(context.getString(R.string.active));
        } else {
            holder.binding.tvStatus.setText(context.getString(R.string.inactive));
        }

        String createdAt = item.getCreatedAtFormatted();
        String updatedAt = item.getUpdatedAtFormatted();
        holder.binding.tvCreatedat.setText(createdAt);
        holder.binding.tvUpdatedAt.setText(updatedAt);

        int workflowId = item.getWorkflowId();
        holder.binding.chbxSelected.setOnCheckedChangeListener(null);
        redrawCheckbox(holder, workflowId);
        redrawExpansion(holder, workflowId);
        holder.binding.btnArrow.setOnClickListener(view -> {
            int expandedItem = holder.getAdapterPosition();
            WorkflowListItem expandedWorkflow = getItem(expandedItem);
            if (expandedWorkflow != null) {

                int expandedItemId = expandedWorkflow.getWorkflowId();
                if (!expandedItems.containsKey(expandedItemId)) {
                    expandedItems.put(expandedItemId, true);
                } else {
                    Boolean selected = expandedItems.get(expandedItemId);
                    expandedItems.put(expandedItemId, !selected);
                }
                redrawExpansion(holder, expandedItemId);

            }
            //todo con doble click la app crashea por la transicion
            //TransitionManager.beginDelayedTransition(recycler);
        });
        holder.binding.chbxSelected.setOnCheckedChangeListener((compoundButton, b) -> {
            int positionChecked = holder.getAdapterPosition();
            WorkflowListItem checkedItem = getItem(positionChecked);
            if (checkedItem != null) {
                int checkedItemId = checkedItem.getWorkflowId();
                if(!checkedItems.containsKey(checkedItemId)) {
                    checkedItems.put(checkedItemId, true);
                } else {
                    checkedItems.put(checkedItemId, b);
                }
            }
        });

        holder.binding.btnDetail.setOnClickListener(view -> {
            // TODO put back because interface was removed
            int position = holder.getAdapterPosition();
            WorkflowListItem selectedItem = getItem(position);
            anInterface.showDetail(selectedItem);
        });

        holder.binding.executePendingBindings();
    }

    private void redrawCheckbox(WorkflowViewholder holder, int worflowId) {
        Boolean hasValue = checkedItems.containsKey(worflowId);
        if (!hasValue) {
            holder.binding.chbxSelected.setChecked(false);
            return;
        }
        Boolean isChecked = checkedItems.get(worflowId);
        holder.binding.chbxSelected.setChecked(isChecked);
    }

    private void redrawExpansion(WorkflowViewholder holder, int workflowId) {
        Context context = holder.binding.btnArrow.getContext();

        Boolean hasValue = expandedItems.containsKey(workflowId);
        if (!hasValue) {
            holder.binding.btnArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            holder.binding.btnArrow.setColorFilter(ContextCompat.getColor(context, R.color.transparentArrow),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            holder.binding.layoutDetails.setVisibility(View.GONE);
            return;
        }

        Boolean isSelected = expandedItems.get(workflowId);

        if (isSelected) {
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
    public void submitList(@Nullable PagedList<WorkflowListItem> list) {
        super.submitList(list);
    }

    public void setAllCheckboxes(boolean isCheck) {
        WorkflowListItem item;
        for (int i = 0; i < getItemCount(); i++) {
            item = getItem(i);
            if (item == null) {
                continue;
            }
            checkedItems.put(item.getWorkflowId(), isCheck);
        }
        notifyDataSetChanged();
    }
}
