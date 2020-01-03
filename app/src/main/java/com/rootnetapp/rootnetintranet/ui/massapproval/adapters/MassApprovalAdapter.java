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
import com.rootnetapp.rootnetintranet.ui.massapproval.models.StatusApproval;

import java.util.ArrayList;
import java.util.List;

public class MassApprovalAdapter extends RecyclerView.Adapter<MassApprovalViewholder> {

    private List<StatusApproval> mDataset;
    private WorkflowTypeDb mWorkflowType;

    public MassApprovalAdapter(WorkflowTypeDb workflowTypeDb, List<StatusApproval> statuses) {
        this.mWorkflowType = workflowTypeDb;
        this.mDataset = statuses;
    }

    public List<StatusApproval> getDataset() {
        return mDataset;
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
        StatusApproval statusApproval = getItem(position);
        Context context = holder.getBinding().spSteps.getContext();

        holder.getBinding().tvStatusTitle.setText(statusApproval.getStatus().getName());

        List<String> nextStatuses = new ArrayList<>();

        for (int nextStatusId : statusApproval.getStatus().getRelations()) {

            Status nextStatus = Status
                    .getStatusByIdFromList(mWorkflowType.getStatus(), nextStatusId);

            if (nextStatus == null) {
                continue;
            }
            String name = nextStatus.getName();
            if (name == null) {
                continue;
            }
            nextStatuses.add(name);
        }

        final String rejectString = context.getString(R.string.mass_approval_activity_reject);
        nextStatuses.add(rejectString);

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
        holder.getBinding().spSteps.setTag(statusApproval);

        // listener to keep track of selected item
        holder.getBinding().spSteps
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {

                        String selectedItem = parent.getSelectedItem().toString();
                        if (selectedItem.equals(rejectString)) {
                            //last item (reject)
                            statusApproval.setRejected(true);
                        } else if (position != 0) {
                            //account for hint as the first item
                            int index = position - 1;

                            int statusId = statusApproval.getStatus().getRelations().get(index);
                            Status nextStatus = Status
                                    .getStatusByIdFromList(mWorkflowType.getStatus(), statusId);

                            statusApproval.setSelectedStatus(nextStatus);
                            statusApproval.setRejected(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        holder.getBinding().executePendingBindings();
    }

    private StatusApproval getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
