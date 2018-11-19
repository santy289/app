package com.rootnetapp.rootnetintranet.ui.workflowdetail.status;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailStatusBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class StatusFragment extends Fragment {

    @Inject
    StatusViewModelFactory statusViewModelFactory;
    StatusViewModel statusViewModel;
    private FragmentWorkflowDetailStatusBinding mBinding;
    private WorkflowListItem mWorkflowItem;
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
        fragment.mWorkflowItem = item;
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

        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        mToken = "Bearer "+ prefs.getString("token","");

        //todo add methods and implementations

        return view;
    }


    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }
}