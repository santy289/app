package com.rootnetapp.rootnetintranet.ui.manager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DialogWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.ManagerDialogAdapter;

/**
 * Created by root on 19/04/18.
 */

public class ManagerWorkflowsDialog extends DialogFragment {

    private DialogWorkflowManagerBinding binding;
    private ManagerInterface anInterface;
    private DialogTypes type;

    public static ManagerWorkflowsDialog newInstance(ManagerInterface anInterface, DialogTypes type) {
        ManagerWorkflowsDialog fragment = new ManagerWorkflowsDialog();
        fragment.anInterface = anInterface;
        fragment.type = type;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_workflow_manager, container, false);
        //((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        switch (type){
            case PENDING:{
                binding.square.setText(R.string.pending_workflows);
                break;
            }
            case WORKFLOWS:{
                binding.square.setText(R.string.workflows);
                break;
            }
            case OUT_OF_TIME:{
                binding.square.setText(R.string.out_of_time);
                break;
            }
            case UPDATED:{
                binding.square.setText(R.string.updated);
                break;
            }
        }
        binding.btnClose.setOnClickListener(view -> dismiss());
        binding.recWorkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recWorkflows.setAdapter(new ManagerDialogAdapter());
        return binding.getRoot();
    }

    public enum DialogTypes{
        PENDING, WORKFLOWS, OUT_OF_TIME, UPDATED
    }

}
