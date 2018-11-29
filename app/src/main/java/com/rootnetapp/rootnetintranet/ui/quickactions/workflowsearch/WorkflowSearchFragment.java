package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowSearchBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickActionsInterface;
import com.rootnetapp.rootnetintranet.ui.quickactions.performaction.PerformActionFragment;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.adapters.WorkflowListAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkflowSearchFragment extends Fragment implements WorkflowSearchFragmentInterface {

    @Inject
    WorkflowSearchViewModelFactory workflowSearchViewModelFactory;
    private WorkflowSearchViewModel workflowSearchViewModel;
    private FragmentWorkflowSearchBinding mBinding;
    private QuickActionsInterface mQuickActionsInterface;
    private @QuickAction int mAction;
    private WorkflowListAdapter mAdapter;

    public WorkflowSearchFragment() {
        // Required empty public constructor
    }

    public static WorkflowSearchFragment newInstance(QuickActionsInterface quickActionsInterface,
                                                     @QuickAction int action) {
        WorkflowSearchFragment fragment = new WorkflowSearchFragment();
        fragment.mQuickActionsInterface = quickActionsInterface;
        fragment.mAction = action;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_search, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        workflowSearchViewModel = ViewModelProviders
                .of(this, workflowSearchViewModelFactory)
                .get(WorkflowSearchViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        workflowSearchViewModel.init(token);
        setupWorkflowRecyclerView();
        setOnClickListeners();
        subscribe();

        return view;
    }

    /**
     * Begins to observe for changes on the LiveData'ss of the ViewModel. The removeObservers()
     * method is to avoid the observable to be created more than once when back stack navigation
     * occurs.
     */
    private void subscribe() {
        workflowSearchViewModel.getObservableWorkflowList().removeObservers(this);
        workflowSearchViewModel.getObservableWorkflowList().observe(this, this::updateAdapterList);

        workflowSearchViewModel.getObservableShowList().removeObservers(this);
        workflowSearchViewModel.getObservableShowList().observe(this, this::showListContent);

        workflowSearchViewModel.getObservableShowToastMessage().removeObservers(this);
        workflowSearchViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);

        workflowSearchViewModel.showLoading.removeObservers(this);
        workflowSearchViewModel.showLoading.observe(this, this::showLoading);

        workflowSearchViewModel.workflowListFromRepo.removeObservers(this);
        workflowSearchViewModel.workflowListFromRepo.observe(this, this::updateAdapterList);

        workflowSearchViewModel.handleShowLoadingByRepo.removeObservers(this);
        workflowSearchViewModel.handleShowLoadingByRepo.observe(this, this::showLoading);
    }

    /**
     * Creates the RecyclerView that will hold the workflow list.
     */
    private void setupWorkflowRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvWorkflows.setLayoutManager(mLayoutManager);
        mAdapter = new WorkflowListAdapter(this);
        mBinding.rvWorkflows.setAdapter(mAdapter);
        // Swipe to refresh recyclerView
//        fragmentWorkflowBinding.swipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * Sets the click listeners for the buttons inside this fragment.
     */
    private void setOnClickListeners() {
        mBinding.btnSearch.setOnClickListener(v -> {
            performSearch();
        });
    }

    /**
     * Executes the search button action by requesting the queried list. If the text query is empty,
     * this will retrieve the recent workflows instead.
     */
    private void performSearch() {
        String query = mBinding.etSearch.getText().toString();

        if (TextUtils.isEmpty(query)) {
            workflowSearchViewModel.getRecentWorkflowList();
            return;
        }

        workflowSearchViewModel.getWorkflowList(query);
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
     * Sets new data to the RecyclerView adapter. For this to work, {@link
     * #setupWorkflowRecyclerView()} must have been called on fragment initialization.
     *
     * @param workflowDbList updated list.
     */
    @UiThread
    private void updateAdapterList(List<WorkflowListItem> workflowDbList) {
        clearSearchText();
        mAdapter.setData(workflowDbList);
    }

    @UiThread
    private void clearSearchText() {
        mBinding.etSearch.setText(null);
    }

    /**
     * Displays the workflow list or an informative label, depending on the parameter.
     *
     * @param show whether to show the list.
     */
    @UiThread
    private void showListContent(boolean show) {
        if (show) {
            mBinding.rvWorkflows.setVisibility(View.VISIBLE);
            mBinding.tvNoResults.setVisibility(View.GONE);
        } else {
            mBinding.rvWorkflows.setVisibility(View.GONE);
            mBinding.tvNoResults.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Instantiates and shows a new fragment that will handle the action for the selected workflow.
     *
     * @param item selected workflow.
     */
    @Override
    public void performAction(WorkflowListItem item) {
        mQuickActionsInterface.showFragment(PerformActionFragment.newInstance(item, mAction), true);
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}