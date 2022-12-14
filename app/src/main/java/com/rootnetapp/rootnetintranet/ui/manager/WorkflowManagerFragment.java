package com.rootnetapp.rootnetintranet.ui.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.Option;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.SingleChoiceFormItem;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.adapters.OnTouchClickListener;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.PendingWorkflowsAdapter;
import com.rootnetapp.rootnetintranet.ui.timeline.SelectDateDialog;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerViewModel.DAY_AGO_DAYS;
import static com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerViewModel.MONTH_AGO_DAYS;
import static com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerViewModel.WEEK_AGO_DAYS;

public class WorkflowManagerFragment extends Fragment implements ManagerInterface {

    @Inject
    WorkflowManagerViewModelFactory factory;
    WorkflowManagerViewModel viewModel;

    private FragmentWorkflowManagerBinding mBinding;
    private MainActivityInterface anInterface;
    private PendingWorkflowsAdapter mWorkflowsAdapter;
    private PendingWorkflowsAdapter mPendingWorkflowsAdapter;

    public WorkflowManagerFragment() {
        // Required empty public constructor
    }

    public static WorkflowManagerFragment newInstance(MainActivityInterface anInterface) {
        WorkflowManagerFragment fragment = new WorkflowManagerFragment();
        fragment.anInterface = anInterface;
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
                R.layout.fragment_workflow_manager, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, factory)
                .get(WorkflowManagerViewModel.class);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");
        String loggedUserId = prefs.getString(PreferenceKeys.PREF_PROFILE_ID, "");

