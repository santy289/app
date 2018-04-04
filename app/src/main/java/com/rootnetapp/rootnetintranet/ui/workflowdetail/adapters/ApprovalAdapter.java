package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.ApprovalHistoryItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 03/04/18.
 */

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalViewholder>{

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> Approvers;

    public ApprovalAdapter() {
        Approvers = new ArrayList<>();
        int i=0;
        while(i<3){
            Approvers.add(1);
            i++;
        }
    }

    @Override
    public ApprovalViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ApprovalHistoryItemBinding itemBinding =
                ApprovalHistoryItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ApprovalViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ApprovalViewholder approvalViewholder, int i) {

    }

    @Override
    public int getItemCount() {
        return Approvers.size();
    }
}
