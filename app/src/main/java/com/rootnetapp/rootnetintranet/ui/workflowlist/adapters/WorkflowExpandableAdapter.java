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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.WorkflowItemBinding;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkflowExpandableAdapter extends PagedListAdapter<WorkflowListItem, WorkflowViewholder> {

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

        holder.binding.btnDetail.setOnClickListener(view -> {
            // TODO put back because interface was removed
            //anInterface.showDetail(item);
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
    public void submitList(@Nullable PagedList<WorkflowListItem> list) {
        if (list != null) {
            int count = list.size();
            setWorkflows(count);
        }
        super.submitList(list);
    }

    public void setAllCheckboxes(boolean isCheck) {
        for (int i = 0; i < getItemCount(); i++) {
            isChecked.set(i, isCheck);
        }
        notifyDataSetChanged();
    }

    private void setWorkflows(int listSize) {
        if (!firstLoad) {
            updateChecks(listSize);
            return;
        }
        // First time loading items
        resetChecksAndExpands(listSize);
        firstLoad = false;
    }

    private void resetChecksAndExpands(int listSize) {
        isChecked = new ArrayList<>();
        isExpanded = new ArrayList<>();
        int i = 0;
        while (i < listSize) {
            isChecked.add(false);
            isExpanded.add(false);
            ++i;
        }
    }

    private void updateChecks(int listSize) {
        int currentCount = getItemCount();
        int difference = listSize - currentCount;
        if (difference < 1) {
            return;
        }
        for (int i = 0; i < difference; i++) {
            isChecked.add(false);
            isExpanded.add(false);
        }
    }

}
