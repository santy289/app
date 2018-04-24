package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.databinding.PeopleInvolvedItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 03/04/18.
 */

public class PeopleInvolvedAdapter extends RecyclerView.Adapter<PeopleInvolvedViewholder>{

    //todo SOLO TESTING mientras no hay backend
    private List<Integer> People;

    public PeopleInvolvedAdapter() {
        People = new ArrayList<>();
        int i=0;
        while(i<3){
            People.add(1);
            i++;
        }
    }

    @Override
    public PeopleInvolvedViewholder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater =
                LayoutInflater.from(viewGroup.getContext());
        PeopleInvolvedItemBinding itemBinding =
                PeopleInvolvedItemBinding.inflate(layoutInflater, viewGroup, false);
        return new PeopleInvolvedViewholder(itemBinding);
    }

    @Override
    public void onBindViewHolder(PeopleInvolvedViewholder peopleInvolvedViewholder, int i) {

    }

    @Override
    public int getItemCount() {
        return People.size();
    }
}
