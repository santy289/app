package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.ApproversItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 02/04/18.
 */

public class ApproversAdapter extends RecyclerView.Adapter<ApproversViewholder>{

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> Approvers;

    public ApproversAdapter() {
        Approvers = new ArrayList<>();
        int i=0;
        while(i<5){
            Approvers.add(1);
            i++;
        }
    }

    @Override
    public ApproversViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ApproversItemBinding itemBinding =
                ApproversItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ApproversViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ApproversViewholder approversViewholder, int i) {

    }

    @Override
    public int getItemCount() {
        return Approvers.size();
    }
}
