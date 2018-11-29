package com.rootnetapp.rootnetintranet.ui.quickactions.performaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentPerformActionBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class PerformActionFragment extends Fragment {

    @Inject
    PerformActionViewModelFactory performActionViewModelFactory;
    private PerformActionViewModel performActionViewModel;
    private FragmentPerformActionBinding mBinding;
    private @QuickAction int mAction;
    private WorkflowListItem mWorkflowListItem;

    public PerformActionFragment() {
        // Required empty public constructor
    }

    public static PerformActionFragment newInstance(WorkflowListItem mWorkflowListItem,
                                                    @QuickAction int action) {
        PerformActionFragment fragment = new PerformActionFragment();
        fragment.mWorkflowListItem = mWorkflowListItem;
        fragment.mAction = action;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_perform_action, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        performActionViewModel = ViewModelProviders
                .of(this, performActionViewModelFactory)
                .get(PerformActionViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setWorkflowUI();
        showActionFragment();

        return view;
    }

    private void showActionFragment() {
        switch (mAction) {
            case QuickAction.EDIT_WORKFLOW:
                //todo add fragment
                break;
            case QuickAction.APPROVE_WORKFLOW:
                showFragment(StatusFragment.newInstance(mWorkflowListItem), true);
                break;
            case QuickAction.CHANGE_STATUS:
                //todo add fragment
                break;
            case QuickAction.COMMENT:
                showFragment(CommentsFragment.newInstance(mWorkflowListItem, false), true);
                break;
        }
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
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

    @UiThread
    private void setWorkflowUI(){
        String mainTitle = mWorkflowListItem.getTitle() + " - " + mWorkflowListItem.getWorkflowTypeKey();
        mBinding.tvTitle.setText(mainTitle);


        mBinding.tvType.setText(mWorkflowListItem.getWorkflowTypeName());

        Context context = mBinding.tvStatus.getContext();
        if (mWorkflowListItem.isStatus()) {
            mBinding.tvStatus.setText(context.getString(R.string.open));
        } else {
            mBinding.tvStatus.setText(context.getString(R.string.closed));
        }
    }
}