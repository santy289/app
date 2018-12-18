package com.rootnetapp.rootnetintranet.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.ActivityMainBinding;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.services.background.WorkflowManagerService;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.main.adapters.SearchAdapter;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickAction;
import com.rootnetapp.rootnetintranet.ui.quickactions.QuickActionsActivity;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.Sort;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerOptionsAdapter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
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
import okhttp3.OkHttpClient;

import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_CHECK;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.INDEX_TYPE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CLEAR_ALL;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.RADIO_UPDATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_CREATED_DATE;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_NUMBER;
import static com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment.SWITCH_UPDATED_DATE;

public class MainActivity extends AppCompatActivity
        implements MainActivityInterface, PopupMenu.OnMenuItemClickListener {

    @Inject
    MainActivityViewModelFactory profileViewModelFactory;
    MainActivityViewModel viewModel;
    private ActivityMainBinding mainBinding;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private MenuItem mSearch = null;

    private OkHttpClient client;

    RightDrawerOptionsAdapter rightDrawerOptionsAdapter;
    RightDrawerFiltersAdapter rightDrawerFiltersAdapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        mainBinding.navView.setCheckedItem(R.id.nav_timeline);
        viewModel = ViewModelProviders
                .of(this, profileViewModelFactory)
                .get(MainActivityViewModel.class);
        fragmentManager = getSupportFragmentManager();
        setActionBar();
        subscribe();
        initActionListeners();
        sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        viewModel.initMainViewModel(sharedPref);

        String workflowId = getIntent().getStringExtra("goToWorkflow");
        // If id is defined, then this activity was launched with a fragment selection
        if (workflowId != null) {
            viewModel.getWorkflow(Integer.parseInt(workflowId));
        }

        showFragment(TimelineFragment.newInstance(this), false);
        startBackgroundWorkflowRequest();
        setFilterBoxListeners();
        setupBottomNavigation();
        setupSpeedDialFab();



        String protocol = sharedPref.getString(PreferenceKeys.PREF_PROTOCOL, "");
        if (TextUtils.isEmpty(protocol)) {
            return;
        }
        initNotifications();
    }

    private void initNotifications() {
        String protocol = sharedPref.getString(PreferenceKeys.PREF_PROTOCOL, "");
        String port = sharedPref.getString(PreferenceKeys.PREF_PORT, "");
        String token = sharedPref.getString(PreferenceKeys.PREF_TOKEN, "");
//
//
//        // Create a session object
//        Session session = new Session();
//        // Add all onJoin listeners
//        session.addOnJoinListener(this::subscribeToWebsocket);
//
//        String domain;
//        try {
//            domain = getDomainName(Utils.domain);
//        } catch (URISyntaxException e) {
//            Log.d(TAG, "initNotifications: Missing websocket settings");
//            return;
//        }
//
//        String url = protocol + "://" + domain + ":" + port + "/";
//        String realm = "master";
//
////        ArrayMap<String, Object> extra = new ArrayMap<>();
////        extra.put("jwt", token);
//
//
////         finally, provide everything to a Client and connect
//        IAuthenticator authenticator = new ChallengeResponseAuth("", "");
//        Client client = new Client(session, url, realm, authenticator);
//
////        Client client = new Client(session, url, realm);
//
//
//        CompletableFuture<ExitInfo> exitInfoCompletableFuture = client.connect();
    }
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

//    public void subscribeToWebsocket(Session session, SessionDetails details) {
        // Subscribe to topic to receive its events.
//        CompletableFuture<Subscription> subFuture = session.subscribe("master.notification",
//                this::onEvent);
//        subFuture.whenComplete((subscription, throwable) -> {
//            if (throwable == null) {
//                // We have successfully subscribed.
//                System.out.println("Subscribed to topic " + subscription.topic);
//            } else {
//                // Something went bad.
//                throwable.printStackTrace();
//            }
//        });
//    }

