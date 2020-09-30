package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.SignatureCustomFieldsFormBinding;
import com.rootnetapp.rootnetintranet.models.responses.signature.FieldCustom;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.signature.adapters.SignatureCustomFieldsAdapter;

import java.util.List;

import javax.inject.Inject;

public class SignatureCustomFieldsForm extends AppCompatActivity {

    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";

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

        viewModel.onStart(worklowListItem);
    }

    private void updateCustomFieldsList(List<FieldCustom> customFields) {
        RecyclerView.LayoutManager manager = binding.customFieldsList.getLayoutManager();
        if (manager == null) {
            binding.customFieldsList.setLayoutManager(new LinearLayoutManager(this));
        }

        binding.customFieldsList.setAdapter(new SignatureCustomFieldsAdapter(customFields));
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
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}