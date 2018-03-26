package com.rootnetapp.rootnetintranet.ui.createworkflow.customviews;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.rootnetapp.rootnetintranet.models.responses.products.Product;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModelFactory;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 26/03/18.
 */

public class ProductoSpinner extends CustomSpinner {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private List<Product> items;

    public ProductoSpinner(FragmentActivity activity, Field field) {
        super(activity, field);
        ((RootnetApp) activity.getApplication()).getAppComponent().
                inject(this);
        viewModel = ViewModelProviders
                .of(activity, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();
        viewModel.getProducts("");
    }

    @Override
    public void subscribe() {
        final Observer<ProductsResponse> listObserver = ((ProductsResponse data) -> {
            if (null != data) {
                items = data.getList();
                for (Product item : items) {
                    spn_data.add(item.getName());
                }
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, spn_data);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);
            }
        });
        viewModel.getObservableProduct().observe(activity, listObserver);
    }

    public Product getSelectedItem() {
        return items.get(spinner.getSelectedItemPosition());
    }
}
