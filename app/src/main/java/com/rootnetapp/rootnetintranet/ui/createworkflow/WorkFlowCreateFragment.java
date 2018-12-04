package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.FragmentCreateWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.adapters.FormItemsAdapter;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.DialogMessage;
import com.rootnetapp.rootnetintranet.ui.createworkflow.dialog.ValidateFormDialog;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.riddhimanadib.formmaster.model.FormElementPickerDate;
import me.riddhimanadib.formmaster.model.FormElementPickerMulti;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementSwitch;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextMultiLine;
import me.riddhimanadib.formmaster.model.FormElementTextNumber;
import me.riddhimanadib.formmaster.model.FormElementTextPhone;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;

public class WorkFlowCreateFragment extends Fragment {

    @Inject
    CreateWorkflowViewModelFactory createWorkflowViewModelFactory;
    CreateWorkflowViewModel viewModel;
    private MainActivityInterface mainActivityInterface;
    private FragmentCreateWorkflowBinding mBinding;

    private static final String TAG = "CreateFragment";
    private final String FILE_CHOOSER_DIR = "/storage/emulated/legacy";

    private MenuItem uploadMenu;
    private FormItemsAdapter mAdapter;
    private AdapterView.OnItemClickListener mOnSingleChoiceItemClickListener;

    public WorkFlowCreateFragment() { }

    public static WorkFlowCreateFragment newInstance(MainActivityInterface mainActivityInterface) {
        WorkFlowCreateFragment fragment = new WorkFlowCreateFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_create_workflow,
                container,
                false
        );
        View view = mBinding.getRoot();

        ((RootnetApp) getActivity().getApplication()).getAppComponent().
                inject(this);

        viewModel = ViewModelProviders
                .of(this, createWorkflowViewModelFactory)
                .get(CreateWorkflowViewModel.class);
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setOnClickListeners();
        setupFormRecycler();
        subscribe();

