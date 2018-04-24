package com.rootnetapp.rootnetintranet.ui.manager.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.ManagerDialogItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 20/04/18.
 */

public class ManagerDialogAdapter extends RecyclerView.Adapter<ManagerDialogViewholder> {

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> workflows;
    private Context context;

    public ManagerDialogAdapter() {
        workflows = new ArrayList<>();
        int i=0;
        while(i<8){
            workflows.add(1);
            i++;
        }
    }

    @Override
    public ManagerDialogViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        ManagerDialogItemBinding itemBinding =
                ManagerDialogItemBinding.inflate(layoutInflater, viewGroup, false);
        return new ManagerDialogViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ManagerDialogViewholder managerDialogViewholder, int i) {

    }

    @Override
    public int getItemCount() {
        return workflows.size();
    }
}
