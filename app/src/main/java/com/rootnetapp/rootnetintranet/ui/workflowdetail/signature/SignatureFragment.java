package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.SignatureFragmentBinding;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.SignersListAdapter;

import javax.inject.Inject;

public class SignatureFragment extends Fragment {

    @Inject
    SignatureViewModelFactory signatureViewModelFactory;
    private SignatureViewModel signatureViewModel;
    private SignatureFragmentBinding signatureFragmentBinding;
    private WorkflowListItem workflowListItem;

    public static SignatureFragment newInstance(WorkflowListItem workflowListItem) {
        SignatureFragment fragment = new SignatureFragment();
        fragment.workflowListItem = workflowListItem;
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

        SharedPreferences prefs = getActivity()
                .getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");

        initUi(token);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        signatureViewModel.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initUi(String token) {
        setupObservables();
        signatureViewModel.onStart(token,
                workflowListItem.workflowTypeId,
                workflowListItem.workflowId);
    }

    private void setupObservables() {
        signatureViewModel.getSignatureTemplateState().observe(getViewLifecycleOwner(), this::updateTemplateUi);
        signatureViewModel.getSignatureSignerState().observe(getViewLifecycleOwner(), this::updateSignersUi);
    }

    private void updateSignersUi(SignatureSignersState signatureSignersState) {
        RecyclerView.LayoutManager manager = signatureFragmentBinding.signatureSignersList.getLayoutManager();
        if (manager == null) {
            signatureFragmentBinding.signatureSignersList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        if (signatureSignersState.isShowMessage()) {
            signatureFragmentBinding.signatureSignersList.setVisibility(View.GONE);
            signatureFragmentBinding.signatureSignersMessage.setText(signatureSignersState.getMessage());
            signatureFragmentBinding.signatureSignersMessage.setVisibility(View.VISIBLE);
            return;
        }

        signatureFragmentBinding.signatureSignersList.setVisibility(View.VISIBLE);
        signatureFragmentBinding.signatureSignersMessage.setText("");
        signatureFragmentBinding.signatureSignersMessage.setVisibility(View.GONE);
        signatureFragmentBinding.signatureSignersList.setAdapter(new SignersListAdapter(signatureSignersState.getSignerItems()));
    }

    private void updateTemplateUi(SignatureTemplateState state) {
        String[] templates = state.getTemplateMenuItems().toArray(new String[0]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.signature_exposed_menu_item,
                        templates);
        signatureFragmentBinding.exposedDropdownTemplates.setAdapter(adapter);
        signatureFragmentBinding.exposedDropdownTemplates.setEnabled(state.isTemplateMenuEnable());
        signatureFragmentBinding.signatureActionTemplateButton.setText(state.getTemplateActionTitleResId());
        signatureFragmentBinding.signatureActionTemplateButton.setEnabled(state.isTemplateActionEnable());
    }

}