        viewModel.initForm(token);
        return view;
    }

    private void subscribe() {

        viewModel.getObservableAddWorkflowTypeItem().observe(this, this::addWorkflowTypeItem);
        viewModel.getObservableAddFormItem().observe(this, this::addItemToForm);
        viewModel.getObservableSetFormItemList().observe(this, this::setItemListToForm);
        viewModel.getObservableValidationUi().observe(this, this::updateValidationUi);

        viewModel.setFieldTextWithData.observe(this, this::addTexFieldData);

        final Observer<Boolean> showLoadingObserver = (this::showLoading);
        viewModel.getObservableShowLoading().observe(this, showLoadingObserver);

        final Observer<DialogMessage> showDialogObserver = (this::showDialog);
        viewModel.getObservableShowDialogMessage().observe(this, showDialogObserver);

        viewModel.setTypeList.observe(this, this::addFieldList);

        viewModel.setTextField.observe(this, this::addTexField);

        viewModel.setTextFieldMultiLine.observe(this, this::addTextFieldMultiLines);

        viewModel.setDatePicker.observe(this, this::datePickerField);

        viewModel.setFormHeader.observe(this, this::formHeader);

        viewModel.setFieldList.observe(this, this::addFieldList);

        viewModel.setFieldNumericWithData.observe(this, this::addNumericField);

        viewModel.setFieldAreaWithData.observe(this, this::addTextFieldMultiLines);

        viewModel.setFieldDateWithData.observe(this, this::datePickerField);

        viewModel.refreshForm.observe(this, refresh -> refreshForm());

        viewModel.setFieldSwitchWithData.observe(this, this::addSwitchField);

        viewModel.setFieldEmailWithData.observe(this, this::addEmailField);

        viewModel.setFieldPhoneWithData.observe(this, this::addPhoneField);

        viewModel.setListWithData.observe(this, this::addList);

        viewModel.setFileUploadField.observe(this, this::fileUploadField);

        viewModel.goBack.observe(this, back -> goBack());

        viewModel.showUploadButton.observe(this, this::setUploadMenu);

        viewModel.chooseFile.observe(this, choose -> chooseFile());

    }

    private void setupFormRecycler() {
        mAdapter = new FormItemsAdapter(getContext(), getChildFragmentManager(), new ArrayList<>());
        mBinding.rvFields.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvFields.setAdapter(mAdapter);
        mBinding.rvFields.setNestedScrollingEnabled(false);
    }

    private void setOnClickListeners() {
        mBinding.btnCreate.setOnClickListener(v -> {
            viewModel.handleCreateWorkflowAction();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_upload:
                viewModel.showUploadFilePicker();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.workflow_create_menu, menu);
        uploadMenu = menu.findItem(R.id.menu_upload);
        uploadMenu.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUploadMenu(Boolean visible) {
        uploadMenu.setVisible(visible);
    }

    private void chooseFile() {
        new ChooserDialog().with(getContext())
                .withStartFile(FILE_CHOOSER_DIR)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        viewModel.selectUploadFile(path, pathFile);
                        Toast.makeText(getContext(), "FILE: " + path, Toast.LENGTH_SHORT).show();
                    }
                })
                .build()
                .show();
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
//        formItems.add(workflowTypes);
    }

    private void addFieldList(FieldListSettings settings) {
        String label = getString(settings.labelRes);
        FormElementPickerSingle fieldList = FormElementPickerSingle
                .createInstance()
                .setTitle(label)
                .setTag(settings.tag)
                .setValue(settings.items.get(0))
                .setOptions(settings.items).setPickerTitle(getString(R.string.pick_option));
//        formItems.add(fieldList);
    }
    // Single line

    private void addTexField(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementTextSingleLine textField = FormElementTextSingleLine
                .createInstance()
                .setTitle(label)
                .setRequired(required);
//        formItems.add(textField);
    }

    private void addTexFieldData(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementTextSingleLine textField = FormElementTextSingleLine
                .createInstance()
                .setTitle(label)
                .setTag(fieldData.tag)
                .setRequired(required);
//        formItems.add(textField);
    }
    // Multiple lines

    private void addTextFieldMultiLines(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementTextMultiLine textFieldMultiLine = FormElementTextMultiLine
                .createInstance()
                .setTitle(label)
                .setRequired(required);
//        formItems.add(textFieldMultiLine);
    }

    private void addTextFieldMultiLines(FieldData fieldData) {
        String label = fieldData.label;
        FormElementTextMultiLine textFieldMultiLine = FormElementTextMultiLine
                .createInstance()
                .setTitle(label)
                .setTag(fieldData.tag)
                .setRequired(fieldData.required);
//        formItems.add(textFieldMultiLine);
    }

    private void datePickerField(int[] settingsData) {
        String label = getString(settingsData[CreateWorkflowViewModel.INDEX_RES_STRING]);
        boolean required = settingsData[CreateWorkflowViewModel.INDEX_REQUIRED] == CreateWorkflowViewModel.REQUIRED;
        FormElementPickerDate datePicker = FormElementPickerDate
                .createInstance()
                .setTitle(label)
                .setRequired(required)
                .setDateFormat("dd-MM-yyyy");
//        formItems.add(datePicker);
    }

    private void datePickerField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementPickerDate datePicker = FormElementPickerDate
                .createInstance()
                .setTitle(label)
                .setDateFormat("dd-MM-yyyy")
                .setTag(fieldData.tag)
                .setRequired(required);
//        formItems.add(datePicker);
    }

    private void addNumericField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementTextNumber numericField = FormElementTextNumber
                .createInstance()
                .setRequired(required)
                .setTag(fieldData.tag)
                .setTitle(label);
//        formItems.add(numericField);
    }

    private void addSwitchField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementSwitch switchField = FormElementSwitch
                .createInstance()
                .setRequired(required)
                .setTitle(label)
                .setTag(fieldData.tag)
                .setSwitchTexts(getString(R.string.yes), getString(R.string.no));
