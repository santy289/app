package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DialogCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

/**
 * Created by root on 21/03/18.
 */

public class CreateWorkflowDialog extends DialogFragment{

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private DialogCreateWorkflowBinding binding;

    public static CreateWorkflowDialog newInstance() {
        CreateWorkflowDialog fragment = new CreateWorkflowDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_create_workflow , container, false);
        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);
        setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();

        binding.btnClose.setOnClickListener(view -> dismiss());
        return binding.getRoot();
    }

    private void subscribe() {



    }

}
