package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementPickerSingle;
import me.riddhimanadib.formmaster.model.FormElementTextEmail;
import me.riddhimanadib.formmaster.model.FormElementTextSingleLine;
import me.riddhimanadib.formmaster.model.FormHeader;


public class WorkFlowCreateFragment extends Fragment {

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

        formBuilder = new FormBuilder(getContext(), fragmentCreateWorkflowBinding.recCreateWorkflow);
        subscribe();
        viewModel.initForm(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCleared();
    }

    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void addWorkflowTypeList(List<String> types) {
        FormElementPickerSingle workflowTypes = FormElementPickerSingle
                .createInstance()
                .setTitle(getContext().getString(R.string.form_workflow_type))
                .setOptions(types).setPickerTitle(getContext().getString(R.string.pick_option));

        workflowTypes.setValue(types.get(0));
        formItems.add(workflowTypes);
    }

    private void addTexField(String label) {
        FormElementTextSingleLine title = FormElementTextSingleLine.createInstance().setTitle(label);
        formItems.add(title);
    }

    private void buildForm() {
        formBuilder.addFormElements(formItems);
    }

    private void subscribe() {
        final Observer<Boolean> showLoadingObserver = (this::showLoading);
        viewModel.getObservableShowLoading().observe(this, showLoadingObserver);

        final Observer<List<String>> setTypeList = (this::addWorkflowTypeList);
        viewModel.setTypeList.observe(this, setTypeList);

        viewModel.buildForm.observe(this, ( build -> {
            buildForm();
        }));
    }

}
