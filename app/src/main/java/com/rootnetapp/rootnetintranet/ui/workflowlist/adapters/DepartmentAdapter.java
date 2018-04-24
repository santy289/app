package com.rootnetapp.rootnetintranet.ui.workflowlist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.DepartmentItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 28/03/18.
 */

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentViewholder>{

    private List<String> dummyDprtmnts;

    public DepartmentAdapter() {
        this.dummyDprtmnts = new ArrayList<>();
        //todo Solo testing mientras no existe el endpoint
        int i = 0;
        while(i<3){
            dummyDprtmnts.add("x");
            i++;
        }
    }

    @Override
    public DepartmentViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        DepartmentItemBinding itemBinding =
                DepartmentItemBinding.inflate(layoutInflater, viewGroup, false);
        return new DepartmentViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(DepartmentViewholder holder, int i) {

    }

    @Override
    public int getItemCount() {
        return dummyDprtmnts.size();
    }
}
