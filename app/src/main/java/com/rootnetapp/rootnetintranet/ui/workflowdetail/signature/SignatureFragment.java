package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.SignatureFragmentBinding;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

public class SignatureFragment extends Fragment {

    @Inject
    SignatureViewModelFactory signatureViewModelFactory;
    private SignatureViewModel signatureViewModel;
    private SignatureFragmentBinding signatureFragmentBinding;
    private AutoCompleteTextView exposedDropdownTemplates;
    private Button actionTemplateButton;

    public static SignatureFragment newInstance() {
        return new SignatureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        signatureViewModel = ViewModelProviders
                .of(this, signatureViewModelFactory)
                .get(SignatureViewModel.class);

        signatureFragmentBinding = DataBindingUtil.inflate(inflater,
                R.layout.signature_fragment,
                container,
                false);

        View view = (View) signatureFragmentBinding.getRoot();
        exposedDropdownTemplates = view.findViewById(R.id.exposed_dropdown_templates);
        actionTemplateButton = view.findViewById(R.id.signature_action_template_button);

        initUi();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initUi() {
        setupObservables();
        signatureViewModel.onStart();
    }

    private void setupObservables() {
        signatureViewModel.getSignatureTemplateState().observe(getViewLifecycleOwner(), this::updateTemplateUi);
    }

    private void updateTemplateUi(SignatureTemplateState state) {
        String[] templates = state.getTemplateMenuItems().toArray(new String[0]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.signature_exposed_menu_item,
                        templates);
        exposedDropdownTemplates.setAdapter(adapter);
        exposedDropdownTemplates.setEnabled(state.isTemplateMenuEnable());
        actionTemplateButton.setText(state.getTemplateActionTitleResId());
        actionTemplateButton.setEnabled(state.isTemplateActionEnable());
    }

}