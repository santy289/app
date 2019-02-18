package com.rootnetapp.rootnetintranet.ui.timeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import static com.rootnetapp.rootnetintranet.ui.timeline.TimelineViewModel.USER_ALL;

public class TimelineFragment extends Fragment implements TimelineInterface {

    @Inject
    TimelineViewModelFactory viewModelFactory;
    private TimelineViewModel viewModel;
    private FragmentTimelineBinding mBinding;
    private MainActivityInterface mMainInterface;
    private TimelineAdapter mTimelineAdapter;
    private TimelineFiltersMenuBinding mFiltersBinding;

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
        viewModel.getObservablePostInteraction().observe(this, this::updateInteraction);
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

    //region TimelineInterface
    @Override
    public void reload() {
        updateTimeline();
    }

    @Override
    public void addCommentClicked(String comment, User author, TimelineItem timelineItem,
                                  int interactionId) {
        if (TextUtils.isEmpty(comment)) {
            showToastMessage(R.string.empty_comment);
            return;
        }

        if (author == null) {
            showToastMessage(R.string.error);
            return;
        }

        viewModel.postComment(interactionId, timelineItem.getEntityId(),
                timelineItem.getEntity(), comment, author.getUserId());

        hideSoftInputKeyboard();
    }

    @Override
    public void likeClicked(User author, TimelineItem timelineItem, int interactionId) {
        if (author == null) {
            showToastMessage(R.string.error);
            return;
        }

        viewModel.postLike(interactionId, timelineItem.getEntityId(),
                timelineItem.getEntity(), author.getUserId());
    }

    @Override
    public void dislikeClicked(User author, TimelineItem timelineItem, int interactionId) {
        if (author == null) {
            showToastMessage(R.string.error);
            return;
        }

        viewModel.postDislike(interactionId, timelineItem.getEntityId(),
                timelineItem.getEntity(), author.getUserId());
    }
    //endregion

