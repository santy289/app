package com.rootnetapp.rootnetintranet.ui.workflowdetail.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBaseInformationBinding;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BaseInformationFragment extends Fragment implements BaseInformationFragmentInterface {

    private FragmentWorkflowDetailBaseInformationBinding mBinding;
    private WorkflowListItem mWorkflowListItem;

    public BaseInformationFragment() {
        // Required empty public constructor
    }

    public static BaseInformationFragment newInstance(WorkflowListItem item) {
        BaseInformationFragment fragment = new BaseInformationFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_base_information, container, false);
        View view = mBinding.getRoot();

        showFragment(InformationFragment.newInstance(this, mWorkflowListItem), true);

        return view;
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