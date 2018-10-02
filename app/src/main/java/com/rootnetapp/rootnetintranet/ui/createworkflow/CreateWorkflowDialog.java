package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.DialogCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.CountryData;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.WorkflowMetas;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Field;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.FieldConfig;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowType;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.CustomCountryPicker;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ListSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ProductoSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.ServicioSpinner;
import com.rootnetapp.rootnetintranet.ui.createworkflow.customviews.UsuariosSpinner;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragmentInterface;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.DepartmentAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CreateWorkflowDialog extends DialogFragment {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private DialogCreateWorkflowBinding binding;
    private List<View> view_list;
    private List<WorkflowType> workflowTypes;
    private int selectedType = -1;
    private WorkflowFragmentInterface anInterface;
    private String token;

    public static CreateWorkflowDialog newInstance(WorkflowFragmentInterface anInterface) {
        CreateWorkflowDialog fragment = new CreateWorkflowDialog();
        fragment.anInterface = anInterface;
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
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        view_list = new ArrayList<>();
        workflowTypes = new ArrayList<>();
        binding.recDepartment.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        binding.btnClose.setOnClickListener(view -> dismiss());
        binding.btnCreate.setOnClickListener(view -> createWorkflow());
        subscribe();
        viewModel.getWorkflowTypes(token);
        return binding.getRoot();
    }

    private void subscribe() {
        final Observer<WorkflowTypesResponse> workflowsObserver = ((WorkflowTypesResponse data) -> {
            if (null != data) {
                this.workflowTypes.addAll(data.getList());
                List<String> types = new ArrayList<>();
                for (WorkflowType type : data.getList()) {
                    types.add(type.getName());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                        R.layout.spinner_item, types);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spnWorkflowtype.setAdapter(dataAdapter);
                binding.spnWorkflowtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedType = i;
                        for (View aView : view_list) {
                            binding.layoutDinamicFields.removeView(aView);
                        }
                        view_list.clear();
                        for (Field field : data.getList().get(i).getFields()) {
                            //todo FieldConfig llega mal formateado del backend, si se arregla eso, se elimina este workaround
                            Moshi moshi = new Moshi.Builder().build();
                            JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
                            FieldConfig config = null;
                            try {
                                config = jsonAdapter.fromJson(field.getFieldConfig());
                                if (config.getFormShow() != null) {
                                    if (config.getFormShow()) {
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
                                                TextView title = v.findViewById(R.id.field_title);
                                                title.setText(field.getFieldName());
                                                break;
                                            }
                                            case "Date": {
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
//                                                ProductoSpinner spinner = new ProductoSpinner(getActivity(),
//                                                        field, token);
//                                                binding.layoutDinamicFields.addView(spinner,
//                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
//                                                view_list.add(spinner);
//                                                break;
                                            }
                                            case "Servicio": {
                                                ServicioSpinner spinner = new ServicioSpinner(getActivity(),
                                                        field, token);
                                                binding.layoutDinamicFields.addView(spinner,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(spinner);
                                                break;
                                            }
                                            case "Usuario": {
                                                UsuariosSpinner spinner = new UsuariosSpinner(getActivity(), field, token);
                                                binding.layoutDinamicFields.addView(spinner,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(spinner);
                                                break;
                                            }
                                            case "Teléfono": {
                                                CustomCountryPicker picker = new CustomCountryPicker(getActivity(),
                                                        field, CustomCountryPicker.PickerType.CODIGO_TELEFONICO, token);
                                                binding.layoutDinamicFields.addView(picker,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(picker);
                                                break;
                                            }
                                            case "Moneda": {
                                                CustomCountryPicker picker = new CustomCountryPicker(getActivity(),
                                                        field, CustomCountryPicker.PickerType.MONEDA, token);
                                                binding.layoutDinamicFields.addView(picker,
                                                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                                view_list.add(picker);
                                                break;
                                            }
                                            case "Enlace": {
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
                                            case "Archivo": {
                                                break;
                                            }
                                            case "Contacto": {
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        view_list.add(null);
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
                binding.recDepartment.setAdapter(new DepartmentAdapter());
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
        //viewModel.getObservableWorkflows().observe(this, workflowsObserver);
        //viewModel.getObservableError().observe(this, errorObserver);
    }

    private void createWorkflow() {
        int workflowTypeId = workflowTypes.get(binding.spnWorkflowtype.getSelectedItemPosition())
                .getId();
        String title = binding.inputWorkflowname.getText().toString();
        String start = binding.pickerStart.getYear() + "-" + binding.pickerStart.getMonth() + "-" + binding.pickerStart.getDayOfMonth();
        String description = binding.inputWorkflowdescription.getText().toString();
        List<WorkflowMetas> metas = new ArrayList<>();

        int i = 0;
        for (Field field : workflowTypes.get(selectedType).getFields()) {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<FieldConfig> jsonAdapter = moshi.adapter(FieldConfig.class);
            FieldConfig config = null;
            try {
                config = jsonAdapter.fromJson(field.getFieldConfig());
                if (config.getFormShow() != null) {
                    if (config.getFormShow()) {
                        WorkflowMetas wm = new WorkflowMetas();
                        switch (config.getTypeInfo().getName()) {
                            case "Texto":
                            case "Area de Texto":
                            case "Email":
                            case "Numerico": {
                                View v = view_list.get(i);
                                AppCompatEditText et = v.findViewById(R.id.field_input);
                                wm.setValue(et.getText().toString());
                                break;
                            }
                            case "Checkbox": {
                                View v = view_list.get(i);
                                Switch checkBox = v.findViewById(R.id.field_checkbox);
                                wm.setValue(String.valueOf(checkBox.isChecked()));
                                break;
                            }
                            case "Date": {
                                View v = view_list.get(i);
                                DatePicker picker = v.findViewById(R.id.field_datepicker);
                                String date = picker.getYear() + "-" + picker.getMonth() + "-" + picker.getDayOfMonth();
                                wm.setValue(date);
                                break;
                            }
                            case "Fecha": {
                                View v = view_list.get(i);
                                DatePicker picker = v.findViewById(R.id.field_datepicker);
                                String date = picker.getYear() + "-" + picker.getMonth() + "-" + picker.getDayOfMonth();
                                wm.setValue(date);
                                break;
                            }
                            case "Lista": {
                                ListSpinner v = (ListSpinner) view_list.get(i);
                                wm.setValue(String.valueOf(v.getSelectedItem().getId()));
                                break;
                            }
                            case "Producto": {
                                ProductoSpinner v = (ProductoSpinner) view_list.get(i);
                                wm.setValue(String.valueOf(v.getSelectedItem().getId()));
                                break;
                            }
                            case "Servicio": {
                                ServicioSpinner v = (ServicioSpinner) view_list.get(i);
                                wm.setValue(String.valueOf(v.getSelectedItem().getId()));
                                break;
                            }
                            case "Usuario": {
                                UsuariosSpinner v = (UsuariosSpinner) view_list.get(i);
                                JsonAdapter<WorkflowUser> adpt = moshi.adapter(WorkflowUser.class);
                                wm.setValue(adpt.toJson(v.getSelectedItem()));
                                break;
                            }
                            case "Teléfono": {
                                CustomCountryPicker v = (CustomCountryPicker) view_list.get(i);
                                JsonAdapter<CountryData> adpt = moshi.adapter(CountryData.class);
                                CountryData data = new CountryData();
                                data.setValue(v.getNumber());
                                data.setCountryId(v.getCountry().getCountryId());
                                wm.setValue(adpt.toJson(data));
                                break;
                            }
                            case "Moneda": {
                                CustomCountryPicker v = (CustomCountryPicker) view_list.get(i);
                                JsonAdapter<CountryData> adpt = moshi.adapter(CountryData.class);
                                CountryData data = new CountryData();
                                data.setValue(v.getNumber());
                                data.setCountryId(v.getCountry().getCountryId());
                                wm.setValue(adpt.toJson(data));
                                break;
                            }
                            case "Enlace": {
                                //todo no funciona en la web?
                                break;
                            }
                            case "Archivo": {
                                //todo Buscar Como Subir archivos??
                                break;
                            }
                            case "Contacto": {
                                //todo La consulta da error 500
                                break;
                            }
                        }
                        wm.setWorkflowTypeFieldId(field.getId());
                        metas.add(wm);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }


        Type listMyData = Types.newParameterizedType(List.class, WorkflowMetas.class);
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<List<WorkflowMetas>> jsonAdapter = moshi.adapter(listMyData);

        String workflowMetas = jsonAdapter.toJson(metas);

        final Observer<CreateWorkflowResponse> workflowsObserver = ((CreateWorkflowResponse data) -> {
            if (null != data) {
                Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                anInterface.dataAdded();
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
        viewModel.createWorkflow(token, workflowTypeId, title, workflowMetas, start, description);
    }

}
