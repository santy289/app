package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.DialogCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Element;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by root on 21/03/18.
 */

public class CreateWorkflowDialog extends DialogFragment {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private DialogCreateWorkflowBinding binding;
    private List<View> view_list;

    public static CreateWorkflowDialog newInstance() {
        CreateWorkflowDialog fragment = new CreateWorkflowDialog();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_create_workflow, container, false);
        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);
        setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        view_list = new ArrayList<>();
        binding.btnClose.setOnClickListener(view -> dismiss());
        subscribe();
        viewModel.getWorkflowTypes("");
        return binding.getRoot();
    }

    private void subscribe() {
        final Observer<List<WorkflowType>> workflowsObserver = ((List<WorkflowType> data) -> {
            if (null != data) {
                // Spinner Drop down elements
                List<String> types = new ArrayList<>();
                for (WorkflowType type : data) {
                    types.add(type.getName());
                }
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, types);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                binding.spnWorkflowtype.setAdapter(dataAdapter);
                // Spinner click listener
                binding.spnWorkflowtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        //todo FieldConfig llega mal formateado del backend, si se arregla eso, se elimina este workaround

                        for (View aView : view_list) {
                            binding.layoutDinamicFields.removeView(aView);
                        }
                        view_list.clear();
                        for (Field field : data.get(i).getFields()) {
                            field.getFieldConfig().substring(1, field.getFieldConfig().length() - 1);
                            Moshi moshi = new Moshi.Builder().build();
                            JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
                            FieldConfig config = null;
                            try {
                                config = jsonAdapter.fromJson(field.getFieldConfig());
                                if (config.getFormShow() != null) {
                                    if (config.getFormShow()) {
                                        //insertar campo dinamico
                                        Log.d("test", "onItemSelected: Show new " + config.getTypeInfo().getValueType());
                                        switch (config.getTypeInfo().getName()) {
                                            case "Texto": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_textinput, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                AppCompatEditText et = v.findViewById(R.id.field_input);
                                                et.setMaxLines(1);
                                                break;
                                            }
                                            case "Area de Texto": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_textinput, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                break;
                                            }
                                            case "Email": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_textinput, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                AppCompatEditText et = v.findViewById(R.id.field_input);
                                                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                                et.setMaxLines(1);
                                                break;
                                            }
                                            case "Numerico": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_textinput, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                AppCompatEditText et = v.findViewById(R.id.field_input);
                                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                et.setMaxLines(1);
                                                break;
                                            }
                                            case "Checkbox": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_checkbox, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                CheckBox title = v.findViewById(R.id.field_checkbox);
                                                title.setText(field.getFieldName());
                                                break;
                                            }
                                            case "Fecha": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_date, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                break;
                                            }
                                            case "Lista": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_list, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                Spinner spinner = v.findViewById(R.id.field_spinner);
                                                List<String> spn_data = new ArrayList<>();

                                                if(config.getListInfo() != null){
                                                    if (config.getListInfo().getElements() != null){
                                                        for (Element item : config.getListInfo().getElements()) {
                                                            spn_data.add(item.getName());
                                                        }
                                                    }
                                                }
                                                // Creating adapter for spinner
                                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                                                        android.R.layout.simple_spinner_item, spn_data);
                                                // Drop down layout style - list view with radio button
                                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                // attaching data adapter to spinner
                                                spinner.setAdapter(dataAdapter);
                                                break;
                                            }
                                            case "Producto": {

                                                break;
                                            }
                                            case "Servicio": {

                                                break;
                                            }
                                            case "Usuario": {

                                                break;
                                            }
                                            case "Teléfono": {
                                                LayoutInflater vi = (LayoutInflater) getContext()
                                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View v = vi.inflate(R.layout.prototype_textinput, null);
                                                binding.layoutDinamicFields.addView(v,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(v);
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                AppCompatEditText et = v.findViewById(R.id.field_input);
                                                et.setInputType(InputType.TYPE_CLASS_PHONE);
                                                et.setMaxLines(1);
                                                break;
                                            }
                                            case "Moneda": {

                                                break;
                                            }
                                            case "Archivo": {

                                                break;
                                            }
                                            case "Contacto": {

                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
        viewModel.getObservableWorkflows().observe(this, workflowsObserver);
        viewModel.getObservableError().observe(this, errorObserver);
    }

}
