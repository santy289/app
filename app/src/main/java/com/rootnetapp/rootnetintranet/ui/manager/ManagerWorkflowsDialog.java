package com.rootnetapp.rootnetintranet.ui.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.databinding.DialogWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.ManagerDialogAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by root on 19/04/18.
 */

public class ManagerWorkflowsDialog extends DialogFragment {

    private DialogWorkflowManagerBinding binding;
    private ManagerInterface anInterface;
    private DialogTypes type;
    private List<WorkflowDb> workflows;

    public static ManagerWorkflowsDialog newInstance(ManagerInterface anInterface, DialogTypes type,
                                                     @NonNull List<WorkflowDb> workflows) {
        ManagerWorkflowsDialog fragment = new ManagerWorkflowsDialog();
        fragment.anInterface = anInterface;
        fragment.type = type;
        fragment.workflows = workflows;
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
        switch (type) {
            case PENDING: {
                binding.square.setText(R.string.pending_workflows);
                break;
            }
            case WORKFLOWS: {
                binding.square.setText(R.string.workflows);
                break;
            }
            case OUT_OF_TIME: {
                binding.square.setText(R.string.out_of_time);
                break;
            }
            case UPDATED: {
                binding.square.setText(R.string.updated);
                break;
            }
        }
        binding.btnClose.setOnClickListener(view -> dismiss());
        binding.recWorkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recWorkflows.setAdapter(new ManagerDialogAdapter(workflows, anInterface));
        return binding.getRoot();
    }

    public enum DialogTypes {
        PENDING, WORKFLOWS, OUT_OF_TIME, UPDATED
    }

}
