package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.SignatureFragmentBinding;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldShared;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureSignersState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureTemplateState;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.SignersListAdapter;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class SignatureFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final int REQUEST_CUSTOM_FORM = 654;

    @Inject
    SignatureViewModelFactory signatureViewModelFactory;
    private SignatureViewModel signatureViewModel;
    private SignatureFragmentBinding signatureFragmentBinding;
    private WorkflowListItem workflowListItem;

    public static SignatureFragment newInstance(WorkflowListItem workflowListItem) {
        SignatureFragment fragment = new SignatureFragment();
        fragment.workflowListItem = workflowListItem;
        return fragment;
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

    /**
     * Starting point for UI content update
     * @param token
     */
    private void initUi(String token) {
        setupObservablesAndListeners();
        signatureViewModel.onStart(token,
                workflowListItem.workflowTypeId,
                workflowListItem.workflowId);
    }

    private void setupObservablesAndListeners() {
        signatureFragmentBinding.signatureActionTemplateButton.setOnClickListener(v -> {
            signatureViewModel.templateActionClicked();
        });

        signatureViewModel.getSignatureTemplateState().observe(getViewLifecycleOwner(), this::updateTemplateUi);
        signatureViewModel.getSignatureSignerState().observe(getViewLifecycleOwner(), this::updateSignersUi);
        signatureViewModel.getShowLoadingObservable().observe(getViewLifecycleOwner(), this::showLoading);
        signatureViewModel.getDialogBoxStateObservable().observe(getViewLifecycleOwner(), this::showDialogBox);
        signatureViewModel.getGoToCustomFieldFormObservable().observe(getViewLifecycleOwner(), this::goToCustomFieldsForm);
    }

    @UiThread
    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    private void showDialogBox(DialogBoxState state) {
        if (getContext() == null) {
            return;
        }
        Resources res = getResources();

        if (state.isShowNegative()) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(res.getString(state.getTitle()))
                    .setMessage(res.getString(state.getMessage()))
                    .setNegativeButton(state.getNegative(), null)
                    .setPositiveButton(state.getPositive(), ( dialog, which) -> {
                        signatureViewModel.dialogPositive(state.getMessage());
                    })
                    .show();
            return;
        }
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(res.getString(state.getTitle()))
                .setMessage(res.getString(state.getMessage()))
                .setPositiveButton(state.getPositive(), ( dialog, which) -> {
                    signatureViewModel.dialogPositive(state.getMessage());
                })
                .show();
    }

    private void goToCustomFieldsForm(SignatureCustomFieldShared shared) {
        workflowListItem.customFieldsJsonConfig = shared.jsonFieldConfig;
        Intent intent = new Intent(getActivity(), SignatureCustomFieldsForm.class);
        intent.putExtra(SignatureCustomFieldsForm.EXTRA_WORKFLOW_LIST_ITEM, workflowListItem);
        intent.putExtra(SignatureCustomFieldsForm.EXTRA_TEMPLATE_SELECTED_ID, shared.templateId);
        startActivityForResult(intent, REQUEST_CUSTOM_FORM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CUSTOM_FORM && resultCode == RESULT_OK) {
            // TODO do something try again to initialize form
            // AGAIN attempt to make a network call

            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This function updates the list of signers for each template. This is list comes from the dataase
     * and the selection made using the select box of the UI.
     *
     * @param signatureSignersState
     */
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

    /**
     * This function updates the UI for templates. It expects a value for the string array, if null
     * is passed it will not do anything in therms of the menu items in the select box. This is
     * done just in case we want to update the buttons and not the menu items.
     * @param state
     */
    private void updateTemplateUi(SignatureTemplateState state) {
        if (state.getTemplateMenuItems() != null && getContext() != null) {
            String[] templates = state.getTemplateMenuItems().toArray(new String[0]);
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            getContext(),
                            R.layout.signature_exposed_menu_item,
                            templates);
            signatureFragmentBinding.exposedDropdownTemplates.setAdapter(adapter);
            signatureFragmentBinding.exposedDropdownTemplates.setOnItemClickListener(this);
        }
        signatureFragmentBinding.exposedDropdownTemplates.setEnabled(state.isTemplateMenuEnable());
        signatureFragmentBinding.signatureActionTemplateButton.setText(state.getTemplateActionTitleResId());
        signatureFragmentBinding.signatureActionTemplateButton.setEnabled(state.isTemplateActionEnable());
    }

    /**
     * This is the implementation for AdapterView onItemClick listener used in templates select menu box.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        signatureViewModel.onItemSelected(position);
    }
}