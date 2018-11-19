package com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class StepsSpinnerAdapter extends ArrayAdapter {

    private String[] mDataset;

    public StepsSpinnerAdapter(@NonNull Context context,
                               int resource,
                               int textViewResourceId, @NonNull String[] objects) {
        super(context, resource, textViewResourceId, objects);

        mDataset = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getCount() {
        return mDataset.length;
    }

    @Override
    public String getItem(int position) {
        return mDataset[position];
    }
}
