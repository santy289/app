package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldsFormBinding;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;
import com.rootnetapp.rootnetintranet.models.ui.general.DialogBoxState;
import com.rootnetapp.rootnetintranet.models.ui.signature.SignatureCustomFieldFormState;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.CustomFieldUserSection;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.SignatureCustomFieldsAdapter;


import java.util.List;

import javax.inject.Inject;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.compat.SectionedRecyclerViewAdapterV2Compat;


public class SignatureCustomFieldsForm extends AppCompatActivity implements ViewTextUpdate {
    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";
    public static final String EXTRA_TEMPLATE_SELECTED_ID = "Extra.SignatureTemplateId";
    public static final int TEMPLATE_ID_NOT_FOUND = 99999;

    @Inject
    SignatureCustomFieldsViewModelFactory viewModelFactory;
    private SignatureCustomFieldsViewModel viewModel;
    private SignatureCustomFieldsFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.signature_custom_fields_form);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SignatureCustomFieldsViewModel.class);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WorkflowListItem worklowListItem = getIntent()
                .getParcelableExtra(SignatureCustomFieldsForm.EXTRA_WORKFLOW_LIST_ITEM);
        int templateId = getIntent()
                .getIntExtra(EXTRA_TEMPLATE_SELECTED_ID, TEMPLATE_ID_NOT_FOUND);

        setupObservablesAndListeners();
        viewModel.onStart(worklowListItem, templateId, getToken());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        return "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
    }

    private void setupObservablesAndListeners() {
        binding.customFieldsSave.setOnClickListener(v -> {
            viewModel.onActionSave();
        });

        viewModel.getShowLoadingObservable().observe(this, this::showLoading);
        viewModel.getDialogBoxStateObservable().observe(this, this::showDialogBox);
        viewModel.getFieldCustomObservable().observe(this, this::updateCustomFieldsList);
        viewModel.getSuccessGoBackObservable().observe(this, this::handleResult);
    }

    SectionedRecyclerViewAdapter sectionAdapter;

    /**
     * Updates the fields in the form. If the adapter was already created we only need to notify
     * the change to the adapter. We are caching in the view model the data structure, thus we
     * can update it from the view model and the adapter will already have the latest version.
     *
     * @param states
     */
    private void updateCustomFieldsList(List<SignatureCustomFieldFormState> states) {
        RecyclerView.LayoutManager manager = binding.customFieldsList.getLayoutManager();
        if (manager == null) {
            binding.customFieldsList.setLayoutManager(new LinearLayoutManager(this));
        }

        if (states == null) {
            // TODO consider an error message?
            return;
        }

        if (binding.customFieldsList.getAdapter() == null) {
            sectionAdapter = new SectionedRecyclerViewAdapter();
            for (SignatureCustomFieldFormState state : states) {
                CustomFieldUserSection section = new CustomFieldUserSection(state, this);
                sectionAdapter.addSection(section);
            }
            binding.customFieldsList.setAdapter(sectionAdapter);
        } else {
            binding.customFieldsList.getAdapter().notifyDataSetChanged();
        }

    }

    @Override
    public void onItemUpdate(@NonNull CustomFieldUserSection section, int itemAdapterPosition, String newValue) {
        if (sectionAdapter == null) {
            return;
        }
        int position = sectionAdapter.getPositionInSection(itemAdapterPosition);
        FieldCustom fieldCustom = section.getItem(position);
        fieldCustom.setCustomValue(newValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @UiThread
    private void handleResult(boolean success) {
        if (!success) {
            return;
        }

        setResult(RESULT_OK);
        finish();
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    private void showDialogBox(DialogBoxState state) {
        Resources res = getResources();

        if (state.isShowNegative()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(res.getString(state.getTitle()))
                    .setMessage(res.getString(state.getMessage()))
                    .setNegativeButton(state.getNegative(), null)
                    .setPositiveButton(state.getPositive(), ( dialog, which) -> {
                        viewModel.dialogPositive(state.getMessage());
                    })
                    .show();
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(res.getString(state.getTitle()))
                .setMessage(res.getString(state.getMessage()))
                .setPositiveButton(state.getPositive(), ( dialog, which) -> {
                    viewModel.dialogPositive(state.getMessage());
                })
                .show();
    }


}