package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailStatusBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters.ApproversAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class StatusFragment extends Fragment {

    @Inject
    StatusViewModelFactory statusViewModelFactory;
    private StatusViewModel statusViewModel;
    private FragmentWorkflowDetailStatusBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    // Used for updating Status info.
    protected static final int INDEX_LAST_STATUS = 0;
    protected static final int INDEX_CURRENT_STATUS = 1;
    protected static final int INDEX_NEXT_STATUS = 2;

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(WorkflowListItem item) {
        StatusFragment fragment = new StatusFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_status, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        statusViewModel = ViewModelProviders
                .of(this, statusViewModelFactory)
                .get(StatusViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setOnClickListeners();
        subscribe();

        statusViewModel.initDetails(token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        statusViewModel.getObservableError().observe(getViewLifecycleOwner(), errorObserver);
        statusViewModel.getObservableShowToastMessage()
                .observe(getViewLifecycleOwner(), this::showToastMessage);
        statusViewModel.getObservableTieStatus()
                .observe(getViewLifecycleOwner(), this::showTieStatusLabel);
        statusViewModel.getObservableEnableApproveRejectButtons()
                .observe(getViewLifecycleOwner(), this::enableApproveRejectButtons);

        statusViewModel.showLoading.observe(getViewLifecycleOwner(), this::showLoading);
        statusViewModel.handleShowLoadingByRepo.observe(getViewLifecycleOwner(), this::showLoading);
        statusViewModel.updateStatusUi.observe(getViewLifecycleOwner(), this::updateStatusDetails);
        statusViewModel.updateCurrentApproversList
                .observe(getViewLifecycleOwner(), this::updateCurrentApproversList);
        statusViewModel.hideApproverListOnEmptyData
                .observe(getViewLifecycleOwner(), this::hideApproverListOnEmptyData);
        statusViewModel.updateApproveSpinner
                .observe(getViewLifecycleOwner(), this::updateApproveSpinner);
        statusViewModel.hideApproveSpinnerOnEmptyData
                .observe(getViewLifecycleOwner(), this::hideApproveSpinnerOnEmptyData);
        statusViewModel.hideApproveSpinnerOnNotApprover
                .observe(getViewLifecycleOwner(), this::hideApproveSpinnerOnNotApprover);
        statusViewModel.updateStatusUiFromUserAction
                .observe(getViewLifecycleOwner(), this::updateStatusDetails);
        statusViewModel.updateActiveStatusFromUserAction
                .observe(getViewLifecycleOwner(), this::updateWorkflowStatus);
        statusViewModel.handleSetWorkflowIsOpenByRepo
                .observe(getViewLifecycleOwner(), this::updateWorkflowStatus);
        statusViewModel.setWorkflowIsOpen
                .observe(getViewLifecycleOwner(), this::updateWorkflowStatus);
    }

    private void setOnClickListeners() {
        mBinding.includeNextStep.btnApprove.setOnClickListener(v -> approveAction());
        mBinding.includeNextStep.btnReject.setOnClickListener(v -> rejectAction());
    }

    /**
     * Click listener function that listens to clicks in the approve button.
     */
    private void approveAction() {
        statusViewModel.handleApproveAction();
    }

    /**
     * Click listener function that listens to clicks in the reject button.
     */
    private void rejectAction() {
        statusViewModel.handleRejectAction();
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    /**
     * Enables or disabled the approve and reject buttons. This is used whenever a user makes a
     * request to approve/reject a status to disable the buttons. When said request is done, the
     * buttons should be enabled again. This avoids multiple undesired calls in a row.
     *
     * @param enable true - enable both buttons; false - disable both buttons.
     */
    @UiThread
    private void enableApproveRejectButtons(boolean enable) {
        mBinding.includeNextStep.btnApprove.setEnabled(enable);
        mBinding.includeNextStep.btnReject.setEnabled(enable);
    }

    @UiThread
    private void updateStatusDetails(String[] statusNames) {
        mBinding.includeStatusSummary.tvLastStatus.setText(statusNames[INDEX_LAST_STATUS]);
        mBinding.includeStatusSummary.tvCurrentStatus.setText(statusNames[INDEX_CURRENT_STATUS]);

        String nextStatus = statusNames[INDEX_NEXT_STATUS];
        if (nextStatus == null || nextStatus.isEmpty()) {
            nextStatus = getString(R.string.no_more_status);
        }
        mBinding.includeStatusSummary.tvNextStatuses.setText(nextStatus);
    }

    @UiThread
    private void updateApproveSpinner(List<String> nextStatuses) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (nextStatuses == null || nextStatuses.size() == 0) return;

        String hint = getString(R.string.workflow_detail_status_fragment_spinner_title);
        // check whether the hint has already been added
        if (!nextStatuses.get(0).equals(hint)) {
            // add hint as first item
            nextStatuses.add(0, hint);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                nextStatuses
        );
        mBinding.includeNextStep.spSteps.setAdapter(adapter);

        // listener to keep track of selected item
        mBinding.includeNextStep.spSteps
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {

                        Integer stepIndex;

                        //account for hint as the first item
                        if (position == 0) {
                            stepIndex = null; //null gets ignored by API call
                        } else {
                            stepIndex = position - 1;
                        }

                        statusViewModel.setApproveSpinnerItemSelection(stepIndex);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }

    /**
     * Hides the spinner in the case that we don't have any next status. When the spinner is hidden,
     * it will be replaced it with a message text view.
     *
     * @param hide Hides or shows the spinner view.
     */
    @UiThread
    private void hideApproveSpinnerOnEmptyData(boolean hide) {
        hideApproveSpinner(hide);
        mBinding.includeNextStep.tvUserNotApprover.setVisibility(View.INVISIBLE);

        if (hide) {
            mBinding.includeNextStep.tvNoMoreStatus.setVisibility(View.VISIBLE);
        } else {
            mBinding.includeNextStep.tvNoMoreStatus.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Hides the spinner in the case that the user is not an approver. When the spinner is hidden,
     * it will be replaced it with a message text view.
     *
     * @param hide Hides or shows the spinner view.
     */
    @UiThread
    private void hideApproveSpinnerOnNotApprover(boolean hide) {
        hideApproveSpinner(hide);
        mBinding.includeNextStep.tvNoMoreStatus.setVisibility(View.INVISIBLE);

        if (hide) {
            mBinding.includeNextStep.tvUserNotApprover.setVisibility(View.VISIBLE);
        } else {
            mBinding.includeNextStep.tvUserNotApprover.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Hides or shows the approve spinner and buttons.
     *
     * @param hide Hides or shows the spinner view.
     */
    private void hideApproveSpinner(boolean hide) {
        if (hide) {
            mBinding.includeNextStep.viewSpinnerBackground.setVisibility(View.GONE);
            mBinding.includeNextStep.spSteps.setVisibility(View.GONE);
            mBinding.includeNextStep.btnApprove.setVisibility(View.GONE);
            mBinding.includeNextStep.btnReject.setVisibility(View.GONE);
        } else {
            mBinding.includeNextStep.viewSpinnerBackground.setVisibility(View.VISIBLE);
            mBinding.includeNextStep.spSteps.setVisibility(View.VISIBLE);
            mBinding.includeNextStep.btnApprove.setVisibility(View.VISIBLE);
            mBinding.includeNextStep.btnReject.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the profile involve section. Profiles will include the ones coming from the workflow
     * type configuration, and also profiles coming from specific status configuration that are
     * coming from the current workflow configurations.
     *
     * @param currentApprovers List of current approvers to be displayed.
     */
    @UiThread
    private void updateCurrentApproversList(List<Approver> currentApprovers) {
        mBinding.includeNextStep.rvApprovers
                .setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.includeNextStep.rvApprovers.setAdapter(new ApproversAdapter(currentApprovers));
    }

    @UiThread
    private void hideApproverListOnEmptyData(boolean hide) {
        if (hide) {
            mBinding.includeNextStep.tvTitleApprovers.setVisibility(View.GONE);
            mBinding.includeNextStep.rvApprovers.setVisibility(View.GONE);
            mBinding.includeNextStep.btnMassApproval.setVisibility(View.GONE);
        } else {
            mBinding.includeNextStep.tvTitleApprovers.setVisibility(View.GONE);
            mBinding.includeNextStep.rvApprovers.setVisibility(View.GONE);
            mBinding.includeNextStep.btnMassApproval.setVisibility(View.GONE);
        }
    }

    /**
     * The TIED label should be shown if the current status is tied.
     *
     * @param show whether to show the label.
     */
    @UiThread
    private void showTieStatusLabel(boolean show) {
        mBinding.includeStatusSummary.tvTiedStatus.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Changes the UI state (text and color) of the selected status. This is called after the API
     * request is completed.
     *
     * @param statusUiData object that contains the current state of the status UI.
     */
    @UiThread
    private void updateWorkflowStatus(StatusUiData statusUiData) {
        mBinding.tvStatus.setVisibility(View.VISIBLE);
        mBinding.tvStatus.setText(statusUiData.getSelectedText());
        mBinding.tvStatus.setTextColor(
                ContextCompat.getColor(getContext(), statusUiData.getSelectedColor()));
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}