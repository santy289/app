package com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBasePeopleInvolvedBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BasePeopleInvolvedFragment extends Fragment implements BasePeopleInvolvedFragmentInterface {

    private FragmentWorkflowDetailBasePeopleInvolvedBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private static final String SAVE_WORKFLOW_TYPE = "SAVE_WORKFLOW_TYPE";

    public BasePeopleInvolvedFragment() {
        // Required empty public constructor
    }

    public static BasePeopleInvolvedFragment newInstance(WorkflowListItem item) {
        BasePeopleInvolvedFragment fragment = new BasePeopleInvolvedFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_base_people_involved, container, false);
        View view = mBinding.getRoot();

        showFragment(PeopleInvolvedFragment.newInstance(this, mWorkflowListItem), true);

        return view;
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
    public void showFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }
}