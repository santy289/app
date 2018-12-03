package com.rootnetapp.rootnetintranet.ui.quickactions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.ActivityQuickActionsBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.WorkflowSearchFragment;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class QuickActionsActivity extends AppCompatActivity implements QuickActionsInterface {

    public static final String EXTRA_ACTION = "Extra.Action";
    public static final String EXTRA_TITLE = "Extra.Title";

    private static final String TAG = "QuickActionsActivity";

    @Inject
    QuickActionsViewModelFactory quickActionsViewModelFactory;
    private QuickActionsViewModel quickActionsViewModel;
    private ActivityQuickActionsBinding mBinding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quick_actions);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        quickActionsViewModel = ViewModelProviders
                .of(this, quickActionsViewModelFactory)
                .get(QuickActionsViewModel.class);
        fragmentManager = getSupportFragmentManager();
        SharedPreferences sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();

        @QuickAction int action = getIntent()
                .getIntExtra(EXTRA_ACTION, QuickAction.APPROVE_WORKFLOW);

        setActionBar();
        showFragment(WorkflowSearchFragment.newInstance(this, action), false);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title == null || title.isEmpty()) title = getTitle().toString();
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showFragment(Fragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    @Override
    public void showActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}