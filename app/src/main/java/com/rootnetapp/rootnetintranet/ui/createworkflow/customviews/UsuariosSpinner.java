package com.rootnetapp.rootnetintranet.ui.createworkflow.customviews;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.services.Service;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModelFactory;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 26/03/18.
 */

public class UsuariosSpinner extends CustomSpinner {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private List<WorkflowUser> items;

    public UsuariosSpinner(FragmentActivity activity, Field field) {
        super(activity, field);
        ((RootnetApp) activity.getApplication()).getAppComponent().
                inject(this);
        viewModel = ViewModelProviders
                .of(activity, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();
        viewModel.getUsers("");
    }

    @Override
    public void subscribe() {
        final Observer<WorkflowUserResponse> listObserver = ((WorkflowUserResponse data) -> {
            if (null != data) {
                items = data.getUsers();
                for (WorkflowUser item : items) {
                    spn_data.add(item.getUsername());
                }
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.spinner_item, spn_data);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);
            }
        });
        viewModel.getObservableWorkflowUser().observe(activity, listObserver);
        //TODO on failure
    }

    public WorkflowUser getSelectedItem() {
        return items.get(spinner.getSelectedItemPosition());
    }
}