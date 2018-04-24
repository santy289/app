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
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModelFactory;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 26/03/18.
 */

public class ServicioSpinner extends CustomSpinner {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private List<Service> items;

    public ServicioSpinner(FragmentActivity activity, Field field, String auth) {
        super(activity, field);
        ((RootnetApp) activity.getApplication()).getAppComponent().
                inject(this);
        viewModel = ViewModelProviders
                .of(activity, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();
        viewModel.getServices(auth);
    }

    @Override
    public void subscribe() {
        final Observer<ServicesResponse> listObserver = ((ServicesResponse data) -> {
            if (null != data) {
                items = data.getList();
                for (Service item : items) {
                    spn_data.add(item.getName());
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
        viewModel.getObservableService().observe(activity, listObserver);
    }

    public Service getSelectedItem() {
        return items.get(spinner.getSelectedItemPosition());
    }
}