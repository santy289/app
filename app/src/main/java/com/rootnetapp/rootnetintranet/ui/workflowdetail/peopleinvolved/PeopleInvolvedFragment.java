package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

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
import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailPeopleInvolvedBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved.adapters.PeopleInvolvedAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PeopleInvolvedFragment extends Fragment {

    @Inject
    PeopleInvolvedViewModelFactory peopleInvolvedViewModelFactory;
    private PeopleInvolvedViewModel peopleInvolvedViewModel;
    private FragmentWorkflowDetailPeopleInvolvedBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private BasePeopleInvolvedFragmentInterface mBasePeopleInvolvedFragmentInterface;

    public PeopleInvolvedFragment() {
        // Required empty public constructor
    }

    public static PeopleInvolvedFragment newInstance(BasePeopleInvolvedFragmentInterface anInterface, WorkflowListItem item) {
        PeopleInvolvedFragment fragment = new PeopleInvolvedFragment();
        fragment.mWorkflowListItem = item;
        fragment.mBasePeopleInvolvedFragmentInterface = anInterface;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_people_involved, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        peopleInvolvedViewModel = ViewModelProviders
                .of(this, peopleInvolvedViewModelFactory)
                .get(PeopleInvolvedViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        String userId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");
        String permissionsString = prefs.getString(PreferenceKeys.PREF_USER_PERMISSIONS, "");

        subscribe();
        setOnClickListeners();

        peopleInvolvedViewModel.initDetails(token, mWorkflowListItem, userId, permissionsString);

        return view;
    }

    private void subscribe() {
        peopleInvolvedViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);

        peopleInvolvedViewModel.showLoading.observe(getViewLifecycleOwner(), this::showLoading);
        peopleInvolvedViewModel.updateProfilesInvolved.observe(getViewLifecycleOwner(), this::updateProfilesInvolved);
        peopleInvolvedViewModel.hideProfilesInvolvedList
                .observe(getViewLifecycleOwner(), this::hideProfilesInvolvedList);
        peopleInvolvedViewModel.showEditButtonLiveData
                .observe(getViewLifecycleOwner(), this::showEditButton);
    }

    private void setOnClickListeners() {
        mBinding.btnEdit.setOnClickListener(v -> {
            mBasePeopleInvolvedFragmentInterface
                    .showFragment(CreateWorkflowFragment.newInstance(mWorkflowListItem), true);
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

    /**
     * Updates the profiles involved.
     *
     * @param profiles List of profiles to display in People Involved recyclerView.
     */
    @UiThread
    private void updateProfilesInvolved(List<ProfileInvolved> profiles) {
        mBinding.rvAllPeopleInvolved.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvAllPeopleInvolved.setAdapter(new PeopleInvolvedAdapter(profiles));
    }

    /**
     * Hides list of people involved and shows a text message instead.
     *
     * @param hide Action to take.
     */
    @UiThread
    private void hideProfilesInvolvedList(boolean hide) {
        if (hide) {
            mBinding.rvAllPeopleInvolved.setVisibility(View.GONE);
            mBinding.noPeopleInvolved.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvAllPeopleInvolved.setVisibility(View.VISIBLE);
            mBinding.noPeopleInvolved.setVisibility(View.GONE);
        }
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showEditButton(boolean show) {
        mBinding.btnEdit.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.viewInformation.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}