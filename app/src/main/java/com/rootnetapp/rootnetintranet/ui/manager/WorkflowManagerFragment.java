package com.rootnetapp.rootnetintranet.ui.manager;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.PendingWorkflowsAdapter;

public class WorkflowManagerFragment extends Fragment {

    private FragmentWorkflowManagerBinding binding;

    public WorkflowManagerFragment() {
        // Required empty public constructor
    }

    public static WorkflowManagerFragment newInstance() {
        WorkflowManagerFragment fragment = new WorkflowManagerFragment();
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
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_manager, container, false);
        View view = binding.getRoot();
        binding.recTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recTimeline.setAdapter(new PendingWorkflowsAdapter());
        return view;
    }

}
