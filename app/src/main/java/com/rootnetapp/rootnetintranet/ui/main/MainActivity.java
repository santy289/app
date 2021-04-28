package com.rootnetapp.rootnetintranet.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityMainBinding;
import com.rootnetapp.rootnetintranet.fcm.FirebaseTopics;
import com.rootnetapp.rootnetintranet.fcm.NotificationDataKeys;
import com.rootnetapp.rootnetintranet.models.createworkflow.form.BaseFormItem;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.createworkflow.CreateWorkflowFragmentInterface;
import com.rootnetapp.rootnetintranet.ui.createworkflow.FormType;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.qrtoken.QRTokenActivity;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickActionsActivity;
import com.rootnetapp.rootnetintranet.ui.resourcing.planner.ResourcingPlannerActivity;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;
import com.rootnetapp.rootnetintranet.ui.workflowlist.Sort;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerOptionsAdapter;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_TYPE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CLEAR_ALL;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_UPDATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_CLEAR_ALL;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_UPDATED_DATE;

public class MainActivity extends AppCompatActivity
        implements MainActivityInterface, PopupMenu.OnMenuItemClickListener,
        CreateWorkflowFragment.OnValueSelectedListener {

    @Inject
    MainActivityViewModelFactory profileViewModelFactory;
    MainActivityViewModel viewModel;
    private ActivityMainBinding mainBinding;
    private FragmentManager fragmentManager;
    private MenuItem mSearch = null;

    RightDrawerOptionsAdapter rightDrawerOptionsAdapter;
    RightDrawerFiltersAdapter rightDrawerFiltersAdapter;

    private static final String TAG = "MainActivity";
    private CreateWorkflowFragment mDynamicFiltersFragment;
    private CreateWorkflowFragment mStandardFiltersFragment;
    private boolean digitalSignatureEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        mainBinding.navView.setCheckedItem(R.id.nav_timeline);
        SharedPreferences sharedPref = getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        digitalSignatureEnabled = sharedPref.getBoolean(PreferenceKeys.PREF_SIGNATURE, false);

        viewModel = ViewModelProviders
                .of(this, profileViewModelFactory)
                .get(MainActivityViewModel.class);

        fragmentManager = getSupportFragmentManager();
        setActionBar();
        subscribe();
        initActionListeners();
        viewModel.initMainViewModel(sharedPref);

        //fixme temporary setup of Workflows as the initial tab
        showFragment(WorkflowFragment.newInstance(this), false);
//        showFragment(TimelineFragment.newInstance(this), false);
        setFilterBoxListeners();
        setupBottomNavigation();
        subscribeToFcmTopics();
        checkForExternalIntent();
    }

    private void checkForExternalIntent() {
        String workflowId = getIntent().getStringExtra(NotificationDataKeys.KEY_WORKFLOW_ID);
        // If id is defined, then this activity was launched from a push notification
        if (!TextUtils.isEmpty(workflowId)) {
            goToWorkflowDetail(workflowId);
        }
    }

    private void subscribeToFcmTopics() {
        subscribeToFcmTopic(FirebaseTopics.WORKFLOWS);
    }

    private void subscribeToFcmTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> Log.d(TAG,
                        "FCM subscribe to topic: " + topic + " - " + task.isSuccessful()));
    }

    private void setToolbarTitle(CharSequence title) {
        mainBinding.toolbarTitle.setText(title);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                showFragment(ProfileFragment.newInstance(), false);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void showFragment(Fragment fragment, boolean addtobackstack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        if (addtobackstack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        hideSoftInputKeyboard();
    }

    public void showDynamicFiltersFragment(Fragment fragment, boolean addtobackstack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fl_dynamic_filters, fragment);
        if (addtobackstack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        hideSoftInputKeyboard();
    }

    public void showStandardFiltersFragment(Fragment fragment, boolean addtobackstack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fl_standard_filters, fragment);
        if (addtobackstack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        hideSoftInputKeyboard();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        //check if the fragment has priority over the onBackPressed callback
        if (fragment instanceof CreateWorkflowFragmentInterface && ((CreateWorkflowFragmentInterface) fragment)
                .onBackPressed()) {
            return;
        }

        DrawerLayout drawer = mainBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showActivity(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    @Override
    public void showActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showDialog(DialogFragment dialogFragment) {
        disposeDialog();
        dialogFragment.show(fragmentManager, "dialog");
    }

    @Override
    public void disposeDialog() {
        Fragment frag = fragmentManager.findFragmentByTag("dialog");
        if (frag != null) {
            fragmentManager.beginTransaction().remove(frag).commit();
        }
    }

    @Override
    public void showWorkflow(int id) {
        viewModel.getWorkflow(id);
    }

    private void dozeModeWhitelist() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
            Log.d(TAG, "dozeModeWhitelist: nothing to do");
        } else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    protected void setImageIn(String[] content) {
        RequestBuilder builder = Glide.with(this).load(content[1]);
        switch (content[0]) {
            case MainActivityViewModel.IMG_LOGO:
                builder.into(mainBinding.leftDrawer.imgLogo);
                break;
            case MainActivityViewModel.IMG_TOOLBAR:
                builder.into(mainBinding.toolbarImage);
                break;
        }
    }

    private void setupBottomNavigation() {
        //fixme temporary setup of Workflows as the initial tab
        mainBinding.bottomNavigation.setSelectedItemId(R.id.menu_workflow_list);
        setToolbarTitle(getString(R.string.bottom_nav_workflow_list));

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                item -> {
                    handleBottomNavigationSelection(item);
                    return false;
                });
    }

    private void handleBottomNavigationSelection(@NonNull MenuItem item) {
        item.setChecked(true);
        setToolbarTitle(item.getTitle());

        switch (item.getItemId()) {
            case R.id.menu_timeline:
                showFragment(TimelineFragment.newInstance(this), false);
                break;

            case R.id.menu_workflow_list:
                showFragment(WorkflowFragment.newInstance(this), false);
                break;

            case R.id.menu_dashboard:
                showFragment(WorkflowManagerFragment.newInstance(this), false);
                break;
        }
    }

    private void setupSpeedDialFab(QuickActionsVisibility visibility) {
        mainBinding.fabSpeedDial.clearActionItems();

        int count = 0;

        //reverse order
        if (visibility.isShowComment()) {
            addActionItem(R.id.fab_comment, R.string.quick_actions_comment,
                    R.drawable.ic_message_black_24dp);
            count++;
        }

        if (visibility.isShowChangeStatus()) {
            addActionItem(R.id.fab_change_status, R.string.quick_actions_change_status,
                    R.drawable.ic_compare_arrows_black_24dp);
            count++;
        }

        if (visibility.isShowApprove()) {
            addActionItem(R.id.fab_approve_workflow, R.string.quick_actions_approve_workflow,
                    R.drawable.ic_like_black_24dp);
            count++;
        }

        if (visibility.isShowEdit()) {
            addActionItem(R.id.fab_edit_workflow, R.string.quick_actions_edit_workflow,
                    R.drawable.ic_workflow_black_24dp);
            count++;
        }

        if (digitalSignatureEnabled) {
            addActionItem(R.id.fab_digital_signature, R.string.workflow_detail_signature_fragment_title,
                    R.drawable.ic_file_black);
            count++;
        }

        if (count > 0) {
            mainBinding.fabSpeedDial.setOnActionSelectedListener(this::handleSpeedDialClick);
            mainBinding.fabSpeedDial.getMainFab().setSupportImageTintList(ColorStateList.valueOf(
                    Color.WHITE)); //this is the only way to change the icon color
        } else {
            mainBinding.fabSpeedDial.setVisibility(View.GONE);
        }
    }

    private void addActionItem(@IdRes int idRes, @StringRes int titleRes,
                               @DrawableRes int drawableRes) {
        mainBinding.fabSpeedDial.addActionItem(
                new SpeedDialActionItem.Builder(idRes, drawableRes)
                        .setLabel(getString(titleRes))
                        .setFabBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        .setFabImageTintColor(ContextCompat.getColor(this, R.color.black))
                        .setLabelBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        .setLabelColor(ContextCompat.getColor(this, R.color.dark_gray))
                        .setLabelClickable(false)
                        .create()
        );
    }

    private boolean handleSpeedDialClick(SpeedDialActionItem speedDialActionItem) {
        @QuickAction int quickAction;
        switch (speedDialActionItem.getId()) {

            case R.id.fab_edit_workflow:
                quickAction = QuickAction.EDIT_WORKFLOW;
                break;

            case R.id.fab_approve_workflow:
                quickAction = QuickAction.APPROVE_WORKFLOW;
                break;

            case R.id.fab_change_status:
                quickAction = QuickAction.CHANGE_STATUS;
                break;

            case R.id.fab_comment:
                quickAction = QuickAction.COMMENT;
                break;

            case R.id.fab_digital_signature:
                quickAction = QuickAction.DIGITAL_SIGNATURE;
                break;
            default:
                return false;
        }

        Intent intent = new Intent(this, QuickActionsActivity.class);
        intent.putExtra(QuickActionsActivity.EXTRA_ACTION, quickAction);
        intent.putExtra(QuickActionsActivity.EXTRA_TITLE, speedDialActionItem.getLabel(this));
        startActivity(intent);

        return false; // true to keep the Speed Dial open
    }

    private void initActionListeners() {
        mainBinding.leftDrawer.navWorkflows.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navResourcing.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navSync.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navProfile.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navLoginQr.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navExit.setOnClickListener(this::drawerClicks);
        mainBinding.rightDrawer.drawerBackButton.setOnClickListener(view -> {
            if (sortingActive) {
                showSortByViews(false);
            }
            if (standardFiltersActive) {
                showStandardFiltersView(false);
            }
            if (dynamicFiltersActive) {
                showDynamicFiltersView(false);
            }
            mainBinding.rightDrawer.rightDrawerFilters.setVisibility(View.GONE);
            viewModel.sendRightDrawerBackButtonClick();
            hideSoftInputKeyboard();
        });

        mainBinding.rightDrawer.rightDrawerSort.setOnClickListener(view -> {
            // TODO tell WorkflowViewModel to show next Sort By Views
            // right now main activity is doing it on its own, it is better that the viewModel does this.
            showSortByViews(true);
        });

        mainBinding.rightDrawer.rightDrawerStandardFilters.setOnClickListener(view -> {
            showStandardFiltersView(true);
        });

        mainBinding.rightDrawer.rightDrawerDynamicFilters.setOnClickListener(view -> {
            showDynamicFiltersView(true);
        });

        // Using the workflow type field filter.
        mainBinding.rightDrawer.rightDrawerWorkflowType.setOnClickListener(view -> {
            viewModel.sendWorkflowTypeFilterClicked();
        });

        // Using the base field filter.
        mainBinding.rightDrawer.rightDrawerBaseFilters.setOnClickListener(view -> {
            viewModel.sendBaseFiltersClicked();
        });

        // Using the status field filter.
        mainBinding.rightDrawer.rightDrawerStatusFilters.setOnClickListener(view -> {
            viewModel.sendStatusFiltersClicked();
        });

        // Using the system status field filter.
        mainBinding.rightDrawer.rightDrawerSystemStatusFilters.setOnClickListener(view -> {
            viewModel.sendSystemStatusFiltersClicked();
        });

        // Using the system status field filter.
        mainBinding.rightDrawer.rightDrawerRestoreDefaults.setOnClickListener(view -> {
            restoreFilterDefaults();
        });

        mainBinding.toolbarImage.setOnClickListener(
                v -> showFragment(ProfileFragment.newInstance(), false));

        mDynamicFiltersFragment = CreateWorkflowFragment
                .newInstance(FormType.DYNAMIC_FILTERS, this);
        showDynamicFiltersFragment(mDynamicFiltersFragment, false);

        mStandardFiltersFragment = CreateWorkflowFragment.newInstance(FormType.STANDARD_FILTERS, this);
        showStandardFiltersFragment(
                mStandardFiltersFragment, false);
    }

    private void restoreFilterDefaults() {
        mDynamicFiltersFragment = CreateWorkflowFragment
                .newInstance(FormType.DYNAMIC_FILTERS, this);
        showDynamicFiltersFragment(mDynamicFiltersFragment, false);

        mStandardFiltersFragment = CreateWorkflowFragment.newInstance(FormType.STANDARD_FILTERS, this);
        showStandardFiltersFragment(
                mStandardFiltersFragment, false);

        updateSortFieldSelection(R.string.no_selection);
        toggleRadioButtonFilter(RADIO_CLEAR_ALL, false);
        toggleAscendingDescendingSwitch(SWITCH_CLEAR_ALL, false);
        viewModel.sendClearFiltersClicked();
    }

    private void openRightDrawer(boolean open) {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            return;
        }
        mainBinding.drawerLayout.openDrawer(GravityCompat.END);
    }

    boolean sortingActive = false;

    private void showSortByViews(boolean show) {
        if (show) {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.sorting));
            mainBinding.rightDrawer.sortOptions.sortingLayout.setVisibility(View.VISIBLE);
            sortingActive = true;
        } else {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
            mainBinding.rightDrawer.sortOptions.sortingLayout.setVisibility(View.GONE);
            sortingActive = false;
        }

        baseShowSecondFiltersView(show);
    }

    boolean standardFiltersActive = false;

    private void showStandardFiltersView(boolean show) {
        if (show) {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.standard_filters));
            mainBinding.rightDrawer.flStandardFilters.setVisibility(View.VISIBLE);
            standardFiltersActive = true;
        } else {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
            mainBinding.rightDrawer.flStandardFilters.setVisibility(View.GONE);
            standardFiltersActive = false;
        }

        baseShowSecondFiltersView(show);
    }

    boolean dynamicFiltersActive = false;

    private void showDynamicFiltersView(boolean show) {
        if (show) {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.dynamic_filters));
            mainBinding.rightDrawer.flDynamicFilters.setVisibility(View.VISIBLE);
            dynamicFiltersActive = true;
        } else {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
            mainBinding.rightDrawer.flDynamicFilters.setVisibility(View.GONE);
            dynamicFiltersActive = false;
        }

        baseShowSecondFiltersView(show);
    }

    private void baseShowSecondFiltersView(boolean show) {
        if (show) {
            mainBinding.rightDrawer.rightDrawerSort.setVisibility(View.GONE);
            mainBinding.rightDrawer.drawerBackButton.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.rightDrawerFilters.setVisibility(View.GONE);
            hideSortingViews(true);
            hideWorkflowTypeFilters(true);
            hideBaseFilters(true);
            hideStatusFilters(true);
            hideSystemStatusFilters(true);
            hideStandardFilters(true);
            hideDynamicFilters(true);
            hideClearFilters(true);
        } else {
            mainBinding.rightDrawer.rightDrawerSort.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.drawerBackButton.setVisibility(View.GONE);
            hideSortingViews(false);
            hideWorkflowTypeFilters(false);
            hideBaseFilters(false);
            hideStatusFilters(false);
            hideSystemStatusFilters(false);
            hideStandardFilters(false);
            hideDynamicFilters(false);
            hideClearFilters(false);
        }
    }

    private void updateSortFieldSelection(@StringRes int resLabel) {
        TextView textView = mainBinding.rightDrawer.rightDrawerSortSelection;
        if (resLabel == R.string.no_selection) {
            textView.setText(getString(R.string.no_selection));
            textView.setTextColor(getResources().getColor(R.color.dark_gray));
        } else {
            textView.setText(getString(resLabel));
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void toggleRadioButtonFilter(int radioType, boolean check) {
        switch (radioType) {
            case RADIO_NUMBER:
                mainBinding.rightDrawer.sortOptions.chbxWorkflownumber.setChecked(check);
                break;
            case RADIO_CREATED_DATE:
                mainBinding.rightDrawer.sortOptions.chbxCreatedate.setChecked(check);
                break;
            case RADIO_UPDATED_DATE:
                mainBinding.rightDrawer.sortOptions.chbxUpdatedate.setChecked(check);
                break;
            case RADIO_CLEAR_ALL:
                mainBinding.rightDrawer.sortOptions.radioGroupSortBy.clearCheck();
            default:
                Log.d(TAG,
                        "toggleRadioButtonFilter: Trying to perform toggle on unknown radio button");
                break;
        }
    }

    // TODO refactor name to setDrawerSortBy
    @Deprecated
    private void setFilterBoxListeners() {
        // radio button listeners
        mainBinding.rightDrawer.sortOptions.chbxWorkflownumber
                .setOnClickListener(this::onRadioButtonClicked);
        mainBinding.rightDrawer.sortOptions.chbxCreatedate
                .setOnClickListener(this::onRadioButtonClicked);
        mainBinding.rightDrawer.sortOptions.chbxUpdatedate
                .setOnClickListener(this::onRadioButtonClicked);

        // ascending / descending listeners

        mainBinding.rightDrawer.sortOptions.swchWorkflownumber.setOnClickListener(view -> {
            Switch aSwitch = ((Switch) view);
            boolean isChecked = aSwitch.isChecked();
            viewModel.handleSwitchOnClick(RADIO_NUMBER, Sort.sortType.BYNUMBER, isChecked);
            setSwitchAscendingDescendingText(mainBinding.rightDrawer.sortOptions.swchWorkflownumber,
                    isChecked);
        });
        mainBinding.rightDrawer.sortOptions.swchCreatedate.setOnClickListener(view -> {
            Switch aSwitch = ((Switch) view);
            boolean isChecked = aSwitch.isChecked();
            viewModel.handleSwitchOnClick(RADIO_CREATED_DATE, Sort.sortType.BYCREATE, isChecked);
            setSwitchAscendingDescendingText(mainBinding.rightDrawer.sortOptions.swchCreatedate,
                    isChecked);
        });
        mainBinding.rightDrawer.sortOptions.swchUpdatedate.setOnClickListener(view -> {
            Switch aSwitch = ((Switch) view);
            boolean isChecked = aSwitch.isChecked();
            viewModel.handleSwitchOnClick(RADIO_UPDATED_DATE, Sort.sortType.BYUPDATE, isChecked);
            setSwitchAscendingDescendingText(mainBinding.rightDrawer.sortOptions.swchUpdatedate,
                    isChecked);
        });
    }

    private void toggleAscendingDescendingSwitch(int switchType, boolean check) {
        switch (switchType) {
            case SWITCH_NUMBER:
                mainBinding.rightDrawer.sortOptions.swchWorkflownumber.setChecked(check);
                setSwitchAscendingDescendingText(
                        mainBinding.rightDrawer.sortOptions.swchWorkflownumber, check);
                break;
            case SWITCH_CREATED_DATE:
                mainBinding.rightDrawer.sortOptions.swchCreatedate.setChecked(check);
                setSwitchAscendingDescendingText(mainBinding.rightDrawer.sortOptions.swchCreatedate,
                        check);
                break;
            case SWITCH_UPDATED_DATE:
                mainBinding.rightDrawer.sortOptions.swchUpdatedate.setChecked(check);
                setSwitchAscendingDescendingText(mainBinding.rightDrawer.sortOptions.swchUpdatedate,
                        check);
                break;
            case SWITCH_CLEAR_ALL:
                toggleAscendingDescendingSwitch(SWITCH_NUMBER, check);
                toggleAscendingDescendingSwitch(SWITCH_CREATED_DATE, check);
                toggleAscendingDescendingSwitch(SWITCH_UPDATED_DATE, check);
                break;
            default:
                Log.d(TAG,
                        "toggleAscendingDescendingSwitch: Trying to perform a toggle and there is no related Switch object");
                break;
        }
    }

    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        viewModel.handleRadioButtonClicked(checked, view.getId());
    }

    private void setSwitchAscendingDescendingText(Switch switchType, boolean check) {
        if (check) {
            switchType.setText(getString(R.string.ascending));
        } else {
            switchType.setText(getString(R.string.descending));
        }
    }

    private void setActionBar() {
        setSupportActionBar(mainBinding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainBinding.drawerLayout, mainBinding.toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainBinding.drawerLayout.addDrawerListener(toggle);

        //disable drawer gestures for the right drawer,
        //in order to prevent the filters drawer to be opened from any activity by the user
        mainBinding.drawerLayout
                .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        String versionName = BuildConfig.VERSION_NAME;
        mainBinding.leftDrawer.tvVersionName.setText(versionName);

        toggle.syncState();
    }

    private void imgClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_avatar, popup.getMenu());
        popup.setOnMenuItemClickListener(MainActivity.this);
        popup.show();
    }

    private void drawerClicks(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.nav_workflows:
                mainBinding.bottomNavigation.setSelectedItemId(R.id.menu_workflow_list);
                showFragment(WorkflowFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_resourcing:
                startActivity(new Intent(this, ResourcingPlannerActivity.class));
                break;
            case R.id.nav_sync:
                startActivity(new Intent(this, SyncActivity.class));
                finishAffinity();
                break;
            case R.id.nav_profile:
                showFragment(ProfileFragment.newInstance(), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_login_qr:
                startActivity(new Intent(this, QRTokenActivity.class));
                break;
            case R.id.nav_exit:
                showLogoutDialog();
                break;
        }
    }

    private void showLogoutDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this,
                R.style.AlertDialogTheme);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirmation);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> logout());
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void logout() {
        SharedPreferences sharedPref = getSharedPreferences("Sessions",
                Context.MODE_PRIVATE);

        Utils.logout(sharedPref);

        startActivity(new Intent(MainActivity.this, DomainActivity.class));
        // close splash activity
        finish();
    }

    private void goToDomain(Boolean open) {
        startActivity(new Intent(MainActivity.this, DomainActivity.class));
        finishAffinity();
    }

    private void attemptToLogin() {
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String user = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        viewModel.attemptLogin(user, password);
    }

    private void saveInPreferences(String key, String content) {
        SharedPreferences sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        sharedPref.edit().putString(key, content).apply();
    }

    protected void collapseActionView(Boolean collapse) {
        if (mSearch != null) {
            mSearch.collapseActionView();
        }
    }

    protected void hideKeyboard(Boolean hide) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        // verify if the soft keyboard is open
        if (!imm.isAcceptingText()) {
            return;
        }
        View view = getCurrentFocus();
        if (view == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void goToWorkflowDetail(String workflowId) {
        Intent intent = new Intent(this, WorkflowDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(WorkflowDetailActivity.INTENT_EXTRA_ID, workflowId);
        startActivity(intent);
    }

    // Populates Filters List
    private void setRightDrawerFilters(List<WorkflowTypeMenu> menus) {
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.GONE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
        hideSortingViews(false);
        hideWorkflowTypeFilters(false);
        hideBaseFilters(false);
        hideStatusFilters(false);
        hideSystemStatusFilters(false);
        hideStandardFilters(false);
        hideDynamicFilters(false);
        hideClearFilters(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        rightDrawerFiltersAdapter = new RightDrawerFiltersAdapter(inflater, menus);

        mainBinding.rightDrawer.rightDrawerFilters
                .setOnItemClickListener((parent, view, position, id) -> {
                    // Clicks on Filter List
                    viewModel.sendFilterClickToWorflowList(position);
                });
        mainBinding.rightDrawer.rightDrawerFilters.setAdapter(rightDrawerFiltersAdapter);
    }

    // Populates Options List
    private void setRightDrawerOptions(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG, "setRightDrawerOptions: Not able to set Drawer Options. OptionList is NULL");
            return;
        }

        prepareUIForOptionList(optionsList.titleLabel);
        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            // Clicks on Option List
            updateViewSelected(view); // adds check mark UI.
            viewModel.sendOptionSelectedToWorkflowList(position);
        };
        setRightDrawerOptionsAdapter(optionsList.optionsList, listener);
    }

    // Populates Options List for WorkflowType Filters.
    private void setRightDrawerWorkflowTypeFilters(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG, "setRightDrawerOptions: Not able to set Drawer Options. OptionList is NULL");
            return;
        }

        if (TextUtils.isEmpty(optionsList.titleLabel)) {
            prepareUIForOptionList(getString(optionsList.titleLabelRes));
        } else {
            prepareUIForOptionList(optionsList.titleLabel);
        }

        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            // Clicks on Option List
            viewModel.sendWorkflowTypeFilterPositionClicked(position);
        };
        setRightDrawerOptionsAdapter(optionsList.optionsList, listener);
    }

    // Populates Options List for Base Filters.
    private void setRightDrawerBaseFilters(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG, "setRightDrawerOptions: Not able to set Drawer Options. OptionList is NULL");
            return;
        }

        if (TextUtils.isEmpty(optionsList.titleLabel)) {
            prepareUIForOptionList(getString(optionsList.titleLabelRes));
        } else {
            prepareUIForOptionList(optionsList.titleLabel);
        }

        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            // Clicks on Option List
            viewModel.sendBaseFilterPositionClicked(position);
        };
        setRightDrawerOptionsAdapter(optionsList.optionsList, listener);
    }

    private void setRightDrawerStatusFilters(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG,
                    "setRightDrawerStatusFilters: Not able to set Drawer Options. OptionList is NULL");
            return;
        }

        if (TextUtils.isEmpty(optionsList.titleLabel)) {
            prepareUIForOptionList(getString(optionsList.titleLabelRes));
        } else {
            prepareUIForOptionList(optionsList.titleLabel);
        }

        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            // Clicks on Option List
            viewModel.sendStatusFilterPositionClicked(position);
        };
        setRightDrawerOptionsAdapter(optionsList.optionsList, listener);
    }

    private void setRightDrawerSystemStatusFilters(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG,
                    "setRightDrawerSystemStatusFilters: Not able to set Drawer Options. OptionList is NULL");
            return;
        }

        if (TextUtils.isEmpty(optionsList.titleLabel)) {
            prepareUIForOptionList(getString(optionsList.titleLabelRes));
        } else {
            prepareUIForOptionList(optionsList.titleLabel);
        }

        AdapterView.OnItemClickListener listener = (parent, view, position, id) -> {
            // Clicks on Option List
            viewModel.sendSystemStatusFilterPositionClicked(position);
        };
        setRightDrawerOptionsAdapter(optionsList.optionsList, listener);
    }

    private void setRightDrawerOptionsAdapter(List<WorkflowTypeMenu> optionsList,
                                              AdapterView.OnItemClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(this);
        rightDrawerOptionsAdapter = new RightDrawerOptionsAdapter(inflater, optionsList);
        mainBinding.rightDrawer.rightDrawerFilters.setOnItemClickListener(listener);
        mainBinding.rightDrawer.rightDrawerFilters.setAdapter(rightDrawerOptionsAdapter);
        mainBinding.rightDrawer.rightDrawerFilters.setVisibility(View.VISIBLE);
    }

    private void prepareUIForOptionList(String title) {
        hideSortingViews(true);
        hideWorkflowTypeFilters(true);
        hideBaseFilters(true);
        hideStatusFilters(true);
        hideSystemStatusFilters(true);
        hideStandardFilters(true);
        hideDynamicFilters(true);
        hideClearFilters(true);
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.VISIBLE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(title);
    }

    private void handleUpdateWorkflowTypeFilterSelectionUpdateWith(String label) {
        mainBinding.rightDrawer.rightDrawerWorkflowTypeSubtitle.setText(label);
    }

    private void handleUpdateBaseFilterSelectionUpdateWith(@StringRes int resLabel) {
        mainBinding.rightDrawer.rightDrawerBaseSubtitle.setText(resLabel);
    }

    private void handleUpdateStatusFilterSelectionUpdateWith(@StringRes int resLabel) {
        mainBinding.rightDrawer.rightDrawerStatusSubtitle.setText(resLabel);
    }

    private void handleUpdateSystemStatusFilterSelectionUpdateWith(@StringRes int resLabel) {
        mainBinding.rightDrawer.rightDrawerSystemStatusSubtitle.setText(resLabel);
    }

    private void invalidateOptionList() {
        if (rightDrawerOptionsAdapter == null) {
            return;
        }
        rightDrawerOptionsAdapter.notifyDataSetChanged();
    }

    private void updateViewSelected(View view) {
        ImageView checkMark = view.findViewById(R.id.right_drawer_image_checkmark);
        TextView title = view.findViewById(R.id.right_drawer_item_title);
        int visibility = checkMark.getVisibility();
        if (visibility == View.VISIBLE) {
            checkMark.setVisibility(View.GONE);
            title.setTextColor(getResources().getColor(R.color.black));
        } else {
            checkMark.setVisibility(View.VISIBLE);
            title.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void hideWorkflowTypeFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerWorkflowType.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorWorkflowType.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerWorkflowType.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorWorkflowType.setVisibility(View.VISIBLE);
        }
    }

    private void hideBaseFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerBaseFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorBase.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerBaseFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorBase.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatusFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerStatusFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorStatus.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerStatusFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorStatus.setVisibility(View.VISIBLE);
        }
    }

    private void hideSystemStatusFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerSystemStatusFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorSystemStatus.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerSystemStatusFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorSystemStatus.setVisibility(View.VISIBLE);
        }
    }

    private void hideStandardFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerStandardFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorStandard.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerStandardFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorStandard.setVisibility(View.VISIBLE);
        }
    }

    private void hideDynamicFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerDynamicFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorDynamic.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerDynamicFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorDynamic.setVisibility(View.VISIBLE);
        }
    }

    private void hideClearFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerRestoreDefaults.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerRestoreDefaults.setVisibility(View.VISIBLE);
        }
    }

    private void hideSortingViews(boolean hide) {
        TextView sortTitle = mainBinding.rightDrawer.rightDrawerSortBy;
        TextView sortSubtitle = mainBinding.rightDrawer.rightDrawerSortSelection;

        if (hide) {
            sortTitle.setVisibility(View.GONE);
            sortSubtitle.setVisibility(View.GONE);
            mainBinding.rightDrawer.separatorSortBy.setVisibility(View.GONE);
        } else {
            sortTitle.setVisibility(View.VISIBLE);
            sortSubtitle.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.separatorSortBy.setVisibility(View.VISIBLE);
        }
    }

    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    private void subscribe() {
        subscribeForLogin();
        final Observer<Integer> errorObserver = ((Integer data) -> {
            // TODO handle error when we cant find Users, workflowlike and workflow
        });

        final Observer<Integer> setSearchMenuObserver = (layoutId -> {

        });

        final Observer<int[]> toggleRadioButtonObserver = (toggle -> {
            if (toggle == null || toggle.length < 1) {
                return;
            }
            boolean check = toggle[INDEX_CHECK] == CHECK;
            toggleRadioButtonFilter(toggle[INDEX_TYPE], check);
        });

        final Observer<int[]> toggleSwitchObserver = (toggle -> {
            if (toggle == null || toggle.length < 1) {
                return;
            }
            boolean check = toggle[INDEX_CHECK] == CHECK;
            toggleAscendingDescendingSwitch(toggle[INDEX_TYPE], check);
        });

        final Observer<String[]> setImgInViewObserver = (this::setImageIn);
        final Observer<Boolean> collapseMenuObserver = (this::collapseActionView);
        final Observer<Boolean> hideKeyboardObserver = (this::hideKeyboard);
        viewModel.getObservableError().observe(this, errorObserver);
        viewModel.getObservableSetImgInView().observe(this, setImgInViewObserver);
        viewModel.getObservableCollapseMenu().observe(this, collapseMenuObserver);
        viewModel.getObservableHideKeyboard().observe(this, hideKeyboardObserver);
        viewModel.setRightDrawerFilterList.observe(this, (this::setRightDrawerFilters));
        viewModel.setRightDrawerOptionList.observe(this, (this::setRightDrawerOptions));
        viewModel.invalidateOptionsList.observe(this, invalidate -> invalidateOptionList());
        viewModel.receiveMessageToggleRadioButton.observe(this, toggleRadioButtonObserver);
        viewModel.receiveMessageToggleSwitch.observe(this, toggleSwitchObserver);
        viewModel.receiveMessageUpdateSortSelected.observe(this, this::updateSortFieldSelection);
        viewModel.receiveMessageCreateWorkflowTypeFiltersAdapter
                .observe(this, this::setRightDrawerWorkflowTypeFilters);
        viewModel.receiveMessageCreateBaseFiltersAdapter
                .observe(this, this::setRightDrawerBaseFilters);
        viewModel.receiveMessageCreateStatusFiltersAdapter
                .observe(this, this::setRightDrawerStatusFilters);
        viewModel.receiveMessageCreateSystemStatusFiltersAdapter
                .observe(this, this::setRightDrawerSystemStatusFilters);
        viewModel.receiveMessageWorkflowTypeIdFilterSelected
                .observe(this, this::sendMessageGenerateWorkflowFieldsByType);
        viewModel.receiveMessageWorkflowTypeFilterSelected
                .observe(this, this::handleUpdateWorkflowTypeFilterSelectionUpdateWith);
        viewModel.receiveMessageBaseFilterSelected
                .observe(this, this::handleUpdateBaseFilterSelectionUpdateWith);
        viewModel.receiveMessageStatusFilterSelected
                .observe(this, this::handleUpdateStatusFilterSelectionUpdateWith);
        viewModel.receiveMessageSystemStatusFilterSelected
                .observe(this, this::handleUpdateSystemStatusFilterSelectionUpdateWith);
        viewModel.openRightDrawer.observe(this, this::openRightDrawer);

        viewModel.getObservableQuickActionsVisibility().observe(this, this::setupSpeedDialFab);
        viewModel.getObservableShowLoading().observe(this, this::showLoading);
    }

    private void subscribeForLogin() {
        final Observer<Boolean> attemptTokenRefreshObserver = (response -> attemptToLogin());
        final Observer<String> saveToPreferenceObserver = (content -> saveInPreferences("token",
                content));
        final Observer<Boolean> goToDomainObserver = (this::goToDomain);
        viewModel.getObservableAttemptTokenRefresh().observe(this, attemptTokenRefreshObserver);
        viewModel.getObservableSaveToPreference().observe(this, saveToPreferenceObserver);
        viewModel.getObservableGoToDomain().observe(this, goToDomainObserver);
    }

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view == null) view = mainBinding.container;

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @UiThread
    private void sendMessageGenerateWorkflowFieldsByType(int workflowTypeId) {
        mDynamicFiltersFragment.handleWorkflowTypeIdUpdateForFilters(workflowTypeId);
        mStandardFiltersFragment.updateStandardFilterFieldTagsUsing(workflowTypeId);
    }

    @Override
    public void onValuesSelected(List<BaseFormItem> baseFormItems) {
        viewModel.onValuesSelected(baseFormItems);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }
}