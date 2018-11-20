package com.rootnetapp.rootnetintranet.ui.workflowdetail;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters.WorkflowDetailViewPagerAdapter;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class WorkflowDetailFragment extends Fragment {

    @Inject
    WorkflowDetailViewModelFactory workflowViewModelFactory;
    WorkflowDetailViewModel workflowDetailViewModel;
    private FragmentWorkflowDetailBinding mBinding;
    private MainActivityInterface mMainActivityInterface;
    private WorkflowListItem mWorkflowListItem;
    private String mToken;

    public WorkflowDetailFragment() {
        // Required empty public constructor
    }

    public static WorkflowDetailFragment newInstance(WorkflowListItem item, MainActivityInterface mainActivityInterface) {
        WorkflowDetailFragment fragment = new WorkflowDetailFragment();
        fragment.mWorkflowListItem = item;
        fragment.mMainActivityInterface = mainActivityInterface;
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
                R.layout.fragment_workflow_detail, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowDetailViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowDetailViewModel.class);

        SharedPreferences prefs = getContext().getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        mToken = "Bearer "+ prefs.getString("token","");
        //todo separate methods for setText
        mBinding.tvWorkflowId.setText(mWorkflowListItem.getWorkflowTypeName()); //todo verify field
        mBinding.tvWorkflowName.setText(mWorkflowListItem.getTitle()); //todo verify field

        setupViewPager();
        subscribe();

        showLoading(true);
        workflowDetailViewModel.initDetails(mToken, mWorkflowListItem);

        return view;
    }

    /**
     * Initializes and set the {@link WorkflowDetailViewPagerAdapter} for the {@link ViewPager}.
     */
    private void setupViewPager() {
        WorkflowDetailViewPagerAdapter adapter = new WorkflowDetailViewPagerAdapter(getContext(), mWorkflowListItem, getChildFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
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
    private void setWorkflowIsOpen(boolean open) {
        if (open) {
            mBinding.tvOpenClosed.setText(getString(R.string.open));
            mBinding.tvOpenClosed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
        } else {
            mBinding.tvOpenClosed.setText(getString(R.string.closed));
            mBinding.tvOpenClosed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));

        }
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            showLoading(false);
            if (null != data) {
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        workflowDetailViewModel.getObservableError().observe(this, errorObserver);
        workflowDetailViewModel.getObservableShowToastMessage().observe(this, this::showToastMessage);

        workflowDetailViewModel.showLoading.observe(this, this::showLoading);
        workflowDetailViewModel.setWorkflowIsOpen.observe(this, this::setWorkflowIsOpen);
    }

    @UiThread
    private void updateHeaderCommentCounter(String count) {
        //todo check interaction with CommentsFragment (the count must come from there)
//        mBinding.detailMessageText.setText(count);
    }
}