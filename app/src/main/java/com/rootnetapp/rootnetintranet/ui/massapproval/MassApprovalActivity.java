package com.rootnetapp.rootnetintranet.ui.massapproval;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ActivityMassApprovalBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.massapproval.adapters.MassApprovalAdapter;
import com.rootnetapp.rootnetintranet.ui.massapproval.models.StatusApproval;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class MassApprovalActivity extends AppCompatActivity {

    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";

    private static final String TAG = "MassApprovalActivity";

    private MassApprovalAdapter mAdapter;

    @Inject
    MassApprovalViewModelFactory workflowViewModelFactory;
    private MassApprovalViewModel massApprovalViewModel;
    private ActivityMassApprovalBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mass_approval);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        massApprovalViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(MassApprovalViewModel.class);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");

        setOnClickListeners();
        setupRecycler();

        subscribe();

        WorkflowListItem mWorkflowListItem = getIntent()
                .getParcelableExtra(EXTRA_WORKFLOW_LIST_ITEM);

        massApprovalViewModel
                .initWithDetails(token, mWorkflowListItem, loggedUserId, permissionsString);
    }

    /**
     * Method will initialize some UI features using an WorkflowListItem object coming from the user
     * selection in workflow list.
     *
     * @param workflowListItem
     */
    private void initUiWith(WorkflowListItem workflowListItem) {
        setActionBar(workflowListItem);
        massApprovalViewModel.getObservableInitWorkflowUi().removeObservers(this);
    }

    private void setActionBar(WorkflowListItem workflowListItem) {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = workflowListItem.getTitle();
        String subtitle = workflowListItem.getWorkflowTypeKey();
        if (title == null || title.isEmpty()) title = getTitle().toString();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(subtitle);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                showToastMessage(data);
            }
        });

        massApprovalViewModel.getObservableError().observe(this, errorObserver);
        massApprovalViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);
        massApprovalViewModel.getObservableInitWorkflowUi()
                .observe(this, this::initUiWith);
        massApprovalViewModel.getObservableWorkflowTypeVersion()
                .observe(this, this::updateToolbarSubtitleWithWorkflowVersion);
        massApprovalViewModel.getObservablePendingStatusList()
                .observe(this, this::updatePendingStatusList);
        massApprovalViewModel.getObservableShowSubmitButton()
                .observe(this, this::showSubmitButton);
        massApprovalViewModel.getObservableEnableSubmitButton()
                .observe(this, this::enableSubmitButton);
        massApprovalViewModel.getObservableHandleResult()
                .observe(this, this::handleResult);
        massApprovalViewModel.getObservableNoStatuses()
                .observe(this, this::handleNoStatuses);

        massApprovalViewModel.showLoading.observe(this, this::showLoading);
    }

    private void setOnClickListeners() {
        mBinding.btnSubmit.setOnClickListener(view -> processMassApproval());
    }

    /**
     * Initializes the mass approval RecyclerView.
     */
    private void setupRecycler() {
        mBinding.rvStatuses.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvStatuses.setNestedScrollingEnabled(false);
    }

    private void updatePendingStatusList(List<StatusApproval> statuses) {
        mAdapter = new MassApprovalAdapter(massApprovalViewModel.getCurrentWorkflowType(),
                statuses);
        mBinding.rvStatuses.setAdapter(mAdapter);
    }

    private void processMassApproval() {
        massApprovalViewModel.processMassApproval(mAdapter.getDataset());
    }

    private void handleResult(boolean success) {
        if (!success) {
            showToastMessage(R.string.mass_approval_activity_fail);
            return;
        }

        showToastMessage(R.string.mass_approval_activity_fail);

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

    @UiThread
    private void updateToolbarSubtitleWithWorkflowVersion(String versionString) {
        if (getSupportActionBar() == null) return;

        String currentSubtitle = (String) getSupportActionBar().getSubtitle();
        currentSubtitle = String.format(Locale.US, "%s (%s)", currentSubtitle, versionString);
        getSupportActionBar().setSubtitle(currentSubtitle);
    }

    @UiThread
    private void showSubmitButton(boolean show) {
        mBinding.btnSubmit.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void enableSubmitButton(boolean enable) {
        mBinding.btnSubmit.setEnabled(enable);
    }

    @UiThread
    private void handleNoStatuses(boolean ignored) {
        showNoStatusesView(true);
        showSubmitButton(false);
        showRecyclerView(false);
    }

    @UiThread
    private void showNoStatusesView(boolean show) {
        mBinding.tvNoMoreStatus.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void showRecyclerView(boolean show) {
        mBinding.rvStatuses.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}