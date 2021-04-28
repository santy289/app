package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowUser;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailInformationBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.Step;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationActivity;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.GeolocationViewModel;
import com.rootnetapp.rootnetintranet.ui.createworkflow.geolocation.SelectedLocation;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.Information;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.adapters.InformationAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.adapters.StepsAdapter;

import java.util.List;

import javax.inject.Inject;

public class InformationFragment extends Fragment implements InformationFragmentInterface {

    @Inject
    InformationViewModelFactory informationViewModelFactory;
    private InformationViewModel informationViewModel;
    private FragmentWorkflowDetailInformationBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private BaseInformationFragmentInterface mBaseInformationFragmentInterface;
    private static final String SAVE_WORKFLOW_TYPE = "SAVE_WORKFLOW_TYPE";

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_WORKFLOW_TYPE, mWorkflowListItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mWorkflowListItem = savedInstanceState.getParcelable(SAVE_WORKFLOW_TYPE);
        }
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
        informationViewModel.getObservableError()
                .observe(getViewLifecycleOwner(), this::showToastMessage);
        informationViewModel.getObservableUpdateOwnerUi()
                .observe(getViewLifecycleOwner(), this::updateOwnerUi);
        informationViewModel.getObservableShowNoConnectionView()
                .observe(getViewLifecycleOwner(), this::showNoConnectionView);

        informationViewModel.showLoading.observe(getViewLifecycleOwner(), this::showLoading);
        informationViewModel.updateInformationListUi
                .observe(getViewLifecycleOwner(), this::updateInformationListUi);
        informationViewModel.showImportantInfoSection
                .observe(getViewLifecycleOwner(), this::showImportantInfoSection);
        informationViewModel.loadImportantInfoSection
                .observe(getViewLifecycleOwner(), this::loadImportantInfoSection);
        informationViewModel.showEditButtonLiveData
                .observe(getViewLifecycleOwner(), this::showEditButton);
    }

    private void setOnClickListeners() {
        mBinding.btnEdit.setOnClickListener(v -> {
            mBaseInformationFragmentInterface
                    .showFragment(CreateWorkflowFragment.newInstance(mWorkflowListItem), true);
        });
    }

    @UiThread
    private void showLoading(boolean show) {
        mBinding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void updateInformationListUi(List<Information> informationList) {
        mBinding.rvInformation.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvInformation.setAdapter(new InformationAdapter(this, informationList));
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
        mBinding.btnEdit.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mBinding.viewInformation.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void updateOwnerUi(WorkflowUser owner) {
        if (owner == null) return;

        if (!TextUtils.isEmpty(owner.getPicture())) {
            String path = Utils.imgDomain + owner.getPicture();
            GlideUrl url = new GlideUrl(path);
            Glide.with(getContext())
                    .load(url.toStringUrl())
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.drawable.default_profile_avatar)
                                    .error(R.drawable.default_profile_avatar)
                    )
                    .into(mBinding.ivOwner);
        }

        mBinding.tvOwnerName.setText(owner.getFullName());

        mBinding.ivOwner.setVisibility(View.VISIBLE);
        mBinding.tvOwnerName.setVisibility(View.VISIBLE);
        mBinding.tvOwnerDescription.setVisibility(View.VISIBLE);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showNoConnectionView(boolean show) {
        mBinding.includeNoConnectionView.lytNoConnectionView
                .setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showLocation(SelectedLocation selectedLocation) {
        if (selectedLocation == null) return;

        Intent intent = new Intent(getActivity(), GeolocationActivity.class);
        intent.putExtra(GeolocationViewModel.EXTRA_SHOW_LOCATION, selectedLocation);
        startActivity(intent);
    }
}