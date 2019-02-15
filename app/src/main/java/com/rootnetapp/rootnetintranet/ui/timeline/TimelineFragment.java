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
import com.rootnetapp.rootnetintranet.databinding.FragmentTimelineBinding;
import com.rootnetapp.rootnetintranet.databinding.TimelineFiltersMenuBinding;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUser;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivityInterface;
import com.rootnetapp.rootnetintranet.ui.timeline.adapters.TimelineAdapter;

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

import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.MODULE_ALL;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.MODULE_WORKFLOWS;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.MODULE_WORKFLOW_APPROVALS;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.MODULE_WORKFLOW_COMMENTS;
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.MODULE_WORKFLOW_FILES;

public class TimelineFragment extends Fragment implements TimelineInterface {

    @Inject
    TimelineViewModelFactory viewModelFactory;
    private TimelineViewModel viewModel;
    private FragmentTimelineBinding mBinding;
    private MainActivityInterface mMainInterface;
    private TimelineAdapter mTimelineAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(MainActivityInterface anInterface) {
        TimelineFragment fragment = new TimelineFragment();
        fragment.mMainInterface = anInterface;
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
                R.layout.fragment_timeline, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(TimelineViewModel.class);
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
        viewModel.getObservableTimeline().observe(this, this::populateTimeline);
        viewModel.getObservableHideMoreButton().observe(this, this::hideMoreButton);
        viewModel.getObservableHideTimelineList().observe(this, this::hideTimelineList);
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

        mBinding.imgFilter.setOnClickListener(v -> {
            PopupWindow popupWindow = createPopupMenu();
            popupWindow.showAsDropDown(v, -40, 18);
        });
    }

    /**
     * Initializes the timeline RecyclerView.
     */
    private void setupRecycler() {
        mBinding.recTimeline.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recTimeline.setNestedScrollingEnabled(false);
    }

    /**
     * Resets the timeline adapter and performs a request to the ViewModel to update the timeline
     * data with new date filters
     *
     * @param startDate start date filter.
     * @param endDate   end date filter.
     */
    private void updateTimeline(String startDate, String endDate) {
        mTimelineAdapter = null;
        viewModel.updateTimeline(startDate, endDate);
    }

    private void updateTimeline() {
        mTimelineAdapter = null;
        viewModel.updateTimeline();
    }

    private void updateTimelineWithUsers(List<String> users) {
        mTimelineAdapter = null;
        viewModel.updateTimelineWithUsers(users);
    }

    private void updateTimelineWithModules(List<String> modules) {
        mTimelineAdapter = null;
        viewModel.updateTimelineWithModules(modules);
    }

    //region Date Filters

    /**
     * Opens the select date dialog.
     */
    private void selectDates() {
        mMainInterface.showDialog(SelectDateDialog.newInstance(this));
    }

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

        updateTimeline(start, end);
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

        updateTimeline(start, end);
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

        updateTimeline(start, end);
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

        updateTimeline(start, end);
    }
    //endregion

    /**
     * Performs a request to the ViewModel to retrieve more workflows.
     */
    private void showMoreClicked() {
        viewModel.incrementCurrentPage();
        viewModel.getTimeline();
    }

    @Override
    public void reload() {
        updateTimeline();
    }

    private PopupWindow createPopupMenu() {
        final PopupWindow popupWindow = new PopupWindow(getContext());

        // inflate your layout or dynamically add view
        TimelineFiltersMenuBinding filtersBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.timeline_filters_menu, null, false);
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.filters_width));
        popupWindow.setHeight((int) getResources().getDimension(R.dimen.filters_height));
        popupWindow.setContentView(filtersBinding.getRoot());

        filtersBinding.switchAllModules.setChecked(false);
        filtersBinding.switchWorkflows.setChecked(false);
        filtersBinding.switchApprovals.setChecked(false);
        filtersBinding.switchFiles.setChecked(false);
        filtersBinding.switchComments.setChecked(false);

        List<String> modules = viewModel.getSelectedModules();
        for (String string : modules) {
            if (string.equals(MODULE_ALL)) {
                filtersBinding.switchAllModules.setChecked(true);
            }
            else if (string.equals(MODULE_WORKFLOWS)) {
                filtersBinding.switchWorkflows.setChecked(true);
            }
            else if (string.equals(MODULE_WORKFLOW_APPROVALS)) {
                filtersBinding.switchApprovals.setChecked(true);
            }
            else if (string.equals(MODULE_WORKFLOW_FILES)) {
                filtersBinding.switchFiles.setChecked(true);
            }
            else if (string.equals(MODULE_WORKFLOW_COMMENTS)) {
                filtersBinding.switchComments.setChecked(true);
            }
        }

        filtersBinding.switchAllModules.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchWorkflows.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchApprovals.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchFiles.setOnClickListener(this::onSwitchClicked);
        filtersBinding.switchComments.setOnClickListener(this::onSwitchClicked);

        List<WorkflowUser> workflowUsers = viewModel.getAllWorkflowUsers();
        for (WorkflowUser workflowUser : workflowUsers) {
            LayoutInflater vi = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.prototype_user, null);
            filtersBinding.lyt.addView(v,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView title = v.findViewById(R.id.field_name);
            title.setText(workflowUser.getUsername());

            Switch switchView = v.findViewById(R.id.field_swtch);
            switchView.setChecked(false);
            switchView.setTag(workflowUser.getId());
            switchView.setOnClickListener(this::userSwitchClicked);

            List<String> users = viewModel.getSelectedUsers();
            for (String user : users) {
                if (user.equals(String.valueOf(workflowUser.getId()))) {
                    switchView.setChecked(true);
                }
            }
        }

        return popupWindow;
    }

    private void userSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();
        int id = (int) view.getTag();
        int i = 0;

        List<String> users = viewModel.getAllUsers();
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

        updateTimelineWithUsers(users);
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

        List<String> modules = viewModel.getAllModules();
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

        updateTimelineWithModules(modules);
    }

    /**
     * Adds data to the timeline adapter. Checks whether the adapter needs to be created or not.
     *
     * @param timelineUiData data to add to the adapter.
     */
    @UiThread
    private void populateTimeline(TimelineUiData timelineUiData) {
        if (mTimelineAdapter == null) {
            //create a new adapter
            mTimelineAdapter = new TimelineAdapter(
                    timelineUiData.getTimelineItems(),
                    timelineUiData.getUsers(),
                    timelineUiData.getInteractionComments(),
                    viewModel,
                    this,
                    this
            );
            mBinding.recTimeline.setAdapter(mTimelineAdapter);
        } else {
            //append a list to the current adapter
            mTimelineAdapter.addData(timelineUiData.getTimelineItems(),
                    timelineUiData.getInteractionComments());
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
     * Hides or shows the timeline list based on the parameter.
     *
     * @param hide true: hide; false: show.
     */
    @UiThread
    private void hideTimelineList(boolean hide) {
        hideMoreButton(hide);

        if (hide) {
            mBinding.recTimeline.setVisibility(View.GONE);
            mBinding.lytNotimeline.setVisibility(View.VISIBLE);
        } else {
            mBinding.recTimeline.setVisibility(View.VISIBLE);
            mBinding.lytNotimeline.setVisibility(View.GONE);
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