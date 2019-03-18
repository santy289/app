package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailInformationBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.InformationAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters.StepsAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class InformationFragment extends Fragment {

    @Inject
    InformationViewModelFactory informationViewModelFactory;
    private InformationViewModel informationViewModel;
    private FragmentWorkflowDetailInformationBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private BaseInformationFragmentInterface mBaseInformationFragmentInterface;

    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance(
            BaseInformationFragmentInterface baseInformationFragmentInterface,
            WorkflowListItem item) {
        InformationFragment fragment = new InformationFragment();
        fragment.mBaseInformationFragmentInterface = baseInformationFragmentInterface;
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_information, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        informationViewModel = ViewModelProviders
                .of(this, informationViewModelFactory)
                .get(InformationViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        String userId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        setOnClickListeners();
        subscribe();
        informationViewModel.initDetails(token, userId, permissionsString, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        informationViewModel.getObservableError().observe(getViewLifecycleOwner(), errorObserver);
//
        informationViewModel.showLoading.observe(getViewLifecycleOwner(), this::showLoading);
        informationViewModel.updateInformationListUi.observe(getViewLifecycleOwner(), this::updateInformationListUi);
        informationViewModel.showImportantInfoSection.observe(getViewLifecycleOwner(), this::showImportantInfoSection);
        informationViewModel.loadImportantInfoSection.observe(getViewLifecycleOwner(), this::loadImportantInfoSection);
        informationViewModel.showEditButtonLiveData.observe(getViewLifecycleOwner(), this::showEditButton);
    }

    private void setOnClickListeners() {
        mBinding.btnEdit.setOnClickListener(v -> {
            mBaseInformationFragmentInterface.showFragment(CreateWorkflowFragment.newInstance(mWorkflowListItem), true);
        });
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
    private void updateInformationListUi(List<Information> informationList) {
        mBinding.rvInformation.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvInformation.setAdapter(new InformationAdapter(informationList));
        mBinding.rvInformation.setNestedScrollingEnabled(false);
    }

    @UiThread
    private void showImportantInfoSection(boolean show) {
        if (show) {
            mBinding.tvTitleImportantSteps.setVisibility(View.VISIBLE);
            mBinding.viewImportantSteps.setVisibility(View.VISIBLE);
            mBinding.rvSteps.setVisibility(View.VISIBLE);
            mBinding.tvTitleInformation.setVisibility(View.VISIBLE);
//            mBinding.viewInformation.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvTitleImportantSteps.setVisibility(View.GONE);
            mBinding.viewImportantSteps.setVisibility(View.GONE);
            mBinding.rvSteps.setVisibility(View.GONE);
            mBinding.tvTitleInformation.setVisibility(View.GONE);
//            mBinding.viewInformation.setVisibility(View.GONE);
        }
    }

    @UiThread
    private void loadImportantInfoSection(List<Step> steps) {
        mBinding.rvSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvSteps.setAdapter(new StepsAdapter(steps));
        mBinding.rvSteps.setNestedScrollingEnabled(false);
    }

    @UiThread
    private void showEditButton(boolean show) {
        mBinding.btnEdit.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}