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
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomCountryPicker;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ListSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ProductoSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ServicioSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.UsuariosSpinner;
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
    private List<WorkflowType> workflowTypes;

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
        binding.btnCreate.setOnClickListener(view -> createWorkflow());
        subscribe();
        viewModel.getWorkflowTypes("");
        return binding.getRoot();
    }

    private void subscribe() {
        final Observer<List<WorkflowType>> workflowsObserver = ((List<WorkflowType> data) -> {
            if (null != data) {
                this.workflowTypes.addAll(data);
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
                /*binding.spnWorkflowtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                                if (config.getListInfo() != null) {
                                                    ListSpinner spinner = new ListSpinner(getActivity(),
                                                            field, config.getListInfo().getId());
                                                    binding.layoutDinamicFields.addView(spinner,
                                                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                                                    view_list.add(spinner);
                                                }
                                                break;
                                            }
                                            case "Producto": {
                                                ProductoSpinner spinner = new ProductoSpinner(getActivity(),
                                                        field);
                                                binding.layoutDinamicFields.addView(spinner,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(spinner);
                                                break;
                                            }
                                            case "Servicio": {
                                                ServicioSpinner spinner = new ServicioSpinner(getActivity(),
                                                        field);
                                                binding.layoutDinamicFields.addView(spinner,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(spinner);
                                                break;
                                            }
                                            case "Usuario": {
                                                UsuariosSpinner spinner = new UsuariosSpinner(getActivity(), field);
                                                binding.layoutDinamicFields.addView(spinner,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(spinner);
                                                break;
                                            }
                                            case "Teléfono": {
                                                CustomCountryPicker picker = new CustomCountryPicker(getActivity(),
                                                        field, CustomCountryPicker.PickerType.CODIGO_TELEFONICO);
                                                binding.layoutDinamicFields.addView(picker,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(picker);
                                                break;
                                            }
                                            case "Moneda": {
                                                CustomCountryPicker picker = new CustomCountryPicker(getActivity(),
                                                        field, CustomCountryPicker.PickerType.MONEDA);
                                                binding.layoutDinamicFields.addView(picker,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(picker);
                                                break;
                                            }
                                            case "Archivo": {
                                                //todo COMO SUBO ARCHIVOS??
                                                break;
                                            }
                                            case "Contacto": {
                                                //todo La consulta da error 500
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
            */
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
                //dismiss();
            }
        });
        viewModel.getObservableWorkflows().observe(this, workflowsObserver);
        viewModel.getObservableError().observe(this, errorObserver);
    }

    private void createWorkflow() {
        /*int workflowTypeId = workflowTypes.get(binding.spnWorkflowtype.getSelectedItemPosition())
                .getId();
        String title = binding.inputWorkflowname.getText().toString();
        String start = binding.pickerStart.getYear()+ "-" +binding.pickerStart.getMonth() + "-" +binding.pickerStart.getDayOfMonth() ;
        String description = binding.inputWorkflowdescription.getText().toString();

        final Observer<Object> workflowsObserver = ((Object data) -> {
            if (null != data) {
                //todo inform changes
                dismiss();
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getObservableCreate().observe(this, workflowsObserver);
        viewModel.getObservableCreateError().observe(this, errorObserver);
        viewModel.createWorkflow("",workflowTypeId, title, "", start, description);*/
    }

}
