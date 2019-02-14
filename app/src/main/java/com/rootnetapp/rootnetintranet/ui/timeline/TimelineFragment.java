package com.rootnetapp.rootnetintranet.ui.timeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.FragmentTimelineBinding;
import com.rootnetapp.rootnetintranet.databinding.TimelineFiltersMenuBinding;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
import com.rootnetapp.rootnetintranet.models.responses.timeline.interaction.Interaction;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.timeline.adapters.TimelineAdapter;

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

public class TimelineFragment extends Fragment implements TimelineInterface {

    private static final String MODULE_ALL = "all";
    private static final String MODULE_WORKFLOWS = "intranet_workflow_reports";
    private static final String MODULE_WORKFLOW_APPROVALS = "intranet_workflow_status_approve";
    private static final String MODULE_WORKFLOW_FILES = "intranet_workflow_file_record";
    private static final String MODULE_WORKFLOW_COMMENTS = "intranet_workflow_comment";

    @Inject
    TimelineViewModelFactory viewModelFactory;
    private TimelineViewModel viewModel;
    private FragmentTimelineBinding binding;
    private MainActivityInterface anInterface;
    private String token;
    private List<TimelineItem> timelineItems;
    private List<User> timelineUsers;
    private List<String> users;
    private List<String> modules;
    private String start, end;
    private List<WorkflowUser> workflowUsers;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(MainActivityInterface anInterface) {
        TimelineFragment fragment = new TimelineFragment();
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
                R.layout.fragment_timeline, container, false);
        View view = binding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(TimelineViewModel.class);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");
        binding.recTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recTimeline.setNestedScrollingEnabled(false);
        subscribe();
        binding.tvMonth.setOnClickListener(this::filterClicked);
        binding.tvWeek.setOnClickListener(this::filterClicked);
        binding.tvDay.setOnClickListener(this::filterClicked);
        binding.btnSelectdates.setOnClickListener(this::selectDates);
        start = Utils.getMonthDay(0, 1);
        end = Utils.getMonthDay(0, 30);
        binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
        binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
        start = start + "T00:00:00-0000";
        end = end + "T00:00:00-0000";
        users = new ArrayList<>();

        modules = new ArrayList<>();
        modules.add(MODULE_ALL);
        modules.add(MODULE_WORKFLOWS);
        modules.add(MODULE_WORKFLOW_APPROVALS);
        modules.add(MODULE_WORKFLOW_FILES);
        modules.add(MODULE_WORKFLOW_COMMENTS);

