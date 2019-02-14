package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowSearchBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickActionsInterface;
import com.rootnetapp.rootnetintranet.ui.quickactions.changestatus.ChangeStatusActivity;
import com.rootnetapp.rootnetintranet.ui.quickactions.performaction.PerformActionFragment;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.adapters.WorkflowListAdapter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    private BottomSheetBehavior mBottomSheetBehavior;

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
        setupBottomSheet();
        workflowSearchViewModel = ViewModelProviders
                .of(this, workflowSearchViewModelFactory)
                .get(WorkflowSearchViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        setupWorkflowRecyclerView();
        workflowSearchViewModel.init(token);
        setOnClickListeners();
        subscribe();

        return view;
    }

    private void setupBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.includeBottomSheet.bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Begins to observe for changes on the LiveData'ss of the ViewModel. The removeObservers()
     * method is to avoid the observable to be created more than once when back stack navigation
     * occurs.
     */
    private void subscribe() {
        workflowSearchViewModel.getObservableShowList().removeObservers(this);
        workflowSearchViewModel.getObservableShowList().observe(this, this::showListContent);

        workflowSearchViewModel.getObservableShowToastMessage().removeObservers(this);
        workflowSearchViewModel.getObservableShowToastMessage()
                .observe(this, this::showToastMessage);

        workflowSearchViewModel.showLoading.removeObservers(this);
        workflowSearchViewModel.showLoading.observe(this, this::showLoading);

        workflowSearchViewModel.showBottomSheetLoading.removeObservers(this);
        workflowSearchViewModel.showBottomSheetLoading.observe(this, this::showBottomSheetLoading);

        final Observer<PagedList<WorkflowListItem>> updateWithSortedListObserver = (this::updateAdapterList);

        workflowSearchViewModel.handleShowLoadingByRepo.removeObservers(this);
        workflowSearchViewModel.handleShowLoadingByRepo.observe(this, this::showLoading);

        workflowSearchViewModel
                .getObservableUpdateWithSortedList()
                .observe(this, updateWithSortedListObserver);

        workflowSearchViewModel.getObservableMessageViewSetLoadingMore().removeObservers(this);
        workflowSearchViewModel.getObservableMessageViewSetLoadingMore().observe(this, this::setLoadingMoreObservers);

        workflowSearchViewModel.getObservableMessageViewSetQueryLoadingMore().removeObservers(this);
        workflowSearchViewModel.getObservableMessageViewSetQueryLoadingMore().observe(this, this::setLoadingMoreObserversForQuery);

        workflowSearchViewModel.getObservableHandleUiLoadingCompleted().removeObservers(this);
        workflowSearchViewModel.getObservableHandleUiLoadingCompleted().observe(this, this::showBottomSheetLoading);

        workflowSearchViewModel.getObservableMessageUiResetListDataSource().removeObservers(this);
        workflowSearchViewModel.getObservableMessageUiResetListDataSource().observe(this, result -> addWorkflowsObserver());

        //addWorkflowsObserver();
    }

    private void setLoadingMoreObservers(Boolean setObserver) {
        workflowSearchViewModel.getObservableFromRepoLoadingMoreCallback().removeObservers(this);
        workflowSearchViewModel.getObservableFromRepoLoadingMoreCallback().observe(this, this::showBottomSheetLoading);
    }

    private void setLoadingMoreObserversForQuery(Boolean setObserver) {
        workflowSearchViewModel.getObservableFromqueryLoadingMorecallback().removeObservers(this);
        workflowSearchViewModel.getObservableFromqueryLoadingMorecallback().observe(this, this::showBottomSheetLoading);
    }

    /**
     * Creates the RecyclerView that will hold the workflow list.
     */
    private void setupWorkflowRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.rvWorkflows.setLayoutManager(layoutManager);
        mBinding.rvWorkflows.setNestedScrollingEnabled(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        itemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        mBinding.rvWorkflows.addItemDecoration(itemDecoration);

        mAdapter = new WorkflowListAdapter(this);
        mBinding.rvWorkflows.setAdapter(mAdapter);
        mBinding.rvWorkflows.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the click listeners for the buttons inside this fragment.
     */
    private void setOnClickListeners() {
        mBinding.btnSearch.setOnClickListener(v -> performSearch());
    }

    /**
     * Executes the search button action by requesting the queried list.
     */
    private void performSearch() {
        // TODO make it work with new Adapter implementation. Still pending this work.
        //clearAdapterList();

        String query = mBinding.etSearch.getText().toString();
        workflowSearchViewModel.performSearch(query);
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
     * Adds new data to the RecyclerView adapter. For this to work, {@link
     * #setupWorkflowRecyclerView()} must have been called on fragment initialization.
     *
     * @param workflowDbList updated list.
     */
    @UiThread
    private void updateAdapterList(PagedList<WorkflowListItem> workflowDbList) {
        clearSearchText(); // TODO check what we have here in this list.
        mAdapter.submitList(workflowDbList);
    }

    @UiThread
    private void clearSearchText() {
        mBinding.etSearch.clearFocus();
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
        if (mAction == QuickAction.CHANGE_STATUS) {
            Intent intent = new Intent(getActivity(), ChangeStatusActivity.class);
            intent.putExtra(ChangeStatusActivity.EXTRA_WORKFLOW_LIST_ITEM, item);
            mQuickActionsInterface.showActivity(intent);
            return;
        }

        mQuickActionsInterface.showFragment(PerformActionFragment.newInstance(item, mAction), true);
    }

    /**
     * Used when we have a general workflow.
     */
    final Observer<PagedList<WorkflowListItem>> getAllWorkflowsObserver = (listWorkflows -> {
//        fragmentWorkflowBinding.swipeRefreshLayout.setRefreshing(false);
        if (mAdapter == null) {
            return;
        }

        workflowSearchViewModel.handleUiAndIncomingList(listWorkflows);
    });

    /**
     * Method is used when we initialize our list of workflows, and also when we reset the
     * DataSource for the recycler view. The ViewModel will call this method any time a new
     * DataSource is initialized.
     */
    private void addWorkflowsObserver() {
        workflowSearchViewModel.getAllWorkflows().removeObservers(this);
        workflowSearchViewModel.getAllWorkflows().observe(this, getAllWorkflowsObserver);
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
    private void showBottomSheetLoading(Boolean show) {
        if (show) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}