//        formItems.add(switchField);
    }

    private void addEmailField(FieldData fieldData) {
        FormElementTextEmail emailField = FormElementTextEmail
                .createInstance()
                .setTitle(getString(R.string.email))
                .setTag(fieldData.tag)
                .setHint(getString(R.string.enter_email))
                .setRequired(fieldData.required);
//        formItems.add(emailField);
    }

    private void addPhoneField(FieldData fieldData) {
        String label = fieldData.label;
        boolean required = fieldData.required;
        FormElementTextPhone phoneField = FormElementTextPhone
                .createInstance()
                .setTitle(label)
                .setTag(fieldData.tag)
                .setRequired(required);
//        formItems.add(phoneField);
    }

    private void addList(FieldData fieldData) {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < fieldData.list.size(); i++) {
            labels.add(fieldData.list.get(i).name);
        }
        String label;
        if (fieldData.resLabel > 0) {
            label = getString(fieldData.resLabel);
        } else {
            label = fieldData.label;
        }

        if (fieldData.isMultipleSelection) {
            FormElementPickerMulti multipleList = FormElementPickerMulti
                    .createInstance()
                    .setTitle(label)
                    .setOptions(labels)
                    .setTag(fieldData.tag)
                    .setPickerTitle(getString(R.string.pick_option))
                    .setNegativeText(getString(R.string.cancel));
//            formItems.add(multipleList);
        } else {
            FormElementPickerSingle singleList = FormElementPickerSingle
                    .createInstance()
                    .setTitle(label)
                    .setTag(fieldData.tag)
                    .setOptions(labels)
                    .setPickerTitle(getString(R.string.pick_option));
//            formItems.add(singleList);
        }
    }

    private void fileUploadField(FieldData fieldData) {
        List<String> labels = new ArrayList<>();
        labels.add(fieldData.list.get(0).name);

        FormElementPickerSingle singleList = FormElementPickerSingle
                .createInstance()
                .setTitle(fieldData.label)
                .setTag(fieldData.tag)
                .setOptions(labels)
                .setValue(labels.get(0))
                .setPickerTitle(getString(R.string.pick_option));
//        formItems.add(singleList);
    }

    private void formHeader(Integer labelRes) {
        String label = getString(labelRes);
        FormHeader formHeader = FormHeader.createInstance(label);
//        formItems.add(formHeader);
    }

    private void showDialog(DialogMessage dialogMessage) {
        FragmentManager fm = getFragmentManager();
        ValidateFormDialog dialog = ValidateFormDialog.newInstance(
                getString(dialogMessage.title),
                getString(dialogMessage.message),
                dialogMessage.list
        );
        dialog.show(fm, "validate_dialog");
    }

    private void goBack() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    private void refreshForm() {
        RecyclerView.Adapter adapter =
                mBinding.rvFields.getAdapter();
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
    }

    @UiThread
    private void addWorkflowTypeItem(SingleChoiceFormItem singleChoiceFormItem) {
        mAdapter.addItem(singleChoiceFormItem);

        singleChoiceFormItem.setOnSelectedListener(item -> {
            if (item.getValue() == null) {
                viewModel.clearForm();
                return;
            }

            String selection = item.getValue().getName();
            viewModel.generateFieldsByType(selection);
        });
    }

    @UiThread
    private void addItemToForm(BaseFormItem item) {
        mAdapter.addItem(item);
    }

    @UiThread
    private void setItemListToForm(List<BaseFormItem> list) {
        mAdapter.setData(list);
    }

    @UiThread
    private void updateValidationUi(BaseFormItem firstInvalidItem) {
        mAdapter.setHasToEvaluateValid(true);

        if (firstInvalidItem == null) return;
        int firstInvalidPosition = mAdapter.getItemPosition(firstInvalidItem);
        mBinding.rvFields.scrollToPosition(firstInvalidPosition); //todo does not work
    }

}