//    private void onEvent(List<Object> args, Map<String, Object> kwargs, EventDetails details) {
//        System.out.println(String.format("Got event: %s", args.get(0)));
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(getString(R.string.search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    viewModel.getWorkflowsLike("%" + newText + "%");
                }
                return true;
            }
        });

        final Observer<Cursor> workflowsObserver = ((Cursor data) -> {
            if (null != data) {
                mSearchView.setSuggestionsAdapter(new SearchAdapter(this, data, this));
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            }
        });

        final Observer<Integer> wfErrorObserver = ((Integer data) -> {
            if (null != data) {
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getObservableWorkflowError().observe(this, wfErrorObserver);
        viewModel.getObservableWorkflows().observe(this, workflowsObserver);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                showFragment(ProfileFragment.newInstance(this), false);
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
    }

    @Override
    public void onBackPressed() {
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

    protected void setImageIn(String[] content) {
        RequestBuilder builder = Glide.with(this).load(content[1]);
        switch (content[0]) {
            case MainActivityViewModel.IMG_LOGO:
                builder.into(mainBinding.leftDrawer.imgLogo);
                return;
            case MainActivityViewModel.IMG_BAR_LOGO:
                builder.into(mainBinding.toolbarLogo);
                return;
            case MainActivityViewModel.IMG_TOOLBAR:
                builder.into(mainBinding.toolbarImage);
        }
    }

    private void setupBottomNavigation() {
        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                item -> {
                    handleBottomNavigationSelection(item);
                    return false;
                });
    }

    private void handleBottomNavigationSelection(@NonNull MenuItem item) {
        item.setChecked(true);

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

    private void setupSpeedDialFab() {
        //reverse order
        addActionItem(R.id.fab_comment, R.string.quick_actions_comment,
                R.drawable.ic_message_black_24dp);
        addActionItem(R.id.fab_change_status, R.string.quick_actions_change_status,
                R.drawable.ic_compare_arrows_black_24dp);
        addActionItem(R.id.fab_approve_workflow, R.string.quick_actions_approve_workflow,
                R.drawable.ic_like_black_24dp);
        addActionItem(R.id.fab_edit_workflow, R.string.quick_actions_edit_workflow,
                R.drawable.ic_workflow_black_24dp);

        mainBinding.fabSpeedDial.setOnActionSelectedListener(this::handleSpeedDialClick);
        mainBinding.fabSpeedDial.getMainFab().setSupportImageTintList(ColorStateList.valueOf(
                Color.WHITE)); //this is the only way to change the icon color
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
        mainBinding.leftDrawer.navProfile.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navExit.setOnClickListener(this::drawerClicks);
        mainBinding.rightDrawer.drawerBackButton.setOnClickListener(view -> {
            if (sortingActive) {
                showSortByViews(false);
            }
            viewModel.sendRightDrawerBackButtonClick();
        });

        mainBinding.rightDrawer.rightDrawerSort.setOnClickListener(view -> {
            // TODO tell WorkflowViewModel to show next Sort By Views
            // right now main activity is doing it on its own, it is better that the viewModel does this.
            showSortByViews(true);
        });

        // Using the base field filter.
        mainBinding.rightDrawer.rightDrawerBaseFilters.setOnClickListener(view -> {
            viewModel.sendBaseFiltersClicked();
        });
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
            mainBinding.rightDrawer.rightDrawerFilters.setVisibility(View.GONE);
            mainBinding.rightDrawer.rightDrawerSort.setVisibility(View.GONE);
            mainBinding.rightDrawer.drawerBackButton.setVisibility(View.VISIBLE);
            hideBaseFilters(true);
            hideTitleDynamicFilters(true);
            sortingActive = true;
        } else {
            mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
            mainBinding.rightDrawer.sortOptions.sortingLayout.setVisibility(View.GONE);
            mainBinding.rightDrawer.rightDrawerFilters.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.rightDrawerSort.setVisibility(View.VISIBLE);
            mainBinding.rightDrawer.drawerBackButton.setVisibility(View.GONE);
            hideBaseFilters(false);
            hideTitleDynamicFilters(false);
            sortingActive = false;
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

    private void startBackgroundWorkflowRequest() {
        Intent MyIntentService = new Intent(this, WorkflowManagerService.class);
        startService(MyIntentService);
    }

    private void setActionBar() {
        setSupportActionBar(mainBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainBinding.drawerLayout, mainBinding.toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainBinding.drawerLayout.addDrawerListener(toggle);
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
            case R.id.nav_profile: {
                showFragment(ProfileFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_exit: {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username", "").apply();
                editor.putString("password", "").apply();
                startActivity(new Intent(MainActivity.this, DomainActivity.class));
                // close splash activity
                finish();
                break;
            }
        }
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

    protected void goToWorkflowDetail(Workflow workflow) {
//        showFragment(
//                WorkflowDetailFragment.newInstance(workflow, this),
//                true
//        );
    }

    // Populates Filters List
    private void setRightDrawerFilters(List<WorkflowTypeMenu> menus) {
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.GONE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
        hideSortingViews(false);
        hideBaseFilters(false);
        hideTitleDynamicFilters(false);
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

    private void setRightDrawerOptionsAdapter(List<WorkflowTypeMenu> optionsList,
                                              AdapterView.OnItemClickListener listener) {
        LayoutInflater inflater = LayoutInflater.from(this);
        rightDrawerOptionsAdapter = new RightDrawerOptionsAdapter(inflater, optionsList);
        mainBinding.rightDrawer.rightDrawerFilters.setOnItemClickListener(listener);
        mainBinding.rightDrawer.rightDrawerFilters.setAdapter(rightDrawerOptionsAdapter);
    }

    private void prepareUIForOptionList(String title) {
        hideSortingViews(true);
        hideBaseFilters(true);
        hideTitleDynamicFilters(true);
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.VISIBLE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(title);
    }

    private void handleUpdateBaseFilterSelectionUpdateWith(@StringRes int resLabel) {
        mainBinding.rightDrawer.rightDrawerBaseSubtitle.setText(resLabel);
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

    private void hideBaseFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.rightDrawerBaseFilters.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.rightDrawerBaseFilters.setVisibility(View.VISIBLE);
        }
    }

    private void hideTitleDynamicFilters(boolean hide) {
        if (hide) {
            mainBinding.rightDrawer.titleDynamicField.setVisibility(View.GONE);
        } else {
            mainBinding.rightDrawer.titleDynamicField.setVisibility(View.VISIBLE);
        }
    }

    private void hideSortingViews(boolean hide) {
        TextView sortTitle = mainBinding.rightDrawer.rightDrawerSortBy;
        TextView sortSubtitle = mainBinding.rightDrawer.rightDrawerSortSelection;

        if (hide) {
            sortTitle.setVisibility(View.GONE);
            sortSubtitle.setVisibility(View.GONE);
        } else {
            sortTitle.setVisibility(View.VISIBLE);
            sortSubtitle.setVisibility(View.VISIBLE);
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
        final Observer<Workflow> goToWorkflowDetailObserver = (this::goToWorkflowDetail);
        viewModel.getObservableError().observe(this, errorObserver);
        viewModel.getObservableSetImgInView().observe(this, setImgInViewObserver);
        viewModel.getObservableCollapseMenu().observe(this, collapseMenuObserver);
        viewModel.getObservableHideKeyboard().observe(this, hideKeyboardObserver);
        viewModel.getObservableGoToWorkflowDetail().observe(this, goToWorkflowDetailObserver);
        viewModel.setRightDrawerFilterList.observe(this, (this::setRightDrawerFilters));
        viewModel.setRightDrawerOptionList.observe(this, (this::setRightDrawerOptions));
        viewModel.invalidateOptionsList.observe(this, invalidate -> invalidateOptionList());
        viewModel.receiveMessageToggleRadioButton.observe(this, toggleRadioButtonObserver);
        viewModel.receiveMessageToggleSwitch.observe(this, toggleSwitchObserver);
        viewModel.receiveMessageUpdateSortSelected.observe(this, this::updateSortFieldSelection);
        viewModel.receiveMessageCreateBaseFiltersAdapter
                .observe(this, this::setRightDrawerBaseFilters);
        viewModel.receiveMessageBaseFilterSelected
                .observe(this, this::handleUpdateBaseFilterSelectionUpdateWith);
        viewModel.openRightDrawer.observe(this, this::openRightDrawer);
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

}