package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowBinding;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.RightDrawerSortSwitchAction;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityViewModel;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.WorkflowExpandableAdapter;

import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowViewModel.REQUEST_WORKFLOW_DETAIL;

public class WorkflowFragment extends Fragment implements WorkflowFragmentInterface,
        SwipeRefreshLayout.OnRefreshListener {

    @Inject
    WorkflowViewModelFactory workflowViewModelFactory;
    WorkflowViewModel workflowViewModel;
    private FragmentWorkflowBinding fragmentWorkflowBinding;
    private MainActivityInterface mainActivityInterface;
    private WorkflowExpandableAdapter adapter;
    private BottomSheetBehavior bottomSheetBehavior;

    public static final int SWITCH_NUMBER = 500;
    public static final int SWITCH_CREATED_DATE = 501;
    public static final int SWITCH_UPDATED_DATE = 502;
    public static final int RADIO_NUMBER = 600;
    public static final int RADIO_CREATED_DATE = 601;
    public static final int RADIO_UPDATED_DATE = 602;
    public static final int RADIO_CLEAR_ALL = 603;
    public static final int SWITCH_PENDING = 700;
    public static final int SWITCH_STATUS = 701;
    public static final int SELECT_TYPE = 702;

    public static final int CHECK = 11;
    public static final int UNCHECK = 10;
    public static final int INDEX_TYPE = 0;
    public static final int INDEX_CHECK = 1;

    MainActivityViewModel mainViewModel;

    // Used when we have a general workflow.
    final Observer<PagedList<WorkflowListItem>> getAllWorkflowsObserver = (listWorkflows -> {
        fragmentWorkflowBinding.swipeRefreshLayout.setRefreshing(false);
        if (adapter == null) {
            return;
        }
        workflowViewModel.handleUiAndIncomingList(listWorkflows);
    });

    private static final String TAG = "WorkflowFragment";

    public WorkflowFragment() {
        // Required empty public constructor
    }

    public static WorkflowFragment newInstance(MainActivityInterface mainActivityInterface) {
        WorkflowFragment fragment = new WorkflowFragment();
        fragment.mainActivityInterface = mainActivityInterface;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentWorkflowBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow, container, false);
        View view = (View) fragmentWorkflowBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        LinearLayout bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        boolean firstLoad = false;
        if (workflowViewModel == null) {
            firstLoad = true;
        }
        workflowViewModel = ViewModelProviders
                .of(this, workflowViewModelFactory)
                .get(WorkflowViewModel.class);

        mainViewModel = ViewModelProviders
                .of(getActivity())
                .get(MainActivityViewModel.class);

        setupWorkflowRecyclerView();
        setupClickListeners();
        setupSearchListener();
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        workflowViewModel.initWorkflowList(prefs, this);
        if (firstLoad) {
            subscribe();
        }
        workflowViewModel.iniRightDrawerFilters();
        workflowViewModel.checkPermissions(prefs);
        return view;
    }

    @Override
    public void showDetail(WorkflowListItem item) {
        if (!workflowViewModel.hasViewDetailsPermissions()) return;

        workflowViewModel.resetFilterSettings();
        Intent intent = new Intent(getActivity(), WorkflowDetailActivity.class);
        intent.putExtra(WorkflowDetailActivity.EXTRA_WORKFLOW_LIST_ITEM, item);
        startActivityForResult(intent, REQUEST_WORKFLOW_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_WORKFLOW_DETAIL && resultCode == RESULT_OK) {
            addWorkflowsObserver();
            workflowViewModel.reloadWorkflowsList(this);
        }
    }

    // swipe down to refresh - SwipeRefreshLayout
    @Override
    public void onRefresh() {
        workflowViewModel.reloadWorkflowsList(this, true);
    }

    private void setupSearchListener() {
        fragmentWorkflowBinding.inputSearch.setOnKeyListener((v, keyCode, event) -> {
            if ((event
                    .getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        fragmentWorkflowBinding.imgSearch.setOnClickListener(v -> performSearch());
    }

    /**
     * Tells the view model to start search by query using the input search.
     */
    private void performSearch() {
        String searchText = fragmentWorkflowBinding.inputSearch.getText().toString();
        workflowViewModel.filterBySearchText(searchText, this);
        hideSoftInputKeyboard();
    }

    private void setupWorkflowRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        fragmentWorkflowBinding.recWorkflows.setLayoutManager(layoutManager);
        fragmentWorkflowBinding.recWorkflows.setNestedScrollingEnabled(false);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        itemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        fragmentWorkflowBinding.recWorkflows.addItemDecoration(itemDecoration);

        adapter = new WorkflowExpandableAdapter(this);
        fragmentWorkflowBinding.recWorkflows.setAdapter(adapter);
        // Swipe to refresh recyclerView
        fragmentWorkflowBinding.swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void updateAdapterList(PagedList<WorkflowListItem> workflowDbList) {
        adapter.submitList(workflowDbList);
        adapter.clearCheckedItems();
    }

    private void scrollRecyclerToTop(boolean scroll) {
        if (!scroll) return;

        //scroll to top after a tiny delay
        new Handler().postDelayed(
                () -> {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) fragmentWorkflowBinding.recWorkflows
                            .getLayoutManager();

                    if (linearLayoutManager == null) {
                        return;
                    }

                    linearLayoutManager.smoothScrollToPosition(fragmentWorkflowBinding.recWorkflows, null, 0);
                },
                500);
    }

    private void createFilterListRightDrawer(List<WorkflowTypeMenu> menus) {
        mainViewModel.createRightDrawerListAdapter(menus);
    }

    private void createOptionListRightDrawer(OptionsList optionsList) {
        mainViewModel.createRightDrawerOptionListAdapter(optionsList);
    }

    private void setupClickListeners() {
        fragmentWorkflowBinding.btnFilters.setOnClickListener(view1 -> {
            mainViewModel.openRightDrawer();
        });

        fragmentWorkflowBinding.btnAdd.setOnClickListener(view12 -> {
            mainActivityInterface.showFragment(CreateWorkflowFragment.newInstance(),
                    true
            );
        });

        fragmentWorkflowBinding.chbxSelectAll.setOnClickListener(view -> {
            boolean isChecked = fragmentWorkflowBinding.chbxSelectAll.isChecked();
            workflowViewModel.handleCheckboxAllOnClick(isChecked);
        });

        fragmentWorkflowBinding.ivMassActions.setOnClickListener(v -> showMassPopupMenu());
    }

    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
            fragmentWorkflowBinding.swipeRefreshLayout.setRefreshing(false);
            showBottomSheetLoading(false);
        }
    }

    private void showBottomSheetLoading(Boolean show) {
        if (show) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void showListContent(boolean show) {
        if (show) {
            fragmentWorkflowBinding.recWorkflows.setVisibility(View.VISIBLE);
            fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.GONE);
        } else {
            fragmentWorkflowBinding.recWorkflows.setVisibility(View.GONE);
            fragmentWorkflowBinding.lytNoworkflows.setVisibility(View.VISIBLE);
        }
    }

    private void addWorkflowsObserver() {
        workflowViewModel.getAllWorkflows().observe(this, getAllWorkflowsObserver);
    }

    private void subscribe() {
        // Used when we have some filter operation happening.
        final Observer<PagedList<WorkflowListItem>> updateWithSortedListObserver = (this::updateAdapterList);

        final Observer<Boolean> showListObserver = (this::showListContent);

        final Observer<Boolean> showLoadingObserver = (this::showLoading);

        final Observer<Boolean> setAllCeckboxesObserver = (isChecked -> {
            if (isChecked == null) {
                return;
            }
            adapter.setAllCheckboxes(isChecked);
        });

        // Workflow Fragment's ViewModel
        workflowViewModel.getObservableError().observe(this, this::showToastMessage);
        workflowViewModel.getObservableShowLoading().observe(this, showLoadingObserver);
        addWorkflowsObserver();
        workflowViewModel.getObservableUpdateWithSortedList()
                .observe(this, updateWithSortedListObserver);
        workflowViewModel.getObservableShowList().observe(this, showListObserver);
        workflowViewModel.getObservableAddWorkflowObserver().observe(this, aBoolean -> {
            addWorkflowsObserver();
        });
        workflowViewModel.getObservableSetAllCheckboxesList()
                .observe(this, setAllCeckboxesObserver);
        workflowViewModel.showBottomSheetLoading.observe(this, this::showBottomSheetLoading);
        workflowViewModel.getObservableLoadMore().observe(this, this::showBottomSheetLoading);
        workflowViewModel.rightDrawerFilterMenus
                .observe(this, this::createFilterListRightDrawer);
        workflowViewModel.rightDrawerOptionMenus
                .observe(this, this::createOptionListRightDrawer);
        workflowViewModel.invalidateDrawerOptionsList
                .observe(this, this::handleInvalidateOptionsList);
        workflowViewModel.messageMainToggleRadioButton
                .observe(this, this::handleMessageMainToggleRadioButton);
        workflowViewModel.messageMainToggleSwitch
                .observe(this, this::handleMessageMainToggleSwitch);
        workflowViewModel.messageMainUpdateSortSelection
                .observe(this, this::handleMessageMainUpdateSortSelection);
        workflowViewModel.messageMainWorkflowTypeFilters
                .observe(this, this::handleMessageMainWorkflowTypeFilters);
        workflowViewModel.messageMainBaseFilters
                .observe(this, this::handleMessageMainBaseFilters);
        workflowViewModel.messageMainStatusFilters
                .observe(this, this::handleMessageMainStatusFilters);
        workflowViewModel.messageMainSystemStatusFilters
                .observe(this, this::handleMessageMainSystemStatusFilters);
        workflowViewModel.messageMainWorkflowTypeFilterSelectionToFilterList
                .observe(this, this::handleMessageMainWorkflowTypeFilterSelected);
        workflowViewModel.messageMainWorkflowTypeIdFilterSelectionToFilterList
                .observe(this, this::handleMessageMainWorkflowTypeIdFilterSelected);
        workflowViewModel.messageMainBaseFilterSelectionToFilterList
                .observe(this, this::handleMessageMainBaseFilterSelected);
        workflowViewModel.messageMainStatusFilterSelectionToFilterList
                .observe(this, this::handleMessageMainStatusFilterSelected);
        workflowViewModel.messageMainSystemStatusFilterSelectionToFilterList
                .observe(this, this::handleMessageMainSystemStatusFilterSelected);
        workflowViewModel.getObservableShowAddButton()
                .observe(this, this::showAddButton);
        workflowViewModel.getObservableShowViewWorkflowButton()
                .observe(this, this::showViewWorkflowDetailsButton);
        workflowViewModel.getObservableCompleteMassAction()
                .observe(this, this::handleCompleteMassAction);
        workflowViewModel.getObservableHandleScrollRecyclerToTop()
                .observe(this, this::scrollRecyclerToTop);
        workflowViewModel.getObservableShowBulkActionMenu()
                .observe(this, this::showMassActionMenuIcon);
        workflowViewModel.getObservableFiltersCounter()
                .observe(this, this::updateFiltersCounterUi);

        // MainActivity's ViewModel
        mainViewModel.messageContainerToWorkflowList
                .observe(this, this::handleRightDrawerFilterClick);
        mainViewModel.messageBackActionToWorkflowList.observe(this, this::handleBackAction);
        mainViewModel.messageOptionSelectedToWorkflowList
                .observe(this, this::handleRightDrawerOptionSelectedClick);
        mainViewModel.messageDynamicFilterSelectedToWorkflowList
                .observe(this, this::handleRightDrawerDynamicFilterSelectedClick);
        mainViewModel.messageDynamicFilterListSelectedToWorkflowList
                .observe(this, this::handleRightDrawerDynamicFilterListSelectedClick);
        mainViewModel.messageInitSortByToWorkflowList.observe(this, this::handleInitSortBy);
        mainViewModel.messageRadioButtonClickedToWorkflowList
                .observe(this, this::handleMessageRadioButtonClickedToWorkflowList);
        mainViewModel.messageSortSwitchActionToWorkflowList
                .observe(this, this::handleMessageSortSwitchActionToWorkflowList);
        mainViewModel.messageWorkflowTypeFilterClickedToWorkflowList
                .observe(this, this::handleMessageWorkflowTypeFilterClicked);
        mainViewModel.messageBaseFiltersClickedToWorkflowList
                .observe(this, this::handleMessageBaseFiltersClicked);
        mainViewModel.messageStatusFiltersClickedToWorkflowList
                .observe(this, this::handleMessageStatusFiltersClicked);
        mainViewModel.messageSystemStatusFiltersClickedToWorkflowList
                .observe(this, this::handleMessageSystemStatusFiltersClicked);
        mainViewModel.messageWorkflowTypeFilterPositionSelectedToWorkflowList
                .observe(this, this::handleMessageWorkflowTypeFilterPositionSelected);
        mainViewModel.messageBaseFilterPositionSelectedToWorkflowList
                .observe(this, this::handleMessageBaseFilterPositionSelected);
        mainViewModel.messageStatusFilterPositionSelectedToWorkflowList
                .observe(this, this::handleMessageStatusFilterPositionSelected);
        mainViewModel.messageSystemStatusFilterPositionSelectedToWorkflowList
                .observe(this, this::handleMessageSystemStatusFilterPositionSelected);
    }

    private void handleMessageMainWorkflowTypeFilterSelected(String label) {
        mainViewModel.receiveMessageWorkflowTypeFilterSelectedToListUi(label);
    }

    private void handleMessageMainWorkflowTypeIdFilterSelected(Integer workflowTypeId) {
        mainViewModel.receiveMessageWorkflowTypeIdFilterSelectedToListUi(workflowTypeId);
    }

    private void handleMessageMainBaseFilterSelected(Integer resLabel) {
        mainViewModel.receiveMessageBaseFilterSelectedToListUi(resLabel);
    }

    private void handleMessageMainStatusFilterSelected(Integer resLabel) {
        mainViewModel.receiveMessageStatusFilterSelectedToListUi(resLabel);
    }

    private void handleMessageMainSystemStatusFilterSelected(Integer resLabel) {
        mainViewModel.receiveMessageSystemStatusFilterSelectedToListUi(resLabel);
    }

    private void handleMessageWorkflowTypeFilterPositionSelected(Integer position) {
        workflowViewModel.handleWorkflowTypeFieldPositionSelected(position, this);
    }

    private void handleMessageBaseFilterPositionSelected(Integer position) {
        workflowViewModel.handleBaseFieldPositionSelected(position, this);
    }

    private void handleMessageStatusFilterPositionSelected(Integer position) {
        workflowViewModel.handleStatusFieldPositionSelected(position, this);
    }

    private void handleMessageSystemStatusFilterPositionSelected(Integer position) {
        workflowViewModel.handleSystemStatusFieldPositionSelected(position, this);
    }

    private void handleMessageMainWorkflowTypeFilters(OptionsList optionsList) {
        mainViewModel.createDrawerWorkflowTypeFiltersOptionListAdapter(optionsList);
    }

    private void handleMessageMainBaseFilters(OptionsList optionsList) {
        mainViewModel.createDrawerBaseFiltersOptionListAdapter(optionsList);
    }

    private void handleMessageMainStatusFilters(OptionsList optionsList) {
        mainViewModel.createDrawerStatusFiltersOptionListAdapter(optionsList);
    }

    private void handleMessageMainSystemStatusFilters(OptionsList optionsList) {
        mainViewModel.createDrawerSystemStatusFiltersOptionListAdapter(optionsList);
    }

    private void handleMessageWorkflowTypeFilterClicked(Boolean clicked) {
        workflowViewModel.handleWorkflowTypeFieldClick();
    }

    private void handleMessageBaseFiltersClicked(Boolean clicked) {
        workflowViewModel.handleBaseFieldClick();
    }

    private void handleMessageStatusFiltersClicked(Boolean clicked) {
        workflowViewModel.handleStatusFieldClick();
    }

    private void handleMessageSystemStatusFiltersClicked(Boolean clicked) {
        workflowViewModel.handleSystemStatusFieldClick();
    }

    private void handleMessageMainUpdateSortSelection(int sortType) {
        mainViewModel.receiveMessageUpdateSortSelection(sortType);
    }

    private void handleMessageSortSwitchActionToWorkflowList(
            RightDrawerSortSwitchAction actionMessage) {
        workflowViewModel.handleSwitchOnClick(
                actionMessage.viewRadioType,
                actionMessage.sortType,
                actionMessage.isChecked
        );
        workflowViewModel.applyFilters(this);
    }

    private void handleMessageRadioButtonClickedToWorkflowList(int[] message) {
        workflowViewModel.receiveMessageRadioButtonClicked(message);
        workflowViewModel.applyFilters(this);
    }

    private void handleMessageMainToggleSwitch(int[] message) {
        mainViewModel.receiveMessageToggleSwitch(message);
    }

    private void handleMessageMainToggleRadioButton(int[] message) {
        mainViewModel.receiveMessageToggleRadioButton(message);
    }

    private void handleInitSortBy(Boolean init) {
        workflowViewModel.initSortBy();
    }

    private void handleRightDrawerOptionSelectedClick(int position) {
        workflowViewModel.handleOptionSelected(position, WorkflowFragment.this);
    }

    private void handleRightDrawerDynamicFilterSelectedClick(DynamicFilter dynamicFilter) {
        workflowViewModel.handleDynamicFilterSelected(dynamicFilter, WorkflowFragment.this);
    }

    private void handleRightDrawerDynamicFilterListSelectedClick(
            List<DynamicFilter> dynamicFilters) {
        workflowViewModel.handleDynamicFilterListSelected(dynamicFilters, WorkflowFragment.this);
    }

    private void handleRightDrawerFilterClick(int position) {
        workflowViewModel.handleSelectedItemInFilters(position);
    }

    private void handleInvalidateOptionsList(Boolean invalidate) {
        mainViewModel.invalidateOptionListDrawer();
    }

    private void handleBackAction(Boolean back) {
        workflowViewModel.handleRightDrawerBackAction();
    }

    private void setSwitchAscendingDescendingText(Switch switchType, boolean check) {
        if (check) {
            switchType.setText(getString(R.string.ascending));
        } else {
            switchType.setText(getString(R.string.descending));
        }
    }

    @UiThread
    private void showAddButton(boolean show) {
        fragmentWorkflowBinding.btnAdd.setVisibility(show ? View.VISIBLE : View.GONE);

        verifyHeaderLineVisibility();
    }

    @UiThread
    private void showViewWorkflowDetailsButton(boolean show) {
        adapter.setShowViewWorkflowButton(show);
    }

    private void showMassPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), fragmentWorkflowBinding.ivMassActions);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_workflow_list, popup.getMenu());

        popup.getMenu().findItem(R.id.enable).setVisible(
                workflowViewModel.isShowEnableActionMenu() && workflowViewModel
                        .hasBulkActivationPermissions());
        popup.getMenu().findItem(R.id.disable).setVisible(
                workflowViewModel.isShowDisableActionMenu() && workflowViewModel
                        .hasBulkActivationPermissions());
        popup.getMenu().findItem(R.id.open).setVisible(
                workflowViewModel.isShowOpenActionMenu() && workflowViewModel
                        .hasBulkOpenClosePermissions());
        popup.getMenu().findItem(R.id.close).setVisible(
                workflowViewModel.isShowCloseActionMenu() && workflowViewModel
                        .hasBulkOpenClosePermissions());
        popup.getMenu().findItem(R.id.delete)
                .setVisible(workflowViewModel.hasBulkDeletePermissions());

        popup.setOnMenuItemClickListener(item -> {
            List<Integer> checkedList = adapter.getCheckedIds();
            if (checkedList.isEmpty()) showToastMessage(R.string.workflow_list_no_selections);

            switch (item.getItemId()) {
                case R.id.enable:
                    workflowViewModel.enableDisableWorkflows(checkedList, true);
                    break;
                case R.id.disable:
                    workflowViewModel.enableDisableWorkflows(checkedList, false);
                    break;
                case R.id.open:
                    workflowViewModel.openCloseWorkflows(checkedList, true);
                    break;
                case R.id.close:
                    workflowViewModel.openCloseWorkflows(checkedList, false);
                    break;
                case R.id.delete:
                    showDeleteConfirmationDialog(checkedList);
                    break;
            }

            return false;
        });

        popup.show();
    }

    private void showDeleteConfirmationDialog(List<Integer> checkedList) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(),
                R.style.AlertDialogTheme);

        int titleResId;
        int messageResId;
        //check for plurals
        if (checkedList.size() > 1) {
            titleResId = R.string.workflow_list_delete_dialog_title;
            messageResId = R.string.workflow_list_delete_dialog_msg;
        } else {
            titleResId = R.string.workflow_detail_activity_delete_dialog_title;
            messageResId = R.string.workflow_detail_activity_delete_dialog_msg;
        }

        builder.setTitle(titleResId);

        final String separator = ", ";
        StringBuilder stringBuilder = new StringBuilder();
        adapter.getCheckedNames().forEach(s -> {
            stringBuilder.append(s);
            stringBuilder.append(separator);
        });
        stringBuilder
                .delete(stringBuilder.length() - separator.length(), stringBuilder.length());
        String workflowsString = stringBuilder.toString();

        builder.setMessage(getString(
                messageResId,
                workflowsString
        ));
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.accept,
                (dialog, which) -> workflowViewModel.deleteWorkflows(checkedList));
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
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
    private void handleCompleteMassAction(boolean refresh) {
        if (!refresh) return;

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);

        workflowViewModel.initWorkflowList(prefs, this);
    }

    @UiThread
    private void showMassActionMenuIcon(boolean show) {
        fragmentWorkflowBinding.ivMassActions.setVisibility(show ? View.VISIBLE : View.GONE);
        fragmentWorkflowBinding.chbxSelectAll.setVisibility(show ? View.VISIBLE : View.GONE);

        adapter.setShowCheckbox(show);

        verifyHeaderLineVisibility();
    }

    @UiThread
    private void verifyHeaderLineVisibility() {
        boolean show = fragmentWorkflowBinding.ivMassActions.getVisibility() == View.VISIBLE
                || fragmentWorkflowBinding.chbxSelectAll.getVisibility() == View.VISIBLE
                || fragmentWorkflowBinding.btnAdd.getVisibility() == View.VISIBLE;

        fragmentWorkflowBinding.viewLineHeader.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void updateFiltersCounterUi(int count) {
        if (count == 0) {
            fragmentWorkflowBinding.btnFilters.setText(R.string.filters);
        } else {
            fragmentWorkflowBinding.btnFilters.setText(getString(R.string.filters_counter, count));
        }
    }

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getActivity() != null ? getActivity().getCurrentFocus() : null;
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
