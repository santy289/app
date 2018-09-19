package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.FragmentCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.riddhimanadib.formmaster.FormBuilder;
import me.riddhimanadib.formmaster.listener.OnFormElementValueChangedListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementSwitch;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;


public class WorkFlowCreateFragment extends Fragment implements OnFormElementValueChangedListener {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private MainActivityInterface mainActivityInterface;
    private FragmentCreateWorkflowBinding fragmentCreateWorkflowBinding;

    private FormBuilder formBuilder;
    private List<BaseFormElement> formItems = new ArrayList<>();

    private static final String TAG = "CreateFragment";

    public WorkFlowCreateFragment() { }

    public static WorkFlowCreateFragment newInstance(MainActivityInterface mainActivityInterface) {
        WorkFlowCreateFragment fragment = new WorkFlowCreateFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCreateWorkflowBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_create_workflow,
                container,
                false
        );
        View view = fragmentCreateWorkflowBinding.getRoot();

        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);

        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        subscribe();
        viewModel.initForm(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        int tag = baseFormElement.getTag();
        if (tag == CreateWorkflowViewModel.TAG_WORKFLOW_TYPE) {
            String typeSelected = baseFormElement.getValue();
            viewModel.generateFieldsByType(typeSelected);
        }
    }

    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void addFieldList(List<String> items) {
        FormElementPickerSingle workflowTypes = FormElementPickerSingle
                .createInstance()
                .setTitle(getString(R.string.form_workflow_type))
                .setOptions(items).setPickerTitle(getString(R.string.pick_option));
        String name = items.get(0);
        workflowTypes.setValue(name);
        formItems.add(workflowTypes);
    }

    private void addFieldList(FieldListSettings settings) {
        String label = getString(settings.labelRes);
        FormElementPickerSingle fieldList = FormElementPickerSingle
                .createInstance()
                .setTitle(label)
                .setTag(settings.tag)
                .setOptions(settings.items).setPickerTitle(getString(R.string.pick_option));
        fieldList.setValue(settings.items.get(0));
        formItems.add(fieldList);
    }

    // Single line
    private void addTexField(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementTextSingleLine textField = FormElementTextSingleLine
                .createInstance()
                .setTitle(label)
                .setRequired(required);
        formItems.add(textField);
    }

    private void addTexField(FieldData settingsData) {
        String label = settingsData.label;
        boolean required = settingsData.required;
        FormElementTextSingleLine textField = FormElementTextSingleLine
                .createInstance()
                .setTitle(label)
                .setRequired(required);
        formItems.add(textField);
    }

    // Multiple lines
    private void addTextFieldMultiLines(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementTextMultiLine textFieldMultiLine = FormElementTextMultiLine
                .createInstance()
                .setTitle(label)
                .setRequired(required);
        formItems.add(textFieldMultiLine);
    }

    private void addTextFieldMultiLines(FieldData fieldData) {
        String label = fieldData.label;
        FormElementTextMultiLine textFieldMultiLine = FormElementTextMultiLine
                .createInstance()
                .setTitle(label)
                .setRequired(fieldData.required);
        formItems.add(textFieldMultiLine);
    }

    private void datePickerField(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementPickerDate datePicker = FormElementPickerDate
                .createInstance()
                .setTitle(label)
                .setRequired(required)
                .setDateFormat("MMM dd, yyyy");
        formItems.add(datePicker);
    }

    private void datePickerField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementPickerDate datePicker = FormElementPickerDate
                .createInstance()
                .setTitle(label)
                .setRequired(required)
                .setDateFormat("MMM dd, yyyy");
        formItems.add(datePicker);
    }

    private void addNumericField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementTextNumber numericField = FormElementTextNumber
                .createInstance()
                .setRequired(required)
                .setTitle(label);
        formItems.add(numericField);
    }

    private void addSwitchField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementSwitch switchField = FormElementSwitch
                .createInstance()
                .setRequired(required)
                .setTitle(label);
        formItems.add(switchField);
    }

    private void formHeader(Integer labelRes) {
        String label = getString(labelRes);
        FormHeader formHeader = FormHeader.createInstance(label);
        formItems.add(formHeader);
    }

    private void buildForm() {
//        List<BaseFormElement> newList = new ArrayList<>(formItems);
        formBuilder = new FormBuilder(
                getContext(),
                fragmentCreateWorkflowBinding.recCreateWorkflow,
                this);
        formBuilder.addFormElements(formItems);
//        formItems = newList;
//        refreshForm();
    }

    private void refreshForm() {
        RecyclerView.Adapter adapter =
                fragmentCreateWorkflowBinding.recCreateWorkflow.getAdapter();
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    private void subscribe() {
        final Observer<Boolean> showLoadingObserver = (this::showLoading);
        viewModel.getObservableShowLoading().observe(this, showLoadingObserver);

        viewModel.setTypeList.observe(this, this::addFieldList);

        viewModel.buildForm.observe(this, ( build -> buildForm()));

        viewModel.setTextField.observe(this, this::addTexField);

        viewModel.setTextFieldMultiLine.observe(this, this::addTextFieldMultiLines);

        viewModel.setDatePicker.observe(this, this::datePickerField);

        viewModel.setFormHeader.observe(this, this::formHeader);

        viewModel.setFieldList.observe(this, this::addFieldList);

        viewModel.setFieldTextWithData.observe(this, this::addTexField);

        viewModel.setFieldNumericWithData.observe(this, this::addNumericField);

        viewModel.setFieldAreaWithData.observe(this, this::addTextFieldMultiLines);

        viewModel.setFieldDateWithData.observe(this, this::datePickerField);

        viewModel.refreshForm.observe(this, refresh -> refreshForm());

        viewModel.setFieldSwitchWithData.observe(this, this::addSwitchField);

    }

}