    private PopupWindow createPopupMenu() {
        final PopupWindow popupWindow = new PopupWindow(getContext());

        // inflate your layout or dynamically add view
        mFiltersBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.timeline_filters_menu, null, false);
        popupWindow.setFocusable(true);
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.filters_width));
        popupWindow.setHeight((int) getResources().getDimension(R.dimen.filters_height));
        popupWindow.setContentView(mFiltersBinding.getRoot());

        mFiltersBinding.switchAllModules.setChecked(false);
        mFiltersBinding.switchWorkflows.setChecked(false);
        mFiltersBinding.switchApprovals.setChecked(false);
        mFiltersBinding.switchFiles.setChecked(false);
        mFiltersBinding.switchComments.setChecked(false);

        List<String> modules = viewModel.getSelectedModules();
        for (String string : modules) {
            if (string.equals(MODULE_ALL)) {
                mFiltersBinding.switchAllModules.setChecked(true);
            } else if (string.equals(MODULE_WORKFLOWS)) {
                mFiltersBinding.switchWorkflows.setChecked(true);
            } else if (string.equals(MODULE_WORKFLOW_APPROVALS)) {
                mFiltersBinding.switchApprovals.setChecked(true);
            } else if (string.equals(MODULE_WORKFLOW_FILES)) {
                mFiltersBinding.switchFiles.setChecked(true);
            } else if (string.equals(MODULE_WORKFLOW_COMMENTS)) {
                mFiltersBinding.switchComments.setChecked(true);
            }
        }

        mFiltersBinding.switchAllModules.setOnClickListener(this::onSwitchClicked);
        mFiltersBinding.switchWorkflows.setOnClickListener(this::onSwitchClicked);
        mFiltersBinding.switchApprovals.setOnClickListener(this::onSwitchClicked);
        mFiltersBinding.switchFiles.setOnClickListener(this::onSwitchClicked);
        mFiltersBinding.switchComments.setOnClickListener(this::onSwitchClicked);

        List<WorkflowUser> workflowUsers = viewModel.getAllWorkflowUsers();

        if (workflowUsers == null) return popupWindow;

        //add "All" filter
        addUserRow(
                mFiltersBinding,
                USER_ALL,
                getString(R.string.all)
        );

        for (WorkflowUser workflowUser : workflowUsers) {
            addUserRow(
                    mFiltersBinding,
                    String.valueOf(workflowUser.getUserId()),
                    workflowUser.getUsername()
            );
        }

        return popupWindow;
    }

    private void addUserRow(TimelineFiltersMenuBinding filtersMenuBinding, String id, String name) {
        LayoutInflater vi = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.prototype_user, null);
        filtersMenuBinding.lyt.addView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView title = v.findViewById(R.id.field_name);
        title.setText(name);

        Switch switchView = v.findViewById(R.id.field_swtch);
        switchView.setChecked(false);
        switchView.setTag(id);
        switchView.setOnClickListener(this::userSwitchClicked);

        List<String> users = viewModel.getSelectedUsers();
        for (String user : users) {
            if (user.equals(id)) {
                switchView.setChecked(true);
            }
        }
    }

    private void userSwitchClicked(View view) {
        boolean checked = ((Switch) view).isChecked();
        String id = (String) view.getTag();

        if (id.equals(USER_ALL)) {
            for (int i = 0; i < mFiltersBinding.lyt.getChildCount(); i++) {
                View child = mFiltersBinding.lyt.getChildAt(i);

                Switch switchView = child.findViewById(R.id.field_swtch);
                if (switchView == null) continue;

                switchView.setChecked(checked);
            }
        } else {
            boolean isAllSelected = true;
            Switch userAllSwitch = null;

            for (int j = 0; j < mFiltersBinding.lyt.getChildCount(); j++) {
                View child = mFiltersBinding.lyt.getChildAt(j);

                Switch switchView = child.findViewById(R.id.field_swtch);

                if (switchView == null) continue;
                if (switchView.getTag().equals(USER_ALL)) {
                    userAllSwitch = switchView;
                    continue;
                }

                isAllSelected &= switchView.isChecked();
            }

            if (userAllSwitch != null) userAllSwitch.setChecked(isAllSelected);
        }

        List<String> users = new ArrayList<>();

        for (int j = 0; j < mFiltersBinding.lyt.getChildCount(); j++) {
            View child = mFiltersBinding.lyt.getChildAt(j);

            Switch switchView = child.findViewById(R.id.field_swtch);

            //do not add if null or if it's the "All" filter
            if (switchView == null || switchView.getTag().equals(USER_ALL)) continue;

            if (switchView.isChecked()) users.add((String) switchView.getTag());
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

        if (type.equals(MODULE_ALL)) {
            mFiltersBinding.switchWorkflows.setChecked(checked);
            mFiltersBinding.switchApprovals.setChecked(checked);
            mFiltersBinding.switchFiles.setChecked(checked);
            mFiltersBinding.switchComments.setChecked(checked);
        } else {
            boolean isAllSelected = mFiltersBinding.switchWorkflows.isChecked()
                    && mFiltersBinding.switchApprovals.isChecked()
                    && mFiltersBinding.switchFiles.isChecked()
                    && mFiltersBinding.switchComments.isChecked();
            mFiltersBinding.switchAllModules.setChecked(isAllSelected);
        }

        List<String> modules = new ArrayList<>();

        if (mFiltersBinding.switchWorkflows.isChecked()) modules.add(MODULE_WORKFLOWS);
        if (mFiltersBinding.switchApprovals.isChecked()) modules.add(MODULE_WORKFLOW_APPROVALS);
        if (mFiltersBinding.switchFiles.isChecked()) modules.add(MODULE_WORKFLOW_FILES);
        if (mFiltersBinding.switchComments.isChecked()) modules.add(MODULE_WORKFLOW_COMMENTS);

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
    private void updateInteraction(Interaction interaction) {
        mTimelineAdapter.updateInteraction(interaction);
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

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}