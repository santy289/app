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
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailStatusBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Approver;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters.ApproversAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters.PeopleInvolvedAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
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
    private String mToken;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        mToken = "Bearer " + prefs.getString("token", "");

        setOnClickListeners();
        subscribe();

        statusViewModel.initDetails(mToken, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        statusViewModel.getObservableError().observe(this, errorObserver);
        statusViewModel.getObservableShowToastMessage().observe(this, this::showToastMessage);

        statusViewModel.showLoading.observe(this, this::showLoading);
        statusViewModel.updateStatusUi.observe(this, this::updateStatusDetails);
        statusViewModel.updateCurrentApproversList.observe(this, this::updateCurrentApproversList);
        statusViewModel.updateProfilesInvolved.observe(this, this::updateProfilesInvolved);
        statusViewModel.updateApproveSpinner.observe(this, this::updateApproveSpinner);
        statusViewModel.hideApproverListOnEmptyData
                .observe(this, this::hideApproverListOnEmptyData);
        statusViewModel.hideApproveSpinnerOnEmptyData.observe(this, this::hideApproveSpinnerOnEmptyData);
        statusViewModel.hideProfilesInvolvedList.observe(this, this::hideProfilesInvolvedList);
        statusViewModel.updateStatusUiFromUserAction.observe(this, this::updateStatusDetails);
    }

    private void setOnClickListeners() {
        mBinding.includeNextStep.btnApprove.setOnClickListener(v -> approveAction());
        mBinding.includeNextStep.btnReject.setOnClickListener(v -> rejectAction());
        //todo verify action for "Mass Approval"
    }

    /**
     * Click listener function that listens to clicks in the approve button.
     */
    private void approveAction() {
        statusViewModel.handleApproveAction(mApproveSpinnerItemSelection);
    }

    /**
     * Click listener function that listens to clicks in the reject button.
     */
    private void rejectAction() {
        statusViewModel.handleRejectAction(mApproveSpinnerItemSelection);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    private void updateStatusDetails(String[] statusNames) {
        mBinding.includeStatusSummary.tvLastStatus.setText(statusNames[INDEX_LAST_STATUS]);
        mBinding.includeStatusSummary.tvCurrentStatus.setText(statusNames[INDEX_CURRENT_STATUS]);
        mBinding.includeStatusSummary.tvNextStatuses.setText(statusNames[INDEX_NEXT_STATUS]);
    }

    private int mApproveSpinnerItemSelection;

    @UiThread
    private void updateApproveSpinner(List<String> nextStatuses) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                nextStatuses
        );
        mBinding.includeNextStep.spSteps.setAdapter(adapter);

        mBinding.includeNextStep.spSteps
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        mApproveSpinnerItemSelection = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }

    /**
     * Hides the spinner in the case that we don't have any next status. When the spinner is hidden.
     * It will replaced it with a message text view.
     *
     * @param hide Hides or shows the spinner view.
     */
    @UiThread
    private void hideApproveSpinnerOnEmptyData(boolean hide) {
        if (hide) {
            mBinding.includeNextStep.tvNoMoreStatus.setVisibility(View.VISIBLE);
            mBinding.includeNextStep.viewSpinnerBackground.setVisibility(View.GONE);
            mBinding.includeNextStep.spSteps.setVisibility(View.GONE);
            mBinding.includeNextStep.btnApprove.setVisibility(View.GONE);
            mBinding.includeNextStep.btnReject.setVisibility(View.GONE);
        } else {
            mBinding.includeNextStep.tvNoMoreStatus.setVisibility(View.GONE);
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

    /**
     * Updates the profiles involved.
     *
     * @param profiles List of profiles to display in People Involved recyclerView.
     */
    @UiThread
    private void updateProfilesInvolved(List<ProfileInvolved> profiles) {
        mBinding.includeAllPeopleInvolved.rvAllPeopleInvolved
                .setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.includeAllPeopleInvolved.rvAllPeopleInvolved
                .setAdapter(new PeopleInvolvedAdapter(profiles));
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
     * Hides list of people involved and shows a text message instead.
     *
     * @param hide Action to take.
     */
    @UiThread
    private void hideProfilesInvolvedList(boolean hide) {
        if (hide) {
            mBinding.includeAllPeopleInvolved.rvAllPeopleInvolved.setVisibility(View.GONE);
            mBinding.includeAllPeopleInvolved.noPeopleInvolved.setVisibility(View.VISIBLE);
        } else {
            mBinding.includeAllPeopleInvolved.rvAllPeopleInvolved.setVisibility(View.VISIBLE);
            mBinding.includeAllPeopleInvolved.noPeopleInvolved.setVisibility(View.GONE);
        }
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}