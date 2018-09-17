package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.FragmentCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;

import javax.inject.Inject;

public class WorkFlowCreateFragment extends Fragment {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private MainActivityInterface mainActivityInterface;
    private FragmentCreateWorkflowBinding fragmentCreateWorkflowBinding;

    public WorkFlowCreateFragment() { }

    public static WorkFlowCreateFragment newInstance(MainActivityInterface mainActivityInterface) {
        WorkFlowCreateFragment fragment = new WorkFlowCreateFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCreateWorkflowBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_create_workflow,
                container,
                false
        );
        View view = fragmentCreateWorkflowBinding.getRoot();

        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);

        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);

        return view;
    }
}
