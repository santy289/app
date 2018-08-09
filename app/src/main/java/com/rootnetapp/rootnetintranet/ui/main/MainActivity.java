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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.databinding.ActivityMainBinding;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.services.manager.WorkflowManagerService;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.main.adapters.SearchAdapter;
import com.rootnetapp.rootnetintranet.ui.manager.WorkflowManagerFragment;
import com.rootnetapp.rootnetintranet.ui.profile.ProfileFragment;
import com.rootnetapp.rootnetintranet.ui.timeline.TimelineFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowlist.WorkflowFragment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.auth0.android.jwt.JWT;

import java.io.IOException;

import javax.inject.Inject;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, profileViewModelFactory)
                .get(MainActivityViewModel.class);
        setSupportActionBar(mainBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainBinding.drawerLayout, mainBinding.toolbar
                , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        fragmentManager = getSupportFragmentManager();
        mainBinding.navView.setCheckedItem(R.id.nav_timeline);
        ClientResponse domain;
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        //todo Preguntar como implementar SharesPreferencesModule en los Viewmodels para cada tipo de clase guardada
        sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String json = sharedPref.getString("domain", "");

        //todo cambiar por consulta al viewmodel
        if (json.isEmpty()) {
            Log.d("test", "onCreate: ALGO PASO");//todo mejorar esta validacion
        } else {
            try {
                domain = jsonAdapter.fromJson(json);
                Utils.domain = "https://" + domain.getClient().getApiUrl();
                Utils.imgDomain = "http://" + domain.getClient().getApiUrl();
                Utils.imgDomain = Utils.imgDomain.replace("/v1", "");

                Glide.with(this).load(Utils.URL + domain.getClient().getLogoUrl()).into(mainBinding.imgLogo);
                Glide.with(this).load(Utils.URL + domain.getClient().getLogoUrl()).into(mainBinding.toolbarLogo);
                RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
                //todo solo para PRUEBAS
                //RetrofitUrlManager.getInstance().putDomain("localhost", "http://192.168.42.183/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent MyIntentService = new Intent(this, WorkflowManagerService.class);
        startService(MyIntentService);
        mainBinding.navTimeline.setOnClickListener(this::drawerClicks);
        mainBinding.navWorkflows.setOnClickListener(this::drawerClicks);
        mainBinding.navWorkflowmanager.setOnClickListener(this::drawerClicks);
        mainBinding.navProfile.setOnClickListener(this::drawerClicks);
        mainBinding.buttonWorkflow.setOnClickListener(this::drawerClicks);
        mainBinding.navExit.setOnClickListener(this::drawerClicks);

        //mainBinding.toolbarImage.setOnClickListener(this::imgClick);

        showFragment(TimelineFragment.newInstance(this), false);
        subscribe();
        String token = sharedPref.getString("token", "");
        JWT jwt = new JWT(token);
        id = Integer.parseInt(jwt.getClaim("profile_id").asString());
        viewModel.getUser(id);

        String workflowId = getIntent().getStringExtra("goToWorkflow");

        // If id is defined, then this activity was launched with a fragment selection
        if (workflowId != null) {
            viewModel.getWorkflow(Integer.parseInt(workflowId));
        }

    }

    private void imgClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_avatar, popup.getMenu());
        popup.setOnMenuItemClickListener(MainActivity.this);
        popup.show();
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
                if (mainBinding.expansionWorkflow.getVisibility() == View.GONE) {
                    mainBinding.arrow1.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    mainBinding.expansionWorkflow.setVisibility(View.VISIBLE);
                } else {
                    mainBinding.arrow1.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    mainBinding.expansionWorkflow.setVisibility(View.GONE);
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

    private void subscribe() {

        final Observer<User> userObserver = ((User data) -> {
            if (null != data) {
                String path = Utils.imgDomain + data.getPicture().trim();
                Glide.with(this).load(path).into(mainBinding.toolbarImage);
            }
        });
        final Observer<Workflow> workflowObserver = ((Workflow data) -> {
            if (null != data) {
                if(mSearch != null){
                    mSearch.collapseActionView();
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                showFragment(WorkflowDetailFragment.newInstance(data,
                        this), true);
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            if (null != data) {
                viewModel.getUser(id);
            }
        });
        viewModel.getObservableUser().observe(this, userObserver);
        viewModel.getObservableWorkflow().observe(this, workflowObserver);
        viewModel.getObservableError().observe(this, errorObserver);
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

}