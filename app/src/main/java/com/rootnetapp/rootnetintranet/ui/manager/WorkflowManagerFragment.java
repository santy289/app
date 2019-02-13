package com.rootnetapp.rootnetintranet.ui.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.PendingWorkflowsAdapter;
import com.rootnetapp.rootnetintranet.ui.timeline.SelectDateDialog;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

public class WorkflowManagerFragment extends Fragment implements ManagerInterface {

    @Inject
    WorkflowManagerViewModelFactory factory;
    WorkflowManagerViewModel viewModel;

    private FragmentWorkflowManagerBinding binding;
    private MainActivityInterface anInterface;
    private String start, end, token;
    private int page = 0;
    private List<WorkflowDb> workflows;

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
        token = "Bearer " + prefs.getString("token", "");
        binding.recPendingworkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recPendingworkflows.setNestedScrollingEnabled(false);
        subscribe();
        binding.btnMonth.setOnClickListener(this::filterClicked);
        binding.btnWeek.setOnClickListener(this::filterClicked);
        binding.btnDay.setOnClickListener(this::filterClicked);
        binding.btnSelectdates.setOnClickListener(this::selectDates);
        binding.btnPendingapproval.setOnClickListener(this::showWorkflowsDialog);
        binding.btnWorkflows.setOnClickListener(this::showWorkflowsDialog);
        binding.btnOutoftime.setOnClickListener(this::showWorkflowsDialog);
        binding.btnUpdated.setOnClickListener(this::showWorkflowsDialog);
        binding.btnShowmore.setOnClickListener(this::showMoreClicked);
        start = Utils.getMonthDay(0, 1);
        end = Utils.getMonthDay(0, 30);
        binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
        binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
        start = start + "T00:00:00-0000";
        end = end + "T00:00:00-0000";
        workflows = new ArrayList<>();
        getPendingWorkflows();
        return view;
    }

    public void getPendingWorkflows() {
        Utils.showLoading(getContext());
        viewModel.getPendingWorkflows(token, page);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Utils.hideLoading();
//                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getObservableWorkflows().observe(this, this::populatePendingWorkflows);
        viewModel.getObservableError().observe(this, errorObserver);
    }

    private void selectDates(View view) {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    private void showMoreClicked(View view) {
        page++;
        getPendingWorkflows();
    }

    private void filterClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_month: {
                selectMonthButton(true);
                selectWeekButton(false);
                selectDayButton(false);

                start = Utils.getMonthDay(0, 1);
                end = Utils.getMonthDay(0, 30);
                binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
                start = start + "T00:00:00-0000";
                end = end + "T00:00:00-0000";
                break;
            }
            case R.id.btn_week: {
                selectMonthButton(false);
                selectWeekButton(true);
                selectDayButton(false);

                start = Utils.getWeekStart();
                end = Utils.getWeekEnd();
                binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_week));
                start = start + "T00:00:00-0000";
                end = end + "T00:00:00-0000";
                break;
            }
            case R.id.btn_day: {
                selectMonthButton(false);
                selectWeekButton(false);
                selectDayButton(true);

                start = Utils.getCurrentDate();
                binding.tvSelecteddates.setText("(" + start + ")");
                binding.tvSelecteddatetitle.setText(getString(R.string.today));
                start = start + "T00:00:00-0000";
                end = Utils.getCurrentDate() + "T23:59:59-0000";
                break;
            }
        }
        workflows = new ArrayList<>();
        page = 0;
        getPendingWorkflows();
    }

    @UiThread
    private void selectMonthButton(boolean select) {
        if (select) {
            binding.btnMonth.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(getContext(), R.color.selected_filter)));
            binding.btnMonth.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.btnMonth.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
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
            binding.btnWeek.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
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
            binding.btnDay.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.unselected_filter)));
            binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @Override
    public void setDate(String start, String end) {
        binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
        binding.tvSelecteddatetitle.setText(getString(R.string.selected_period));
        this.start = start + "T00:00:00-0000";
        this.end = end + "T00:00:00-0000";
        workflows = new ArrayList<>();
        page = 0;
        getPendingWorkflows();
    }

    @Override
    public void showWorkflow(WorkflowListItem workflowListItem) {
        Intent intent = new Intent(getActivity(), WorkflowDetailActivity.class);
        intent.putExtra(WorkflowDetailActivity.EXTRA_WORKFLOW_LIST_ITEM, workflowListItem);
        anInterface.showActivity(intent);
    }

    private void showWorkflowsDialog(View view) {
        switch (view.getId()) {
            case R.id.btn_pendingapproval: {
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.PENDING, workflows));
                break;
            }
            case R.id.btn_workflows: {
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.WORKFLOWS, workflows));
                break;
            }
            case R.id.btn_outoftime: {
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.OUT_OF_TIME, workflows));
                break;
            }
            case R.id.btn_updated: {
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.UPDATED, workflows));
                break;
            }
        }
    }

    @UiThread
    private void populatePendingWorkflows(WorkflowResponseDb workflowResponseDb){
        Utils.hideLoading();
        if (workflowResponseDb != null) {
            workflows.addAll(workflowResponseDb.getList());

            if (workflowResponseDb.getPager().isIsLastPage()) {
                binding.btnShowmore.setVisibility(View.GONE);
            } else {
                binding.btnShowmore.setVisibility(View.VISIBLE);
            }
            if (workflows.size() != 0) {
                binding.lytNoworkflows.setVisibility(View.GONE);
                binding.recPendingworkflows.setVisibility(View.VISIBLE);
                binding.recPendingworkflows
                        .setAdapter(new PendingWorkflowsAdapter(workflows, this));
            } else {
                binding.recPendingworkflows.setVisibility(View.GONE);
                binding.lytNoworkflows.setVisibility(View.VISIBLE);
            }
        }
    }
}
