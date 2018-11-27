package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowSearchBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class WorkflowSearchFragment extends Fragment {

    @Inject
    WorkflowSearchViewModelFactory workflowSearchViewModelFactory;
    private WorkflowSearchViewModel workflowSearchViewModel;
    private FragmentWorkflowSearchBinding mBinding;
    private @QuickAction int mAction;

    public WorkflowSearchFragment() {
        // Required empty public constructor
    }

    public static WorkflowSearchFragment newInstance(@QuickAction int action) {
        WorkflowSearchFragment fragment = new WorkflowSearchFragment();
        fragment.mAction = action;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_search, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowSearchViewModel = ViewModelProviders
                .of(this, workflowSearchViewModelFactory)
                .get(WorkflowSearchViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        subscribe();

        return view;
    }

    private void subscribe() {

    }
}