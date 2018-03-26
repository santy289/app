package com.rootnetapp.rootnetintranet.ui.createworkflow.customviews;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.country.Country;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 26/03/18.
 */

public class CustomCountryPicker extends FrameLayout {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private List<Country> items;
    private List<String> spn_data;
    private Spinner spinner;
    private FragmentActivity activity;
    private PickerType type;

    public CustomCountryPicker(FragmentActivity activity, Field field, PickerType type) {
        super(activity);
        this.activity = activity;
        this.type = type;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.prototype_country_textinput, this, true);
        TextView title = findViewById(R.id.field_title);
        title.setText(field.getFieldName());
        spinner = findViewById(R.id.field_spinner);
        spn_data = new ArrayList<>();
        ((RootnetApp) activity.getApplication()).getAppComponent().
                inject(this);
        viewModel = ViewModelProviders
                .of(activity, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();
        viewModel.getCountries("");
    }

    private void subscribe() {
        final Observer<CountriesResponse> countryObserver = ((CountriesResponse data) -> {
            if (null != data) {
                items = data.getCountries();
                for (Country item : items) {
                    if (type.equals(PickerType.MONEDA)) {
                        spn_data.add(item.getCurrency() + " - " + item.getDescription());
                    } else {
                        spn_data.add(item.getPhoneCode() + " - " + item.getDescription());
                    }
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
        viewModel.getObservableCountries().observe(activity, countryObserver);

    }

    public Country getCountry() {

        return null;
    }

    public int getNumber() {

        return 0;
    }

    public enum PickerType {
        MONEDA, CODIGO_TELEFONICO
    }
}
