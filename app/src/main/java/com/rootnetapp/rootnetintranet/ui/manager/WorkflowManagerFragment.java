package com.rootnetapp.rootnetintranet.ui.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.PendingWorkflowsAdapter;
import com.rootnetapp.rootnetintranet.ui.timeline.SelectDateDialog;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;

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

    private FragmentWorkflowManagerBinding binding;
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
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_manager, container, false);
        View view = binding.getRoot();
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
    }

    private void setOnClickListeners() {
        binding.btnMonth.setOnClickListener(v -> filterMonthClicked());
        binding.btnWeek.setOnClickListener(v -> filterWeekClicked());
        binding.btnDay.setOnClickListener(v -> filterDayClicked());
        binding.btnSelectdates.setOnClickListener(v -> selectDates());
        binding.btnPendingapproval.setOnClickListener(v -> showMyPendingWorkflowsDialog());
        binding.btnWorkflows.setOnClickListener(v -> showMyOpenWorkflowsDialog());
        binding.btnOutoftime.setOnClickListener(v -> showOutOfTimeWorkflowsDialog());
        binding.btnUpdated.setOnClickListener(v -> showUpdatedWorkflowsDialog());
        binding.btnShowmore.setOnClickListener(v -> showMoreClicked());
    }

    private void setupRecycler() {
        binding.recPendingworkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recPendingworkflows.setNestedScrollingEnabled(false);
    }

    private void selectDates() {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    private void showMoreClicked() {
        viewModel.incrementCurrentPage();
        viewModel.getWorkflows();
    }

    private void filterMonthClicked(){
        selectMonthButton(true);
        selectWeekButton(false);
        selectDayButton(false);

        String start = Utils.getMonthDay(0, 1);
        String end = Utils.getMonthDay(0, 30);
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.current_month);

        viewModel.updateDashboard(start, end);
    }

    private void filterWeekClicked(){
        selectMonthButton(false);
        selectWeekButton(true);
        selectDayButton(false);

        String start = Utils.getWeekStart();
        String end = Utils.getWeekEnd();
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.current_week);

        viewModel.updateDashboard(start, end);
    }

    private void filterDayClicked(){
        selectMonthButton(false);
        selectWeekButton(false);
        selectDayButton(true);

        String start = Utils.getCurrentDate();
        String end = Utils.getTomorrowDate();
        updateSelectedDatesUi(start);
        updateSelectedDateTitle(R.string.today);

        viewModel.updateDashboard(start, end);
    }

    @UiThread
    private void selectMonthButton(boolean select) {
        if (select) {
            binding.btnMonth.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.selected_filter)));
            binding.btnMonth.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.btnMonth.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
            binding.btnMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @UiThread
    private void selectWeekButton(boolean select) {
        if (select) {
            binding.btnWeek.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.selected_filter)));
            binding.btnWeek.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.btnWeek.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
            binding.btnWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @UiThread
    private void selectDayButton(boolean select) {
        if (select) {
            binding.btnDay.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.selected_filter)));
            binding.btnDay.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.btnDay.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
            binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @Override
    public void setDate(String start, String end) {
        updateSelectedDatesUi(start, end);
        updateSelectedDateTitle(R.string.selected_period);

        viewModel.resetCurrentPage();
        viewModel.updateDashboard(start, end);
    }

    @Override
    public void showWorkflow(WorkflowListItem workflowListItem) {
        Intent intent = new Intent(getActivity(), WorkflowDetailActivity.class);
        intent.putExtra(WorkflowDetailActivity.EXTRA_WORKFLOW_LIST_ITEM, workflowListItem);
        anInterface.showActivity(intent);
    }

    private void showMyPendingWorkflowsDialog(){
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.PENDING,
                viewModel.getOutOfTimeWorkflows()));
    }

    private void showMyOpenWorkflowsDialog(){
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.WORKFLOWS,
                viewModel.getOutOfTimeWorkflows()));
    }

    private void showMyClosedWorkflowsDialog(){
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.WORKFLOWS,
                viewModel.getOutOfTimeWorkflows()));
    }

    private void showOutOfTimeWorkflowsDialog(){
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.OUT_OF_TIME,
                viewModel.getOutOfTimeWorkflows()));
    }

    private void showUpdatedWorkflowsDialog(){
        anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                ManagerWorkflowsDialog.DialogTypes.UPDATED,
                viewModel.getOutOfTimeWorkflows()));
    }

    @UiThread
    private void populatePendingWorkflows(List<WorkflowDb> workflowList) {
        if (workflowList == null) return;

        if (mWorkflowsAdapter == null) {
            mWorkflowsAdapter = new PendingWorkflowsAdapter(workflowList, this);
            binding.recPendingworkflows.setAdapter(mWorkflowsAdapter);
        }

        mWorkflowsAdapter.addData(workflowList);
    }

    @UiThread
    private void hideMoreButton(boolean hide) {
        if (hide) {
            binding.btnShowmore.setVisibility(View.GONE);
        } else {
            binding.btnShowmore.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    private void hideWorkflowList(boolean hide) {
        if (hide) {
            binding.lytNoworkflows.setVisibility(View.GONE);
            binding.recPendingworkflows.setVisibility(View.VISIBLE);
        } else {
            binding.recPendingworkflows.setVisibility(View.GONE);
            binding.lytNoworkflows.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    private void updateSelectedDatesUi(String startDate) {
        binding.tvSelectedDates.setText(String.format(Locale.US, "(%s)", startDate));
    }

    @UiThread
    private void updateSelectedDatesUi(String startDate, String endDate) {
        binding.tvSelectedDates.setText(String.format(Locale.US, "(%s - %s)", startDate, endDate));
    }

    @UiThread
    private void updateSelectedDateTitle(int titleRes) {
        binding.tvSelectedDateTitle.setText(getString(titleRes));
    }

    @UiThread
    private void updateMyPendingWorkflowsCount(int count) {
        binding.tvMyPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateMyOpenWorkflowsCount(int count) {
        binding.tvMyOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateMyClosedWorkflowsCount(int count) {
        binding.tvMyClosedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateOutOfTimeWorkflowsCount(int count) {
        binding.tvOutOfTimeCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateUpdatedWorkflowsCount(int count) {
        binding.tvUpdatedCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updatePendingWorkflowsCount(int count) {
        binding.tvPendingCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateOpenWorkflowsCount(int count) {
        binding.tvOpenCount.setText(String.valueOf(count));
    }

    @UiThread
    private void updateClosedWorkflowsCount(int count) {
        binding.tvClosedCount.setText(String.valueOf(count));
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
