package com.rootnetapp.rootnetintranet.ui.timeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import com.rootnetapp.rootnetintranet.models.responses.timeline.ItemComments;
import com.rootnetapp.rootnetintranet.models.responses.timeline.TimelineItem;
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
        subscribe();
        binding.btnMonth.setOnClickListener(this::filterClicked);
        binding.btnWeek.setOnClickListener(this::filterClicked);
        binding.btnDay.setOnClickListener(this::filterClicked);
        binding.btnSelectdates.setOnClickListener(this::selectDates);
        start = Utils.getMonthDay(0, 1);
        end = Utils.getMonthDay(0, 30);
        binding.tvSelecteddates.setText("(" + start + " - " + end + ")");
        binding.tvSelecteddatetitle.setText(getString(R.string.current_month));
        start = start + "T00:00:00-0000";
        end = end + "T00:00:00-0000";
        users = new ArrayList<>();
        modules = new ArrayList<>();
        //salesforce-status
        modules.add("crm_sprint_metadata_status");
        //tracks
        modules.add("crm_contact_tracking");
        //accounts
        modules.add("crm_contact");
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
        getTimeline();
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

        final Observer<List<ItemComments>> commentsObserver = ((List<ItemComments> data) -> {
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
            if (string.equals("crm_sprint_metadata_status")) {
                filtersBinding.swchSales.setChecked(true);
            }
            if (string.equals("crm_contact_tracking")) {
                filtersBinding.swchTracking.setChecked(true);
            }
            if (string.equals("crm_contact")) {
                filtersBinding.swchAccounts.setChecked(true);
            }
        }

        filtersBinding.swchSales.setOnClickListener(this::onSwitchClicked);
        filtersBinding.swchTracking.setOnClickListener(this::onSwitchClicked);
        filtersBinding.swchAccounts.setOnClickListener(this::onSwitchClicked);

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
            case R.id.swch_sales: {
                type = "crm_sprint_metadata_status";
                break;
            }
            case R.id.swch_tracking: {
                type = "crm_contact_tracking";
                break;
            }
            case R.id.swch_accounts: {
                type = "crm_contact";
                break;
            }
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
