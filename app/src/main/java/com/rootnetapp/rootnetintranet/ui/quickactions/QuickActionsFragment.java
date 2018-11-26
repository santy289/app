package com.rootnetapp.rootnetintranet.ui.quickactions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailApprovalHistoryBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ApproverHistory;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.adapters.ApprovalHistoryAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class QuickActionsFragment extends Fragment {

    @Inject
    QuickActionsViewModelFactory quickActionsViewModelFactory;
    private QuickActionsViewModel quickActionsViewModel;
    private FragmentWorkflowDetailApprovalHistoryBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    public QuickActionsFragment() {
        // Required empty public constructor
    }

    public static QuickActionsFragment newInstance(WorkflowListItem item) {
        QuickActionsFragment fragment = new QuickActionsFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_approval_history, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        quickActionsViewModel = ViewModelProviders
                .of(this, quickActionsViewModelFactory)
                .get(QuickActionsViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        subscribe();
        quickActionsViewModel.initDetails(token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        quickActionsViewModel.getObservableError().observe(this, errorObserver);

        quickActionsViewModel.showLoading.observe(this, this::showLoading);
        quickActionsViewModel.updateApprovalHistoryList.observe(this, this::updateApprovalHistoryList);
        quickActionsViewModel.hideHistoryApprovalList.observe(this, this::hideHistoryApprovalList);
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
    private void updateApprovalHistoryList(List<ApproverHistory> approverHistoryList) {
        mBinding.rvApprovalHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvApprovalHistory.setAdapter(new ApprovalHistoryAdapter(approverHistoryList));
    }

    /**
     * Shows the approval history list.
     * @param hide
     *  Boolean that decides if we are showing this list or not.
     */
    @UiThread
    private void hideHistoryApprovalList(boolean hide) {
        if (hide) {
            mBinding.rvApprovalHistory.setVisibility(View.GONE);
            mBinding.noHistory.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvApprovalHistory.setVisibility(View.VISIBLE);
            mBinding.noHistory.setVisibility(View.GONE);
        }
    }
}