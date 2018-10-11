package com.rootnetapp.rootnetintranet.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.ActivityMainBinding;
import com.rootnetapp.rootnetintranet.models.workflowlist.OptionsList;
import com.rootnetapp.rootnetintranet.models.workflowlist.WorkflowTypeMenu;
import com.rootnetapp.rootnetintranet.services.manager.WorkflowManagerService;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.main.adapters.SearchAdapter;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerFiltersAdapter;
import com.rootnetapp.rootnetintranet.ui.workflowlist.adapters.RightDrawerOptionsAdapter;

import org.w3c.dom.Text;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements MainActivityInterface, PopupMenu.OnMenuItemClickListener {

    @Inject
    MainActivityViewModelFactory profileViewModelFactory;
    MainActivityViewModel viewModel;
    private ActivityMainBinding mainBinding;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;
    private int id;
    private MenuItem mSearch = null;

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

        // TODO debuging remove later
        startRightDrawerNavigation();
    }



    // TODO debugging remove later
    String firstPage[] = {"TEST 1", "TEST 2", "TEST 3"};
    String secondPage[] = {"TEST 4", "TEST 5", "TEST 6"};
    ArrayAdapter<String> firstAdapter;
    private void startRightDrawerNavigation() {
//        firstAdapter =
//                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, firstPage);
//
//
//        ListView listView = mainBinding.rightDrawer.rightDrawerFilters;
//        listView.setAdapter(firstAdapter);
//
//
//        listView.setOnItemClickListener((parentAdapter, view, position, id) -> {
//            Log.d(TAG, "startRightDrawerNavigation: ");
//
//            ArrayAdapter<String> secondAdapter =
//                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, secondPage);
//
//            mainBinding.rightDrawer.rightDrawerFilters.setAdapter(secondAdapter);
//
//        });
//
//        mainBinding.rightDrawer.drawerBackButton.setOnClickListener(view -> {
//
//            mainBinding.rightDrawer.rightDrawerFilters.setAdapter(firstAdapter);
//        });
    }



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
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
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

    private void initActionListeners() {
        mainBinding.leftDrawer.navTimeline.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navWorkflows.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navWorkflowmanager.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navProfile.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.buttonWorkflow.setOnClickListener(this::drawerClicks);
        mainBinding.leftDrawer.navExit.setOnClickListener(this::drawerClicks);
        mainBinding.rightDrawer.drawerBackButton.setOnClickListener(view -> {
            viewModel.sendRightDrawerBackButtonClick();
        });
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
            case R.id.nav_timeline: {
                showFragment(TimelineFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_workflows: {
                showFragment(WorkflowFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_profile: {
                showFragment(ProfileFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.nav_workflowmanager: {
                showFragment(WorkflowManagerFragment.newInstance(this), false);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.button_workflow: {
                if (mainBinding.leftDrawer.expansionWorkflow.getVisibility() == View.GONE) {
                    mainBinding.leftDrawer.arrow1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    mainBinding.leftDrawer.expansionWorkflow.setVisibility(View.VISIBLE);
                } else {
                    mainBinding.leftDrawer.arrow1.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    mainBinding.leftDrawer.expansionWorkflow.setVisibility(View.GONE);
                }
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
        if(mSearch != null){
            mSearch.collapseActionView();
        }
    }

    protected void hideKeyboard(Boolean hide) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        // verify if the soft keyboard is open
        if(!imm.isAcceptingText()) {
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

    private void setRightDrawerFilters(List<WorkflowTypeMenu> menus) {
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.GONE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(getString(R.string.filters));
        hideSortingViews(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        rightDrawerFiltersAdapter = new RightDrawerFiltersAdapter(inflater, menus);

        mainBinding.rightDrawer.rightDrawerFilters.setOnItemClickListener((parent, view, position, id) -> {
            // Clicks on Filter List
             viewModel.sendFilterClickToWorflowList(position);
        });
        mainBinding.rightDrawer.rightDrawerFilters.setAdapter(rightDrawerFiltersAdapter);
    }

    private void setRightDrawerOptions(OptionsList optionsList) {
        if (optionsList == null) {
            Log.d(TAG, "setRightDrawerOptions: Not able to set Drawer Options");
        }
        hideSortingViews(true);
        mainBinding.rightDrawer.drawerBackButton.setVisibility(View.VISIBLE);
        mainBinding.rightDrawer.rightDrawerTitle.setText(optionsList.titleLabel);
        LayoutInflater inflater = LayoutInflater.from(this);
        rightDrawerOptionsAdapter = new RightDrawerOptionsAdapter(inflater, optionsList.optionsList);
        mainBinding.rightDrawer.rightDrawerFilters.setOnItemClickListener((parent, view, position, id) -> {
            // Clicks on Option List
            updateViewSelected(view);
            viewModel.sendOptionSelectedToWorkflowList(position);

        });
        mainBinding.rightDrawer.rightDrawerFilters.setAdapter(rightDrawerOptionsAdapter);
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

    private void hideSortingViews(boolean hide) {
        TextView sortTitle =  mainBinding.rightDrawer.rightDrawerSortBy;
        TextView sortSubtitle = mainBinding.rightDrawer.rightDrawerSortSelection;

        if (hide) {
            sortTitle.setVisibility(View.GONE);
            sortSubtitle.setVisibility(View.GONE);
        } else {
            sortTitle.setVisibility(View.VISIBLE);
            sortSubtitle.setVisibility(View.VISIBLE);
        }
    }

    private void subscribe() {
        subscribeForLogin();
        final Observer<Integer> errorObserver = ((Integer data) -> {
            // TODO handle error when we cant find Users, workflowlike and workflow
        });

        final Observer<Integer> setSearchMenuObserver = ( layoutId -> {

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
    }

    private void subscribeForLogin() {
        final Observer<Boolean> attemptTokenRefreshObserver = (response -> attemptToLogin());
        final Observer<String> saveToPreferenceObserver = (content -> saveInPreferences("token", content));
        final Observer<Boolean> goToDomainObserver = (this::goToDomain);
        viewModel.getObservableAttemptTokenRefresh().observe(this, attemptTokenRefreshObserver);
        viewModel.getObservableSaveToPreference().observe(this, saveToPreferenceObserver);
        viewModel.getObservableGoToDomain().observe(this, goToDomainObserver);
    }


}