        String start = Utils.getCurrentFormattedDateDaysDiff(MONTH_AGO_DAYS);
        String end = Utils.getCurrentFormattedDate();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.month);

        subscribe();
        setOnClickListeners();
        setupRecyclers();

        viewModel.init(token, loggedUserId, start, end);

        return view;
    }

    /**
     * Performs all of the subscriptions to the ViewModel's observables.
     */
    private void subscribe() {
        viewModel.getObservableShowLoading().observe(getViewLifecycleOwner(), this::showLoading);
        viewModel.getObservableError().observe(getViewLifecycleOwner(), this::showToastMessage);
        viewModel.getObservableWorkflows().observe(getViewLifecycleOwner(), this::populateWorkflows);
        viewModel.getObservableHideMoreButton().observe(getViewLifecycleOwner(), this::hideMoreButton);
        viewModel.getObservableHideWorkflowList().observe(getViewLifecycleOwner(), this::hideWorkflowList);
        viewModel.getObservableWorkflowTypeItem().observe(getViewLifecycleOwner(), this::setupWorkflowTypeFormItem);
        viewModel.getObservableUserPendingWorkflowsList().observe(getViewLifecycleOwner(), this::populatePendingWorkflows);
        viewModel.getObservableHidePendingMoreButton().observe(getViewLifecycleOwner(), this::hidePendingMoreButton);
        viewModel.getObservableHidePendingWorkflowList().observe(getViewLifecycleOwner(), this::hidePendingWorkflowList);

        viewModel.getObservableUserPendingCount().observe(getViewLifecycleOwner(), this::updateUserPendingWorkflowsCount);
        viewModel.getObservableUserOpenCount().observe(getViewLifecycleOwner(), this::updateUserOpenWorkflowsCount);
        viewModel.getObservableUserClosedCount().observe(getViewLifecycleOwner(), this::updateUserClosedWorkflowsCount);
        viewModel.getObservableUserOutOfTimeCount().observe(getViewLifecycleOwner(), this::updateUserOutOfTimeWorkflowsCount);
        viewModel.getObservableUserUpdatedCount().observe(getViewLifecycleOwner(), this::updateUserUpdatedWorkflowsCount);
        viewModel.getObservableUserPendingWorkflowsDialog().observe(getViewLifecycleOwner(), this::showUserPendingWorkflowsDialog);
        viewModel.getObservableUserOpenWorkflows().observe(getViewLifecycleOwner(), this::showUserOpenWorkflowsDialog);
        viewModel.getObservableUserClosedWorkflows().observe(getViewLifecycleOwner(), this::showUserClosedWorkflowsDialog);
        viewModel.getObservableUserOutOfTimeWorkflows().observe(getViewLifecycleOwner(), this::showUserOutOfTimeWorkflowsDialog);
        viewModel.getObservableUserUpdatedWorkflows().observe(getViewLifecycleOwner(), this::showUserUpdatedWorkflowsDialog);

        viewModel.getObservableCompanyPendingCount().observe(getViewLifecycleOwner(), this::updateCompanyPendingWorkflowsCount);
        viewModel.getObservableCompanyOpenCount().observe(getViewLifecycleOwner(), this::updateCompanyOpenWorkflowsCount);
        viewModel.getObservableCompanyClosedCount().observe(getViewLifecycleOwner(), this::updateCompanyClosedWorkflowsCount);
        viewModel.getObservableCompanyOutOfTimeCount().observe(getViewLifecycleOwner(), this::updateCompanyOutOfTimeWorkflowsCount);
        viewModel.getObservableCompanyUpdatedCount().observe(getViewLifecycleOwner(), this::updateCompanyUpdatedWorkflowsCount);
        viewModel.getObservableCompanyPeopleInvolvedCount().observe(getViewLifecycleOwner(), this::updateCompanyPeopleInvolvedCount);
        viewModel.getObservableCompanyPendingWorkflows().observe(getViewLifecycleOwner(), this::showCompanyPendingWorkflowsDialog);
        viewModel.getObservableCompanyOpenWorkflows().observe(getViewLifecycleOwner(), this::showCompanyOpenWorkflowsDialog);
        viewModel.getObservableCompanyClosedWorkflows().observe(getViewLifecycleOwner(), this::showCompanyClosedWorkflowsDialog);
        viewModel.getObservableCompanyOutOfTimeWorkflows().observe(getViewLifecycleOwner(), this::showCompanyOutOfTimeWorkflowsDialog);
        viewModel.getObservableCompanyUpdatedWorkflows().observe(getViewLifecycleOwner(), this::showCompanyUpdatedWorkflowsDialog);
    }

    /**
     * Define the onClick behavior for this View's components.
     */
    private void setOnClickListeners() {
        mBinding.tvMonth.setOnClickListener(v -> filterMonthClicked());
        mBinding.tvWeek.setOnClickListener(v -> filterWeekClicked());
        mBinding.tvDay.setOnClickListener(v -> filterDayClicked());
        mBinding.btnSelectDates.setOnClickListener(v -> selectDates());
        mBinding.btnShowMore.setOnClickListener(v -> showMoreClicked());
        mBinding.btnMyPendingShowMore.setOnClickListener(v -> showMoreClickedPending());

        mBinding.btnUserPendingApproval.setOnClickListener(v -> getUserPendingWorkflows());
        mBinding.llUserOpenWorkflows.setOnClickListener(v -> getUserOpenWorkflows());
        mBinding.llUserClosedWorkflows.setOnClickListener(v -> getUserClosedWorkflows());
        mBinding.btnUserOutOfTime.setOnClickListener(v -> getUserOutOfTimeWorkflows());
        mBinding.btnUserUpdated.setOnClickListener(v -> getUserUpdatedWorkflows());

        mBinding.btnCompanyPendingApproval.setOnClickListener(v -> getCompanyPendingWorkflows());
        mBinding.llCompanyOpenWorkflows.setOnClickListener(v -> getCompanyOpenWorkflows());
        mBinding.llCompanyClosedWorkflows.setOnClickListener(v -> getCompanyClosedWorkflows());
        mBinding.btnCompanyOutOfTime.setOnClickListener(v -> getCompanyOutOfTimeWorkflows());
        mBinding.btnCompanyUpdated.setOnClickListener(v -> getCompanyUpdatedWorkflows());
    }

    /**
     * Initializes the workflows RecyclerView.
     */
    private void setupRecyclers() {
        mBinding.rvMyPendingWorkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvMyPendingWorkflows.setNestedScrollingEnabled(false);

        mBinding.rvWorkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvWorkflows.setNestedScrollingEnabled(false);
    }

    /**
     * Creates and defines the behavior for the Workflow Type filter.
     *
     * @param singleChoiceFormItem form item that will work as a filter.
     */
    @UiThread
    private void setupWorkflowTypeFormItem(SingleChoiceFormItem singleChoiceFormItem) {

        String title = singleChoiceFormItem.getTitle();
        if ((title == null || title.isEmpty()) && singleChoiceFormItem.getTitleRes() != 0) {
            title = getString(singleChoiceFormItem.getTitleRes());
        }
        mBinding.formItemWorkflowType.tvTitle.setText(title);

        List<Option> options = new ArrayList<>(singleChoiceFormItem.getOptions());

        //add hint
        String hint = getString(R.string.no_selection_hint);
        // check whether the hint has already been added
        if (!options.get(0).getName().equals(hint)) {
            // add hint as first item
            options.add(0, new Option(0, hint));
        }

        //create the adapter
        mBinding.formItemWorkflowType.spInput.setAdapter(
                new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                        options));

        //workaround so the listener won't be called on init
        mBinding.formItemWorkflowType.spInput.setSelection(0, false);

        // this prevents the listener to be triggered by setSelection
        int index = singleChoiceFormItem.getOptions().indexOf(singleChoiceFormItem.getValue());
        index++; // because of the No Selection option
        mBinding.formItemWorkflowType.spInput.setTag(index);
        mBinding.formItemWorkflowType.spInput.setSelection(index);

        mBinding.formItemWorkflowType.spInput.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {

                        // this prevents the listener to be triggered by setSelection
                        Object tag = mBinding.formItemWorkflowType.spInput.getTag();
                        if (tag == null || (int) tag != position) {

                            // the user has selected the No Selection option
                            if (position == 0) {
                                singleChoiceFormItem.setValue(null);
                                if (singleChoiceFormItem.getOnSelectedListener() != null) {
                                    singleChoiceFormItem.getOnSelectedListener()
                                            .onSelected(singleChoiceFormItem);
                                }
                                return;
                            }

                            // the user has selected a valid option
                            int index = position - 1; // because of the No Selection option
                            singleChoiceFormItem
                                    .setValue(singleChoiceFormItem.getOptions().get(index));
                            if (singleChoiceFormItem.getOnSelectedListener() != null) {
                                singleChoiceFormItem.getOnSelectedListener()
                                        .onSelected(singleChoiceFormItem);
                            }
                        }

                        // this prevents the listener to be triggered by setSelection
                        mBinding.formItemWorkflowType.spInput.setTag(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        //make sure this view gets the focus
        mBinding.formItemWorkflowType.spInput
                .setOnTouchListener(new OnTouchClickListener(mBinding.formItemWorkflowType.root));

        //required indicator
        mBinding.formItemWorkflowType.tvRequired.setVisibility(View.GONE);

        singleChoiceFormItem.setOnSelectedListener(item -> {
            Integer workflowTypeId = null;

            if (item.getValue() != null) {
                workflowTypeId = item.getValue().getId();
            }

            updateDashboard(workflowTypeId);
        });
    }

    /**
     * Resets the workflows adapter and performs a request to the ViewModel to update the dashboard
     * data with new date filters
     *
     * @param startDate start date filter.
     * @param endDate   end date filter.
     */
    private void updateDashboard(String startDate, String endDate) {
        mPendingWorkflowsAdapter = null; //reset the adapter
        mWorkflowsAdapter = null; //reset the adapter
        viewModel.updateDashboard(startDate, endDate);
    }

    /**
     * Resets the workflows adapter and performs a request to the ViewModel to update the dashboard
     * data with new workflow type filters.
     *
     * @param workflowTypeId workflow type filter.
     */
    private void updateDashboard(Integer workflowTypeId) {
        mPendingWorkflowsAdapter = null; //reset the adapter
        mWorkflowsAdapter = null; //reset the adapter
        viewModel.updateDashboard(workflowTypeId);
    }

    /**
     * Opens the select date dialog.
     */
    private void selectDates() {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    /**
     * Performs a request to the ViewModel to retrieve more workflows.
     */
    private void showMoreClicked() {
        viewModel.incrementCurrentPage();
        viewModel.getWorkflows();
    }

    /**
     * Performs a request to the ViewModel to retrieve more pending workflows.
     */
    private void showMoreClickedPending() {
        viewModel.incrementCurrentPagePending();
        viewModel.getUserPendingWorkflowsList();
    }

    //region Date Filters

    /**
     * Updates the UI related to the date filters with the current month and updates the dashboard
     * data.
     */
    private void filterMonthClicked() {
        selectMonthButton(true);
        selectWeekButton(false);
        selectDayButton(false);

        String start = Utils.getCurrentFormattedDateDaysDiff(MONTH_AGO_DAYS);
        String end = Utils.getCurrentFormattedDate();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.month);

        updateDashboard(start, end);
    }

    /**
     * Updates the UI related to the date filters with the current week and updates the dashboard
     * data.
     */
    private void filterWeekClicked() {
        selectMonthButton(false);
        selectWeekButton(true);
        selectDayButton(false);

        String start = Utils.getCurrentFormattedDateDaysDiff(WEEK_AGO_DAYS);
        String end = Utils.getCurrentFormattedDate();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.week);

        updateDashboard(start, end);
    }

    /**
     * Updates the UI related to the date filters with the current day and updates the dashboard
     * data.
     */
    private void filterDayClicked() {
        selectMonthButton(false);
        selectWeekButton(false);
        selectDayButton(true);

        String start = Utils.getCurrentFormattedDateDaysDiff(DAY_AGO_DAYS);
        String end = Utils.getCurrentFormattedDate();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.day);

        updateDashboard(start, end);
    }

    /**
     * Selects or deselects visually the current month button based on the parameter.
     *
     * @param select true - select; false - deselect.
     */
    @UiThread
    private void selectMonthButton(boolean select) {
        if (select) {
            mBinding.tvMonth.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.selected_filter));
            mBinding.tvMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            mBinding.tvMonth.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter));
            mBinding.tvMonth.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter_text));
        }
    }

    /**
     * Selects or deselects visually the current week button based on the parameter.
     *
     * @param select true - select; false - deselect.
     */
    @UiThread
    private void selectWeekButton(boolean select) {
        if (select) {
            mBinding.tvWeek.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.selected_filter));
            mBinding.tvWeek.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            mBinding.tvWeek.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter));
            mBinding.tvWeek.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter_text));
        }
    }

    /**
     * Selects or deselects visually the current day button based on the parameter.
     *
     * @param select true - select; false - deselect.
     */
    @UiThread
    private void selectDayButton(boolean select) {
        if (select) {
            mBinding.tvDay.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.selected_filter));
            mBinding.tvDay.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
            mBinding.tvDay.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter));
            mBinding.tvDay.setTextColor(
                    ContextCompat.getColor(getContext(), R.color.unselected_filter_text));
        }
    }

    /**
     * Updates the date filter UI and request a dashboard data update. Called from the select date
     * dialog.
     *
     * @param start selected start date.
     * @param end   selected end date.
     */
    @Override
    public void setDate(String start, String end) {
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.selected_period);

        updateDashboard(start, end);
    }
    //endregion

    /**
     * Opens the WorkflowDetailActivity for the specified workflow.
     *
     * @param workflowListItem workflow to open the details.
     */
    @Override
    public void showWorkflow(WorkflowListItem workflowListItem) {
        Intent intent = new Intent(getActivity(), WorkflowDetailActivity.class);
        intent.putExtra(WorkflowDetailActivity.EXTRA_WORKFLOW_LIST_ITEM, workflowListItem);
        anInterface.showActivity(intent);
    }

    //region Workflows Dialog

    //region User Workflows

    /**
     * Sends a request to the ViewModel to retrieve the user's pending workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showUserPendingWorkflowsDialog(List)}.
     */
    private void getUserPendingWorkflows() {
        viewModel.getUserPendingWorkflowsDialog();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's open workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showUserOpenWorkflowsDialog(List)}.
     */
    private void getUserOpenWorkflows() {
        viewModel.getUserOpenWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's closed workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showUserClosedWorkflowsDialog(List)}.
     */
    private void getUserClosedWorkflows() {
        viewModel.getUserClosedWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's out of time workflows. This is
     * invoked by the user interaction onClick and the flow ends with the dialog display called by
     * {@link #showUserOutOfTimeWorkflowsDialog(List)}.
     */
    private void getUserOutOfTimeWorkflows() {
        viewModel.getUserOutOfTimeWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's updated workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showUserUpdatedWorkflowsDialog(List)}.
     */
    private void getUserUpdatedWorkflows() {
        viewModel.getUserUpdatedWorkflows();
    }

    /**
     * Displays a DialogFragment with a list of the user's pending workflows.
     *
     * @param workflowList list of user's pending workflows.
     */
    private void showUserPendingWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.USER_PENDING,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's open workflows.
     *
     * @param workflowList list of user's open workflows.
     */
    private void showUserOpenWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.USER_OPEN,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's closed workflows.
     *
     * @param workflowList list of user's closed workflows.
     */
    private void showUserClosedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.USER_CLOSED,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's out of time workflows.
     *
     * @param workflowList list of user's out of time workflows.
     */
    private void showUserOutOfTimeWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.USER_OUT_OF_TIME,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's updated workflows.
     *
     * @param workflowList list of user's updated workflows.
     */
    private void showUserUpdatedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.USER_UPDATED,
                workflowList));
    }
    //endregion

    //region Company Workflows

    /**
     * Sends a request to the ViewModel to retrieve the user's pending workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showCompanyPendingWorkflowsDialog(List)}.
     */
    private void getCompanyPendingWorkflows() {
        viewModel.getCompanyPendingWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's open workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showCompanyOpenWorkflowsDialog(List)}.
     */
    private void getCompanyOpenWorkflows() {
        viewModel.getCompanyOpenWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's closed workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showCompanyClosedWorkflowsDialog(List)}.
     */
    private void getCompanyClosedWorkflows() {
        viewModel.getCompanyClosedWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's out of time workflows. This is
     * invoked by the user interaction onClick and the flow ends with the dialog display called by
     * {@link #showCompanyOutOfTimeWorkflowsDialog(List)}.
     */
    private void getCompanyOutOfTimeWorkflows() {
        viewModel.getCompanyOutOfTimeWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's updated workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showCompanyUpdatedWorkflowsDialog(List)}.
     */
    private void getCompanyUpdatedWorkflows() {
        viewModel.getCompanyUpdatedWorkflows();
    }

    /**
     * Displays a DialogFragment with a list of the user's pending workflows.
     *
     * @param workflowList list of user's pending workflows.
     */
    private void showCompanyPendingWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.COMPANY_PENDING,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's open workflows.
     *
     * @param workflowList list of user's open workflows.
     */
    private void showCompanyOpenWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.COMPANY_OPEN,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's closed workflows.
     *
     * @param workflowList list of user's closed workflows.
     */
    private void showCompanyClosedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.COMPANY_CLOSED,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's out of time workflows.
     *
     * @param workflowList list of user's out of time workflows.
     */
    private void showCompanyOutOfTimeWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.COMPANY_OUT_OF_TIME,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's updated workflows.
     *
     * @param workflowList list of user's updated workflows.
     */
    private void showCompanyUpdatedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogType.COMPANY_UPDATED,
                workflowList));
    }
    //endregion
    //endregion

    /**
     * Adds data to the pending workflows adapter. Checks whether the adapter needs to be created or
     * not.
     *
     * @param workflowList data to add to the adapter.
     */
    @UiThread
    private void populatePendingWorkflows(List<WorkflowDb> workflowList) {
        if (workflowList == null) return;

        if (mPendingWorkflowsAdapter == null) {
            //create a new adapter
            mPendingWorkflowsAdapter = new PendingWorkflowsAdapter(workflowList, this);
            mBinding.rvMyPendingWorkflows.setAdapter(mPendingWorkflowsAdapter);
        } else {
            //append a list to the current adapter
            mPendingWorkflowsAdapter.addData(workflowList);
        }
    }

    /**
     * Adds data to the workflows adapter. Checks whether the adapter needs to be created or not.
     *
     * @param workflowList data to add to the adapter.
     */
    @UiThread
    private void populateWorkflows(List<WorkflowDb> workflowList) {
        if (workflowList == null) return;

        if (mWorkflowsAdapter == null) {
            //create a new adapter
            mWorkflowsAdapter = new PendingWorkflowsAdapter(workflowList, this);
            mBinding.rvWorkflows.setAdapter(mWorkflowsAdapter);
        } else {
            //append a list to the current adapter
            mWorkflowsAdapter.addData(workflowList);
        }
    }

    /**
     * Hides or shows the "SHOW MORE" button of the pending workflows list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hidePendingMoreButton(boolean hide) {
        if (hide) {
            mBinding.btnMyPendingShowMore.setVisibility(View.GONE);
        } else {
            mBinding.btnMyPendingShowMore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides or shows the pending workflows list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hidePendingWorkflowList(boolean hide) {
        hidePendingMoreButton(hide);

        if (hide) {
            mBinding.rvMyPendingWorkflows.setVisibility(View.GONE);
            mBinding.tvNoPendingWorkflows.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvMyPendingWorkflows.setVisibility(View.VISIBLE);
            mBinding.tvNoPendingWorkflows.setVisibility(View.GONE);
        }
    }

    /**
     * Hides or shows the "SHOW MORE" button of the general workflows list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hideMoreButton(boolean hide) {
        if (hide) {
            mBinding.btnShowMore.setVisibility(View.GONE);
        } else {
            mBinding.btnShowMore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides or shows the general workflows list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hideWorkflowList(boolean hide) {
        hideMoreButton(hide);

        if (hide) {
            mBinding.rvWorkflows.setVisibility(View.GONE);
            mBinding.lytNoworkflows.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvWorkflows.setVisibility(View.VISIBLE);
            mBinding.lytNoworkflows.setVisibility(View.GONE);
        }
    }

    @UiThread
    private void updateSelectedDatesUi(String startDate, String endDate) {
        startDate = Utils.getFormattedDate(startDate, Utils.SERVER_DATE_FORMAT,
                Utils.SHORT_DATE_DISPLAY_FORMAT);
        endDate = Utils.getFormattedDate(endDate, Utils.SERVER_DATE_FORMAT,
                Utils.SHORT_DATE_DISPLAY_FORMAT);

        mBinding.tvSelectedDate.setText(String.format(Locale.US, "(%s - %s)", startDate, endDate));
    }

    @UiThread
    private void updateSelectedDateTitle(int titleRes) {
        mBinding.tvSelectedDateTitle.setText(getString(titleRes));
    }

    @UiThread
    private void updateUserPendingWorkflowsCount(int count) {
        mBinding.tvUserPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUserOpenWorkflowsCount(int count) {
        mBinding.tvUserOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUserClosedWorkflowsCount(int count) {
        mBinding.tvUserClosedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUserOutOfTimeWorkflowsCount(int count) {
        mBinding.tvUserOutOfTimeCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUserUpdatedWorkflowsCount(int count) {
        mBinding.tvUserUpdatedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyPendingWorkflowsCount(int count) {
        mBinding.tvCompanyPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyOpenWorkflowsCount(int count) {
        mBinding.tvCompanyOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyClosedWorkflowsCount(int count) {
        mBinding.tvCompanyClosedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyOutOfTimeWorkflowsCount(int count) {
        mBinding.tvCompanyOutOfTimeCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyUpdatedWorkflowsCount(int count) {
        mBinding.tvCompanyUpdatedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateCompanyPeopleInvolvedCount(int count) {
        mBinding.tvCompanyPeopleInvolvedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void showLoading(Boolean show) {
        if (show) {
            Utils.showLoading(getContext());
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    @Override
    public void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}
