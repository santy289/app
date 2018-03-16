package com.rootnetapp.rootnetintranet.ui.workflowlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;

public class WorkflowFragment extends Fragment {

    public WorkflowFragment() {
        // Required empty public constructor
    }


    public static WorkflowFragment newInstance() {
        WorkflowFragment fragment = new WorkflowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workflow, container, false);
    }

}
