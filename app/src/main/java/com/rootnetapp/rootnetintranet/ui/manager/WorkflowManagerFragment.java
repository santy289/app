package com.rootnetapp.rootnetintranet.ui.manager;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.PendingWorkflowsAdapter;
import com.rootnetapp.rootnetintranet.ui.timeline.SelectDateDialog;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineInterface;

public class WorkflowManagerFragment extends Fragment implements ManagerInterface {

    private FragmentWorkflowManagerBinding binding;
    private MainActivityInterface anInterface;
    private String start, end;

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
        binding.recPendingworkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recPendingworkflows.setAdapter(new PendingWorkflowsAdapter());
        //subscribe();
        binding.btnMonth.setOnClickListener(this::filterClicked);
        binding.btnWeek.setOnClickListener(this::filterClicked);
        binding.btnDay.setOnClickListener(this::filterClicked);
        binding.btnSelectdates.setOnClickListener(this::selectDates);
        binding.btnPendingapproval.setOnClickListener(this::showWorkflowsDialog);
        binding.btnWorkflows.setOnClickListener(this::showWorkflowsDialog);
        binding.btnOutoftime.setOnClickListener(this::showWorkflowsDialog);
        binding.btnUpdated.setOnClickListener(this::showWorkflowsDialog);
        start = Utils.getMonthDay(0,1);
        end = Utils.getMonthDay(0,30);
        binding.tvSelecteddates.setText("("+start+" - "+end+")");
        binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
        start = start+"T00:00:00-0000";
        end = end+"T00:00:00-0000";
        //getTimeline();
        return view;
    }

    private void selectDates(View view) {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    private void filterClicked(View view) {
        switch (view.getId()){
            case R.id.btn_month:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.white));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                start = Utils.getMonthDay(0,1);
                end = Utils.getMonthDay(0,30);
                binding.tvSelecteddates.setText("("+start+" - "+end+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
                start = start+"T00:00:00-0000";
                end = end+"T00:00:00-0000";
                break;
            }
            case R.id.btn_week:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.white));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                start = Utils.getWeekStart();
                end = Utils.getWeekEnd();
                binding.tvSelecteddates.setText("("+start+" - "+end+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.current_week));
                start = start+"T00:00:00-0000";
                end = end+"T00:00:00-0000";
                break;
            }
            case R.id.btn_day:{
                binding.btnMonth.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnWeek.setBackground(getResources().getDrawable(R.drawable.unselectedfilter_bg));
                binding.btnWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
                binding.btnDay.setBackground(getResources().getDrawable(R.drawable.selectedfilter_bg));
                binding.btnDay.setTextColor(getResources().getColor(R.color.white));
                start = Utils.getCurrentDate();
                binding.tvSelecteddates.setText("("+start+")");
                binding.tvSelecteddatetitle.setText(getString(R.string.today));
                start = start+"T00:00:00-0000";
                end = Utils.getCurrentDate()+"T23:59:59-0000";
                break;
            }
        }
        //getTimeline();
    }

    @Override
    public void setDate(String start, String end) {
        binding.tvSelecteddates.setText("("+start+" - "+end+")");
        binding.tvSelecteddatetitle.setText(getString(R.string.selected_period));
        this.start = start+"T00:00:00-0000";
        this.end = end+"T00:00:00-0000";
        //getTimeline();
    }

    private void showWorkflowsDialog(View view) {
        switch (view.getId()){
            case R.id.btn_pendingapproval:{
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.PENDING));
                break;
            }
            case R.id.btn_workflows:{
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.WORKFLOWS));
                break;
            }
            case R.id.btn_outoftime:{
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.OUT_OF_TIME));
                break;
            }
            case R.id.btn_updated:{
                anInterface.showDialog(ManagerWorkflowsDialog.newInstance(this,
                        ManagerWorkflowsDialog.DialogTypes.UPDATED));
                break;
            }
        }
    }

}
