package com.rootnetapp.rootnetintranet.ui.massapproval.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.databinding.ItemMassApprovalBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Status;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;

import java.util.ArrayList;
import java.util.List;

public class MassApprovalAdapter extends RecyclerView.Adapter<MassApprovalViewholder> {

    private List<Status> mDataset;
    private WorkflowTypeDb mWorkflowType;

    public MassApprovalAdapter(WorkflowTypeDb workflowTypeDb, List<Status> statuses) {
        this.mWorkflowType = workflowTypeDb;
        this.mDataset = statuses;
    }

    @Override
    public MassApprovalViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ItemMassApprovalBinding itemBinding =
                ItemMassApprovalBinding.inflate(layoutInflater, viewGroup, false);
        return new MassApprovalViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MassApprovalViewholder holder, int position) {
        Status status = getItem(position);
        Context context = holder.getBinding().spSteps.getContext();

        List<String> nextStatuses = new ArrayList<>();

        for (int nextStatusId : status.getRelations()) {

            Status nextStatus = findStatusInListBy(nextStatusId);
            if (nextStatus == null) {
                continue;
            }
            String name = nextStatus.getName();
            if (name == null) {
                continue;
            }
            nextStatuses.add(name);
        }

        nextStatuses.add(context.getString(R.string.mass_approval_activity_reject));

        String hint = context.getString(R.string.workflow_detail_status_fragment_spinner_title);
        // check whether the hint has already been added
        if (!nextStatuses.get(0).equals(hint)) {
            // add hint as first item
            nextStatuses.add(0, hint);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                nextStatuses
        );
        holder.getBinding().spSteps.setAdapter(adapter);

        // listener to keep track of selected item
        holder.getBinding().spSteps
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {

                        Integer stepIndex;

                        //account for hint as the first item
                        if (position == 0) {
                            stepIndex = null; //null gets ignored by API call
                        } else {
                            stepIndex = position - 1;
                        }

//                        statusViewModel.setApproveSpinnerItemSelection(stepIndex);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        holder.getBinding().executePendingBindings();
    }

    private Status getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private Status findStatusInListBy(int statusId) {
        List<Status> statusList = mWorkflowType.getStatus();
        if (statusList == null || statusList.size() < 1) {
            return null;
        }

        Status status;
        for (int i = 0; i < statusId; i++) {
            status = statusList.get(i);
            if (status.getId() == statusId) {
                return status;
            }
        }

        return null;
    }
}
