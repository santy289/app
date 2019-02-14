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

public class WorkflowManagerFragment extends Fragment implements ManagerInterface {

    @Inject
    WorkflowManagerViewModelFactory factory;
    WorkflowManagerViewModel viewModel;

    private FragmentWorkflowManagerBinding mBinding;
    private MainActivityInterface anInterface;
    private PendingWorkflowsAdapter mWorkflowsAdapter;

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
        String token = "Bearer " + prefs.getString("token", "");

        String start = Utils.getMonthDay(0, 1);
        String end = Utils.getMonthDay(0, 30);
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.current_month);

        subscribe();
        setOnClickListeners();
        setupRecycler();

        viewModel.init(token, start, end);

        return view;
    }

    /**
     * Performs all of the subscriptions to the ViewModel's observables.
     */
    private void subscribe() {
        viewModel.getObservableShowLoading().observe(this, this::showLoading);
        viewModel.getObservableError().observe(this, this::showToastMessage);
        viewModel.getObservableWorkflows().observe(this, this::populatePendingWorkflows);
        viewModel.getObservableMyPendingCount().observe(this, this::updateMyPendingWorkflowsCount);
        viewModel.getObservableMyOpenCount().observe(this, this::updateMyOpenWorkflowsCount);
        viewModel.getObservableMyClosedCount().observe(this, this::updateMyClosedWorkflowsCount);
        viewModel.getObservableOutOfTimeCount().observe(this, this::updateOutOfTimeWorkflowsCount);
        viewModel.getObservableUpdatedCount().observe(this, this::updateUpdatedWorkflowsCount);
        viewModel.getObservablePendingCount().observe(this, this::updatePendingWorkflowsCount);
        viewModel.getObservableOpenCount().observe(this, this::updateOpenWorkflowsCount);
        viewModel.getObservableClosedCount().observe(this, this::updateClosedWorkflowsCount);
        viewModel.getObservableHideMoreButton().observe(this, this::hideMoreButton);
        viewModel.getObservableHideWorkflowList().observe(this, this::hideWorkflowList);
        viewModel.getObservableMyPendingWorkflows()
                .observe(this, this::showMyPendingWorkflowsDialog);
        viewModel.getObservableMyOpenWorkflows().observe(this, this::showMyOpenWorkflowsDialog);
        viewModel.getObservableMyClosedWorkflows().observe(this, this::showMyClosedWorkflowsDialog);
        viewModel.getObservableOutOfTimeWorkflows()
                .observe(this, this::showOutOfTimeWorkflowsDialog);
        viewModel.getObservableUpdatedWorkflows().observe(this, this::showUpdatedWorkflowsDialog);
        viewModel.getObservableWorkflowTypeItem().observe(this, this::setupWorkflowTypeFormItem);
    }

    /**
     * Define the onClick behavior for this View's components.
     */
    private void setOnClickListeners() {
        mBinding.tvMonth.setOnClickListener(v -> filterMonthClicked());
        mBinding.tvWeek.setOnClickListener(v -> filterWeekClicked());
        mBinding.tvDay.setOnClickListener(v -> filterDayClicked());
        mBinding.btnSelectDates.setOnClickListener(v -> selectDates());
        mBinding.btnPendingApproval.setOnClickListener(v -> getMyPendingWorkflows());
        mBinding.llMyOpenWorkflows.setOnClickListener(v -> getMyOpenWorkflows());
        mBinding.llMyClosedWorkflows.setOnClickListener(v -> getMyClosedWorkflows());
        mBinding.btnOutOfTime.setOnClickListener(v -> getOutOfTimeWorkflows());
        mBinding.btnUpdated.setOnClickListener(v -> getUpdatedWorkflows());
        mBinding.btnShowMore.setOnClickListener(v -> showMoreClicked());
    }

    /**
     * Initializes the workflows RecyclerView.
     */
    private void setupRecycler() {
        mBinding.recPendingworkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recPendingworkflows.setNestedScrollingEnabled(false);
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

    //region Date Filters

    /**
     * Updates the UI related to the date filters with the current month and updates the dashboard
     * data.
     */
    private void filterMonthClicked() {
        selectMonthButton(true);
        selectWeekButton(false);
        selectDayButton(false);

        String start = Utils.getMonthDay(0, 1);
        String end = Utils.getMonthDay(0, 30);
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.current_month);

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

        String start = Utils.getWeekStart();
        String end = Utils.getWeekEnd();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.current_week);

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

        String start = Utils.getCurrentDate();
        String end = Utils.getTomorrowDate();
        updateSelectedDatesUi(start);
        updateSelectedDateTitle(R.string.today);

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

    /**
     * Sends a request to the ViewModel to retrieve the user's pending workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showMyPendingWorkflowsDialog(List)}.
     */
    private void getMyPendingWorkflows() {
        viewModel.getMyPendingWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's open workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showMyOpenWorkflowsDialog(List)}.
     */
    private void getMyOpenWorkflows() {
        viewModel.getMyOpenWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's closed workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showMyClosedWorkflowsDialog(List)}.
     */
    private void getMyClosedWorkflows() {
        viewModel.getMyClosedWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's out of time workflows. This is
     * invoked by the user interaction onClick and the flow ends with the dialog display called by
     * {@link #showOutOfTimeWorkflowsDialog(List)}.
     */
    private void getOutOfTimeWorkflows() {
        viewModel.getOutOfTimeWorkflows();
    }

    /**
     * Sends a request to the ViewModel to retrieve the user's updated workflows. This is invoked by
     * the user interaction onClick and the flow ends with the dialog display called by {@link
     * #showUpdatedWorkflowsDialog(List)}.
     */
    private void getUpdatedWorkflows() {
        viewModel.getUpdatedWorkflows();
    }

    /**
     * Displays a DialogFragment with a list of the user's pending workflows.
     *
     * @param workflowList list of user's pending workflows.
     */
    private void showMyPendingWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.PENDING,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's open workflows.
     *
     * @param workflowList list of user's open workflows.
     */
    private void showMyOpenWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.WORKFLOWS,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's closed workflows.
     *
     * @param workflowList list of user's closed workflows.
     */
    private void showMyClosedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.WORKFLOWS,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's out of time workflows.
     *
     * @param workflowList list of user's out of time workflows.
     */
    private void showOutOfTimeWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.OUT_OF_TIME,
                workflowList));
    }

    /**
     * Displays a DialogFragment with a list of the user's updated workflows.
     *
     * @param workflowList list of user's updated workflows.
     */
    private void showUpdatedWorkflowsDialog(List<WorkflowDb> workflowList) {
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.UPDATED,
                workflowList));
    }
    //endregion

    /**
     * Adds data to the workflows adapter. Checks whether the adapter needs to be created or not.
     *
     * @param workflowList data to add to the adapter.
     */
    @UiThread
    private void populatePendingWorkflows(List<WorkflowDb> workflowList) {
        if (workflowList == null) return;

        if (mWorkflowsAdapter == null) {
            //create a new adapter
            mWorkflowsAdapter = new PendingWorkflowsAdapter(workflowList, this);
            mBinding.recPendingworkflows.setAdapter(mWorkflowsAdapter);
        } else {
            //append a list to the current adapter
            mWorkflowsAdapter.addData(workflowList);
        }
    }

    /**
     * Hides or shows the "SHOW MORE" button based on the parameter.
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
     * Hides or shows the workflow list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hideWorkflowList(boolean hide) {
        if (hide) {
            mBinding.recPendingworkflows.setVisibility(View.GONE);
            mBinding.lytNoworkflows.setVisibility(View.VISIBLE);
        } else {
            mBinding.recPendingworkflows.setVisibility(View.VISIBLE);
            mBinding.lytNoworkflows.setVisibility(View.GONE);
        }
    }

    @UiThread
    private void updateSelectedDatesUi(String startDate) {
        mBinding.tvSelectedDate.setText(String.format(Locale.US, "(%s)", startDate));
    }

    @UiThread
    private void updateSelectedDatesUi(String startDate, String endDate) {
        mBinding.tvSelectedDate.setText(String.format(Locale.US, "(%s - %s)", startDate, endDate));
    }

    @UiThread
    private void updateSelectedDateTitle(int titleRes) {
        mBinding.tvSelectedDateTitle.setText(getString(titleRes));
    }

    @UiThread
    private void updateMyPendingWorkflowsCount(int count) {
        mBinding.tvMyPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateMyOpenWorkflowsCount(int count) {
        mBinding.tvMyOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateMyClosedWorkflowsCount(int count) {
        mBinding.tvMyClosedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateOutOfTimeWorkflowsCount(int count) {
        mBinding.tvOutOfTimeCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUpdatedWorkflowsCount(int count) {
        mBinding.tvUpdatedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updatePendingWorkflowsCount(int count) {
        mBinding.tvPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateOpenWorkflowsCount(int count) {
        mBinding.tvOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateClosedWorkflowsCount(int count) {
        mBinding.tvClosedCount.setText(String.valueOf(count));
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
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                getContext(),
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }
}