        binding.imgFilter.setOnClickListener(view1 -> {
            PopupWindow popupwindow_obj = popupMenu();
            popupwindow_obj.showAsDropDown(binding.imgFilter, -40, 18);
        });
        getTimeline();
        return view;
    }

    private void selectDates(View view) {
        anInterface.showDialog(SelectDateDialog.newInstance(this));
    }

    private void filterClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_month: {
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
            case R.id.tv_week: {
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
            case R.id.tv_day: {
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
        getTimeline();
    }

    @UiThread
    private void selectMonthButton(boolean select) {
        if (select) {
            binding.tvMonth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_filter));
            binding.tvMonth.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.tvMonth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected_filter));
            binding.tvMonth.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @UiThread
    private void selectWeekButton(boolean select) {
        if (select) {
            binding.tvWeek.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_filter));
            binding.tvWeek.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.tvWeek.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected_filter));
            binding.tvWeek.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    @UiThread
    private void selectDayButton(boolean select) {
        if (select) {
            binding.tvDay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected_filter));
            binding.tvDay.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.tvDay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.unselected_filter));
            binding.tvDay.setTextColor(getResources().getColor(R.color.unselected_filter_text));
        }
    }

    private void getTimeline() {
        Utils.showLoading(getContext());
        viewModel.getUsers(token);
    }

    private void subscribe() {

        final Observer<List<User>> usersObserver = ((List<User> data) -> {
            if (null != data) {
                timelineUsers = data;
                viewModel.getWorkflowUsers(token);
            } else {
                Utils.hideLoading();
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<List<WorkflowUser>> workflowUsersObserver = ((List<WorkflowUser> data) -> {
            if (null != data) {
                workflowUsers = data;
                for (WorkflowUser user : data) {
                    users.add(String.valueOf(user.getId()));
                }
                viewModel.getTimeline(token, start, end, users, modules);
            } else {
                Utils.hideLoading();
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<List<TimelineItem>> timelineObserver = ((List<TimelineItem> data) -> {
            if (null != data) {
                timelineItems = data;
                viewModel.getComments(token);
            } else {
                Utils.hideLoading();
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<List<Interaction>> commentsObserver = ((List<Interaction> data) -> {
            Utils.hideLoading();
            if (null != data) {
                if (timelineItems.size() != 0) {
                    binding.lytNotimeline.setVisibility(View.GONE);
                    binding.recTimeline.setVisibility(View.VISIBLE);
                    binding.recTimeline.setAdapter(new TimelineAdapter(timelineItems, timelineUsers,
                            data, viewModel, token, this, this));
                } else {
                    binding.recTimeline.setVisibility(View.GONE);
                    binding.lytNotimeline.setVisibility(View.VISIBLE);
                }
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                //TODO mejorar toast
                Utils.hideLoading();
                Toast.makeText(getContext(), getString(data), Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getObservableTimeline().observe(this, timelineObserver);
        viewModel.getObservableUsers().observe(this, usersObserver);
        viewModel.getObservableWorkflowUsers().observe(this, workflowUsersObserver);
        viewModel.getObservableComments().observe(this, commentsObserver);
        viewModel.getObservableError().observe(this, errorObserver);

    }

    @Override
    public void setDate(String start, String end) {
        binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
        binding.tvSelecteddatetitle.setText(getString(R.string.selected_period));
        this.start = start + "T00:00:00-0000";
        this.end = end + "T00:00:00-0000";
        getTimeline();
    }

    @Override
    public void reload() {
        getTimeline();
    }

    private PopupWindow popupMenu() {
        final PopupWindow popupWindow = new PopupWindow(getContext());

        // inflate your layout or dynamically add view
        TimelineFiltersMenuBinding filtersBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.timeline_filters_menu, null, false);
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.filters_width));
        popupWindow.setHeight((int) getResources().getDimension(R.dimen.filters_height));
        popupWindow.setContentView(filtersBinding.getRoot());

        for (String string : modules) {
            if (string.equals(MODULE_ALL)) {
                filtersBinding.switchAllModules.setChecked(true);
            }
            if (string.equals(MODULE_WORKFLOWS)) {
                filtersBinding.switchWorkflows.setChecked(true);
            }
            if (string.equals(MODULE_WORKFLOW_APPROVALS)) {
                filtersBinding.switchApprovals.setChecked(true);
            }
            if (string.equals(MODULE_WORKFLOW_FILES)) {
                filtersBinding.switchFiles.setChecked(true);
            }
            if (string.equals(MODULE_WORKFLOW_COMMENTS)) {
                filtersBinding.switchComments.setChecked(true);
            }
        }

        filtersBinding.switchAllModules.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchWorkflows.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchApprovals.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchFiles.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchComments.setOnClickListener(this::onSwitchClicked);

        for (WorkflowUser user : workflowUsers) {
            LayoutInflater vi = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.prototype_user, null);
            filtersBinding.lyt.addView(v,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView title = v.findViewById(R.id.field_name);
            title.setText(user.getUsername());
            v.findViewById(R.id.field_swtch).setTag(user.getId());
            v.findViewById(R.id.field_swtch).setOnClickListener(this::userSwitchClicked);
        }

        return popupWindow;
    }

    private void userSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();
        int id = (int) view.getTag();
        int i = 0;

        while (i < users.size()) {
            if (users.get(i).equals("undefined")) {
                users.remove(i);
            }
            i++;
        }

        if (checked) {
            users.add(String.valueOf(id));
        } else {
            i = 0;
            while (i < users.size()) {
                if (users.get(i).equals(String.valueOf(id))) {
                    users.remove(i);
                }
                i++;
            }
        }

        if (users.size() == 0) {
            users.add("undefined");
        }

    }

    private void onSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();

        String type = "";
        switch (view.getId()) {
            case R.id.switch_all_modules:
                type = MODULE_ALL;
                break;

            case R.id.switch_workflows:
                type = MODULE_WORKFLOWS;
                break;

            case R.id.switch_approvals:
                type = MODULE_WORKFLOW_APPROVALS;
                break;

            case R.id.switch_files:
                type = MODULE_WORKFLOW_FILES;
                break;

            case R.id.switch_comments:
                type = MODULE_WORKFLOW_COMMENTS;
                break;

        }
        int i = 0;

        while (i < modules.size()) {
            if (modules.get(i).equals("undefined")) {
                modules.remove(i);
            }
            i++;
        }

        if (checked) {
            modules.add(type);
        } else {
            i = 0;
            while (i < modules.size()) {
                if (modules.get(i).equals(type)) {
                    modules.remove(i);
                }
                i++;
            }
        }

        if (modules.size() == 0) {
            modules.add("undefined");
        }

        getTimeline();
    }
}