package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.Toast;

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

public class SignatureFragment extends Fragment implements AdapterView.OnItemClickListener
        , View.OnClickListener{

    private static final int REQUEST_CUSTOM_FORM = 654;
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSIONS = 745;

    private static final String SAVE_WORKFLOW_TYPE = "SAVE_WORKFLOW_TYPE";

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.workflowListItem = savedInstanceState.getParcelable(SAVE_WORKFLOW_TYPE);
        }
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
        initUi(getToken());
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_WORKFLOW_TYPE, workflowListItem);
    }

    private String getToken() {
        if (getActivity() == null) {
            return "";
        }
        SharedPreferences prefs = getActivity()
                .getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        return "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
    }

    private void setupObservablesAndListeners() {
        signatureFragmentBinding.signatureActionTemplateButton.setOnClickListener(v -> {
            signatureViewModel.templateActionClicked();
        });
        signatureFragmentBinding.signaturePdfDownload.setOnClickListener(this);
        signatureFragmentBinding.signaturePdfSignedRequest.setOnClickListener(this);

        signatureViewModel.getSignatureTemplateState().observe(getViewLifecycleOwner(), this::updateTemplateUi);
        signatureViewModel.getSignatureSignerState().observe(getViewLifecycleOwner(), this::updateSignersUi);
        signatureViewModel.getShowLoadingObservable().observe(getViewLifecycleOwner(), this::showLoading);
        signatureViewModel.getDialogBoxStateObservable().observe(getViewLifecycleOwner(), this::showDialogBox);
        signatureViewModel.getGoToCustomFieldFormObservable().observe(getViewLifecycleOwner(), this::goToCustomFieldsForm);
        signatureViewModel.getMenuNameSelectedObservable().observe(getViewLifecycleOwner(), this::selectMenuTemplateName);
        signatureViewModel.getOpenPdfUriObservable().observe(getViewLifecycleOwner(), this::openUriPdf);
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

    private void openUriPdf(Uri uri) {
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
           Toast.makeText(getActivity(), "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
        }
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
            signatureViewModel.handleBackFromCustomFieldForm(true);
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

        RecyclerView.Adapter adapterRecycler = signatureFragmentBinding.signatureSignersList.getAdapter();
        if (adapterRecycler == null) {
            signatureFragmentBinding.signatureSignersList.setAdapter(new SignersListAdapter(signatureSignersState.getSignerItems()));
        } else {
            SignersListAdapter adapter = (SignersListAdapter)adapterRecycler;
            adapter.updateList(signatureSignersState.getSignerItems());
        }

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

    private void selectMenuTemplateName(String selection) {
        signatureFragmentBinding.exposedDropdownTemplates.setText(selection, false);
    }

    /**
     * This is the implementation for AdapterView onItemClick listener used in templates select menu box.
     * Check function updateTemplateUi for usage.
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        signatureViewModel.onItemSelected(position);
    }

    /**
     * This function implements View.OnClickListener for the pdf buttons, check its usage in setupObservablesAndListeners().
     * @param v
     */
    @Override
    public void onClick(View v) {
        boolean hasWritePermissions = checkExternalStoragePermissions();
        if (!hasWritePermissions) {
            requestWritePermission();
        }

        Context context = getContext();
        if (context == null) {
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        switch (v.getId()) {
            case R.id.signature_pdf_download:
                signatureViewModel.pdfDownloadClicked(contentResolver, hasWritePermissions);
                break;
            case R.id.signature_pdf_signed_request:
                signatureViewModel.pdfSignedClicked(contentResolver, hasWritePermissions);
                break;
        }
    }

    private boolean checkExternalStoragePermissions() {
        if (getContext() == null) {
            return false;
        }
        // Here, thisActivity is the current activity
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWritePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_EXTERNAL_STORAGE_PERMISSIONS);
    }

}